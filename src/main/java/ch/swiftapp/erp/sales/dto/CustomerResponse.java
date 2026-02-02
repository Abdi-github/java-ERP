package ch.swiftapp.erp.sales.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.sales.model.Customer}.
 */
public record CustomerResponse(
        UUID id,
        String customerNumber,
        String companyName,
        String firstName,
        String lastName,
        String displayName,
        String email,
        String phone,
        String street,
        String city,
        String postalCode,
        String canton,
        String country,
        String vatNumber,
        Integer paymentTerms,
        BigDecimal creditLimit,
        String notes,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {}

