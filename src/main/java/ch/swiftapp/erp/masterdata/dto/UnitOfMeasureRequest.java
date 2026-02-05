package ch.swiftapp.erp.masterdata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * Request DTO for creating or updating a {@link ch.swiftapp.erp.masterdata.model.UnitOfMeasure}.
 */
public record UnitOfMeasureRequest(

        @NotBlank(message = "Unit code is required")
        @Size(max = 20, message = "Code must not exceed 20 characters")
        String code,

        @NotBlank(message = "Unit name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description,

        Map<String, String> nameTranslations,
        Map<String, String> descriptionTranslations
) {}

