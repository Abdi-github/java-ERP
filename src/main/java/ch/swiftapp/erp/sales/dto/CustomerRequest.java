package ch.swiftapp.erp.sales.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for creating or updating a {@link ch.swiftapp.erp.sales.model.Customer}.
 */
public record CustomerRequest(

        @Size(max = 30, message = "Customer number must not exceed 30 characters")
        String customerNumber,

        String companyName,

        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @Email(message = "Invalid email address")
        String email,

        @Size(max = 50, message = "Phone must not exceed 50 characters")
        String phone,

        String street,

        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        @Size(max = 20, message = "Postal code must not exceed 20 characters")
        String postalCode,

        @Size(max = 50, message = "Canton must not exceed 50 characters")
        String canton,

        String country,

        @Size(max = 30, message = "VAT number must not exceed 30 characters")
        String vatNumber,

        @Min(value = 0, message = "Payment terms must be >= 0")
        Integer paymentTerms,

        @DecimalMin(value = "0.0000", message = "Credit limit must be >= 0")
        BigDecimal creditLimit,

        String notes,

        Boolean active
) {}

