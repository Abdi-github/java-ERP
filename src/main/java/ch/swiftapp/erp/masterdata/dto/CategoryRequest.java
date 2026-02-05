package ch.swiftapp.erp.masterdata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for creating or updating a {@link ch.swiftapp.erp.masterdata.model.Category}.
 */
public record CategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,

        String description,

        UUID parentId,

        Map<String, String> nameTranslations,
        Map<String, String> descriptionTranslations
) {}

