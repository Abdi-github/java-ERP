package ch.swiftapp.erp.accounting.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for a single journal entry line.
 */
public record JournalEntryLineRequest(

        @NotNull(message = "Account ID is required")
        UUID accountId,

        String description,

        @NotNull(message = "Debit amount is required")
        @DecimalMin(value = "0.0000", message = "Debit must be >= 0")
        BigDecimal debit,

        @NotNull(message = "Credit amount is required")
        @DecimalMin(value = "0.0000", message = "Credit must be >= 0")
        BigDecimal credit,

        Integer position
) {}

