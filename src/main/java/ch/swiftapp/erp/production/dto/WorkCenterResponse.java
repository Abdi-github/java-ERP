package ch.swiftapp.erp.production.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record WorkCenterResponse(
        UUID id, String code, String name, String description,
        BigDecimal capacityPerDay, BigDecimal costPerHour,
        Boolean active, Instant createdAt, Instant updatedAt,
        Map<String, String> nameTranslations,
        Map<String, String> descriptionTranslations
) {}

