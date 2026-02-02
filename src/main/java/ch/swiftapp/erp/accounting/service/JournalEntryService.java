package ch.swiftapp.erp.accounting.service;

import ch.swiftapp.erp.accounting.dto.*;
import ch.swiftapp.erp.accounting.event.JournalEntryPostedEvent;
import ch.swiftapp.erp.accounting.event.JournalEntryReversedEvent;
import ch.swiftapp.erp.accounting.model.JournalEntry;
import ch.swiftapp.erp.accounting.model.JournalEntryLine;
import ch.swiftapp.erp.accounting.repository.AccountRepository;
import ch.swiftapp.erp.accounting.repository.JournalEntryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for managing journal entries (Buchungssätze).
 *
 * <p>Enforces double-entry bookkeeping: total debits must equal total credits.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final AtomicLong SEQUENCE = new AtomicLong(System.currentTimeMillis() % 100000);

    public Page<JournalEntryResponse> findAll(Pageable pageable) {
        return journalEntryRepository.findAllByDeletedAtIsNull(pageable)
                .map(this::toResponse);
    }

    public Page<JournalEntryResponse> search(String query, Pageable pageable) {
        return journalEntryRepository.searchEntries(query, pageable)
                .map(this::toResponse);
    }

    public JournalEntryResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public JournalEntryResponse create(JournalEntryRequest request) {
        log.info("Creating journal entry: description={}", request.description());

        var entry = new JournalEntry();
        entry.setEntryNumber(generateEntryNumber());
        entry.setEntryDate(request.entryDate());
        entry.setDescription(request.description());
        entry.setReference(request.reference());
        entry.setPosted(false);
        entry.setReversed(false);

        int pos = 0;
        for (var lineReq : request.lines()) {
            var account = accountRepository.findById(lineReq.accountId())
                    .orElseThrow(() -> new EntityNotFoundException("Account not found: " + lineReq.accountId()));
            var line = new JournalEntryLine();
            line.setAccount(account);
            line.setDescription(lineReq.description());
            line.setDebit(lineReq.debit());
            line.setCredit(lineReq.credit());
            line.setPosition(lineReq.position() != null ? lineReq.position() : pos++);
            entry.addLine(line);
        }

        validateBalance(entry);
        
        entry = journalEntryRepository.save(entry);

        log.info("Created journal entry id={} number={}", entry.getId(), entry.getEntryNumber());
        return toResponse(entry);
    }

    @Transactional
    public void post(UUID id) {
        log.info("Posting journal entry id={}", id);
        var entry = findEntityById(id);

        if (entry.getPosted()) {
            throw new IllegalStateException("Journal entry is already posted: " + id);
        }
        
        validateBalance(entry);

        entry.setPosted(true);
        journalEntryRepository.save(entry);

        // TODO: Ensure event includes all entry details
        eventPublisher.publishEvent(new JournalEntryPostedEvent(
                entry.getId(), entry.getEntryNumber()));
        
        log.info("Posted journal entry id={}", id);
    }

    @Transactional
    public JournalEntryResponse reverse(UUID id) {
        log.info("Reversing journal entry id={}", id);
        var original = findEntityById(id);

        if (!original.getPosted()) {
            throw new IllegalStateException("Only posted entries can be reversed: " + id);
        }
        if (original.getReversed()) {
            throw new IllegalStateException("Journal entry is already reversed: " + id);
        }

        // Create reversal entry
        var reversal = new JournalEntry();
        reversal.setEntryNumber(generateEntryNumber());
        reversal.setEntryDate(LocalDate.now());
        reversal.setDescription("Reversal of " + original.getEntryNumber() + ": " + original.getDescription());
        reversal.setReference(original.getEntryNumber());
        reversal.setPosted(true);

        int pos = 0;
        for (var line : original.getLines()) {
            var reversalLine = new JournalEntryLine();
            reversalLine.setAccount(line.getAccount());
            reversalLine.setDescription("Reversal: " + (line.getDescription() != null ? line.getDescription() : ""));
            reversalLine.setDebit(line.getCredit());   // Swap debit and credit
            reversalLine.setCredit(line.getDebit());
            reversalLine.setPosition(pos++);
            reversal.addLine(reversalLine);
        }

        original.setReversed(true);
        journalEntryRepository.save(original);
        
        reversal = journalEntryRepository.save(reversal);

        eventPublisher.publishEvent(new JournalEntryReversedEvent(
                original.getId(), original.getEntryNumber(), reversal.getId()));

        log.info("Reversed journal entry id={}, reversal id={}", id, reversal.getId());
        return toResponse(reversal);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Soft-deleting journal entry id={}", id);
        var entry = findEntityById(id);
        
        if (entry.getPosted()) {
            throw new IllegalStateException("Cannot delete a posted journal entry. Reverse it instead.");
        }
        
        entry.setDeletedAt(Instant.now());
        
        journalEntryRepository.save(entry);
    }

    // ── Private helpers ───────────────────────────────────────

    private JournalEntry findEntityById(UUID id) {
        return journalEntryRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Journal entry not found: " + id));
    }

    private void validateBalance(JournalEntry entry) {
        var totalDebit = entry.getLines().stream()
                .map(JournalEntryLine::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalCredit = entry.getLines().stream()
                .map(JournalEntryLine::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new IllegalArgumentException(
                    "Journal entry is not balanced: debits=%s credits=%s"
                            .formatted(totalDebit.toPlainString(), totalCredit.toPlainString()));
        }
    }

    private String generateEntryNumber() {
        return "JE-%06d".formatted(SEQUENCE.incrementAndGet());
    }

    private JournalEntryResponse toResponse(JournalEntry entry) {
        var totalDebit = entry.getLines().stream()
                .map(JournalEntryLine::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalCredit = entry.getLines().stream()
                .map(JournalEntryLine::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var lineResponses = entry.getLines().stream()
                .map(line -> new JournalEntryLineResponse(
                        line.getId(),
                        line.getAccount().getId(),
                        line.getAccount().getAccountNumber(),
                        line.getAccount().getName(),
                        line.getDescription(),
                        line.getDebit(),
                        line.getCredit(),
                        line.getPosition()
                ))
                .toList();

        return new JournalEntryResponse(
                entry.getId(),
                entry.getEntryNumber(),
                entry.getEntryDate(),
                entry.getDescription(),
                entry.getPosted(),
                entry.getReversed(),
                entry.getReference(),
                totalDebit,
                totalCredit,
                lineResponses,
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }
}

