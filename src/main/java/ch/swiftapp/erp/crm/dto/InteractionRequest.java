package ch.swiftapp.erp.crm.dto;

import ch.swiftapp.erp.crm.model.InteractionType;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record InteractionRequest(
        @NotNull UUID contactId,
        @NotNull InteractionType interactionType,
        @NotBlank @Size(max = 255) String subject,
        String description,
        Instant interactionDate,
        LocalDate followUpDate
) {}

