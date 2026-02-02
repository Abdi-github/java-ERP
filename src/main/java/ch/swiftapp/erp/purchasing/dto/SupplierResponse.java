package ch.swiftapp.erp.purchasing.dto;

import java.time.Instant;
import java.util.UUID;

public record SupplierResponse(
        UUID id, String supplierNumber, String companyName,
        String firstName, String lastName, String displayName,
        String email, String phone, String street, String city,
        String postalCode, String canton, String country,
        String vatNumber, Integer paymentTerms,
        String contactPerson, String website, String notes,
        Boolean active, Instant createdAt, Instant updatedAt
) {}

