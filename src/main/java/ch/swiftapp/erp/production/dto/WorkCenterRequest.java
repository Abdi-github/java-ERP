package ch.swiftapp.erp.production.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Map;

public record WorkCenterRequest(
        @NotBlank @Size(max = 20) String code,
        @NotBlank @Size(max = 255) String name,
        String description,
        @DecimalMin("0.0001") BigDecimal capacityPerDay,
        @DecimalMin("0.0000") BigDecimal costPerHour,
        Boolean active,
        Map<String, String> nameTranslations,
        Map<String, String> descriptionTranslations
) {}

