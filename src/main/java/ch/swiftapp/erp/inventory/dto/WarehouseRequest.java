package ch.swiftapp.erp.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * Request DTO for creating or updating a {@link ch.swiftapp.erp.inventory.model.Warehouse}.
 */
public record WarehouseRequest(

        @NotBlank(message = "Warehouse code is required")
        @Size(max = 20, message = "Code must not exceed 20 characters")
        String code,

        @NotBlank(message = "Warehouse name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,

        String description,

        String address,

        Boolean active,

        Map<String, String> nameTranslations,
        Map<String, String> descriptionTranslations
) {}

