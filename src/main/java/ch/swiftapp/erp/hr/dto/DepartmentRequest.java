package ch.swiftapp.erp.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;

public record DepartmentRequest(
        @NotBlank(message = "Department code is required") @Size(max = 20) String code,
        @NotBlank(message = "Department name is required") @Size(max = 255) String name,
        String description,
        UUID managerId,
        Boolean active,
        Map<String, String> nameTranslations,
        Map<String, String> descriptionTranslations
) {}

