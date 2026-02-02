package ch.swiftapp.erp.accounting.service;

import ch.swiftapp.erp.accounting.dto.AccountRequest;
import ch.swiftapp.erp.accounting.dto.AccountResponse;
import ch.swiftapp.erp.accounting.model.Account;
import ch.swiftapp.erp.accounting.repository.AccountRepository;
import ch.swiftapp.erp.accounting.repository.JournalEntryLineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing the chart of accounts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final JournalEntryLineRepository lineRepository;

    public Page<AccountResponse> findAll(Pageable pageable) {
        return accountRepository.findAllByDeletedAtIsNull(pageable)
                .map(this::toResponse);
    }

    public List<AccountResponse> findAllActive() {
        return accountRepository.findAllByDeletedAtIsNullAndActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public Page<AccountResponse> search(String query, Pageable pageable) {
        return accountRepository.searchAccounts(query, pageable)
                .map(this::toResponse);
    }

    public AccountResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    public Optional<BigDecimal> getBalance(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(lineRepository.calculateAccountBalance(accountId));
    }

    @Transactional
    public AccountResponse create(AccountRequest request) {
        log.info("Creating account: number={} name={}", request.accountNumber(), request.name());

        if (accountRepository.existsByAccountNumberIgnoreCase(request.accountNumber())) {
            throw new IllegalArgumentException("Account number already exists: " + request.accountNumber());
        }

        var account = new Account();
        mapRequestToEntity(request, account);
        
        account = accountRepository.save(account);

        log.info("Created account id={} number={}", account.getId(), account.getAccountNumber());
        return toResponse(account);
    }

    @Transactional
    public AccountResponse update(UUID id, AccountRequest request) {
        log.info("Updating account id={}", id);
        var account = findEntityById(id);

        if (!account.getAccountNumber().equalsIgnoreCase(request.accountNumber())
                && accountRepository.existsByAccountNumberIgnoreCase(request.accountNumber())) {
            throw new IllegalArgumentException("Account number already exists: " + request.accountNumber());
        }

        mapRequestToEntity(request, account);
        
        account = accountRepository.save(account);
        
        return toResponse(account);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Soft-deleting account id={}", id);
        var account = findEntityById(id);
        
        account.setDeletedAt(Instant.now());
        
        accountRepository.save(account);
    }

    private Account findEntityById(UUID id) {
        return accountRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Account not found: " + id));
    }

    private void mapRequestToEntity(AccountRequest request, Account account) {
        account.setAccountNumber(request.accountNumber());
        account.setName(request.name());
        account.setDescription(request.description());
        account.setAccountType(request.accountType());
        account.setActive(request.active() != null ? request.active() : true);
        
        if (request.parentId() != null) {
            account.setParent(accountRepository.findById(request.parentId()).orElse(null));
        } else {
            account.setParent(null);
        }
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getName(),
                account.getDescription(),
                account.getAccountType(),
                account.getParent() != null ? account.getParent().getId() : null,
                account.getParent() != null ? account.getParent().getName() : null,
                account.getActive(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}

