package ch.swiftapp.erp.crm.dto;

import ch.swiftapp.erp.crm.model.InteractionType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record InteractionResponse(
        UUID id, UUID contactId, String contactName,
        InteractionType interactionType, String subject, String description,
        Instant interactionDate, LocalDate followUpDate,
        Instant createdAt
) {}

