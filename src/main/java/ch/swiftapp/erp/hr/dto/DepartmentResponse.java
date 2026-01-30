package ch.swiftapp.erp.hr.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record DepartmentResponse(
        UUID id, String code, String name, String description,
        UUID managerId, String managerName,
        Boolean active, Instant createdAt, Instant updatedAt,
        Map<String, String> nameTranslations,
        Map<String, String> descriptionTranslations
) {}

