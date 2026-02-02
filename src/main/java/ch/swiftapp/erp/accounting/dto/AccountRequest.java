package ch.swiftapp.erp.accounting.dto;

import ch.swiftapp.erp.accounting.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO for creating or updating an {@link ch.swiftapp.erp.accounting.model.Account}.
 */
public record AccountRequest(

        @NotBlank(message = "Account number is required")
        @Size(max = 20, message = "Account number must not exceed 20 characters")
        String accountNumber,

        @NotBlank(message = "Account name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,

        String description,

        @NotNull(message = "Account type is required")
        AccountType accountType,

        UUID parentId,

        Boolean active
) {}

