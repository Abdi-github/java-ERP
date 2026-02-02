package ch.swiftapp.erp.accounting.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.accounting.model.JournalEntry}.
 */
public record JournalEntryResponse(
        UUID id,
        String entryNumber,
        LocalDate entryDate,
        String description,
        Boolean posted,
        Boolean reversed,
        String reference,
        BigDecimal totalDebit,
        BigDecimal totalCredit,
        List<JournalEntryLineResponse> lines,
        Instant createdAt,
        Instant updatedAt
) {}

