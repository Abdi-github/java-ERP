package ch.swiftapp.erp.accounting.dto;

import ch.swiftapp.erp.accounting.model.AccountType;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.accounting.model.Account}.
 */
public record AccountResponse(
        UUID id,
        String accountNumber,
        String name,
        String description,
        AccountType accountType,
        UUID parentId,
        String parentName,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {}

