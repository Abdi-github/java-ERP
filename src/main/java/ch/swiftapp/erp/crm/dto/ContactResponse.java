package ch.swiftapp.erp.crm.dto;

import java.time.Instant;
import java.util.UUID;

public record ContactResponse(
        UUID id, String firstName, String lastName, String fullName,
        String email, String phone, String company, String position,
        UUID customerId, String notes, Boolean active,
        Instant createdAt, Instant updatedAt
) {}

