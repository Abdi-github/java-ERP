package ch.swiftapp.erp.masterdata.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.masterdata.model.UnitOfMeasure}.
 */
public record UnitOfMeasureResponse(
        UUID id,
        String code,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        Map<String, String> nameTranslations,
        Map<String, String> descriptionTranslations
) {}

