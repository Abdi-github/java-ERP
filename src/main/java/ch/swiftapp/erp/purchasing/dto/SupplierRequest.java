package ch.swiftapp.erp.purchasing.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;

/**
 * Request DTO for creating or updating a {@link ch.swiftapp.erp.purchasing.model.Supplier}.
 */
public record SupplierRequest(
        @Size(max = 30) String supplierNumber,
        String companyName,
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Email String email,
        @Size(max = 50) String phone,
        String street,
        @Size(max = 100) String city,
        @Size(max = 20) String postalCode,
        @Size(max = 50) String canton,
        String country,
        @Size(max = 30) String vatNumber,
        @Min(0) Integer paymentTerms,
        String contactPerson,
        String website,
        String notes,
        Boolean active
) {}

