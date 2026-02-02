package ch.swiftapp.erp.accounting.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for a single journal entry line.
 */
public record JournalEntryLineResponse(
        UUID id,
        UUID accountId,
        String accountNumber,
        String accountName,
        String description,
        BigDecimal debit,
        BigDecimal credit,
        Integer position
) {}

