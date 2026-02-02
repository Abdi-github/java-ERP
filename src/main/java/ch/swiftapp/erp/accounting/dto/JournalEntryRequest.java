package ch.swiftapp.erp.accounting.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating or updating a {@link ch.swiftapp.erp.accounting.model.JournalEntry}.
 */
public record JournalEntryRequest(

        @NotNull(message = "Entry date is required")
        LocalDate entryDate,

        @NotBlank(message = "Description is required")
        String description,

        String reference,

        @NotEmpty(message = "At least one line is required")
        @Valid
        List<JournalEntryLineRequest> lines
) {}

