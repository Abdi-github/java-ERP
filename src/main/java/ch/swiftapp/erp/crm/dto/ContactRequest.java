package ch.swiftapp.erp.crm.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record ContactRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @Email String email, String phone, String company,
        String position, UUID customerId, String notes, Boolean active
) {}

