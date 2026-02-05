package ch.swiftapp.erp.masterdata.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.masterdata.model.Category}.
 */
public record CategoryResponse(
        UUID id,
        String name,
        String description,
        UUID parentId,
        String parentName,
        Instant createdAt,
        Instant updatedAt,
        Map<String, String> nameTranslations,
        Map<String, String> descriptionTranslations
) {}

