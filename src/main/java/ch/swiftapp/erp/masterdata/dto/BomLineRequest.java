package ch.swiftapp.erp.masterdata.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for a single BOM line (linking a product to a material).
 */
public record BomLineRequest(

        @NotNull(message = "Material ID is required")
        UUID materialId,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.0001", message = "Quantity must be greater than 0")
        BigDecimal quantity,

        UUID unitOfMeasureId,

        @NotNull(message = "Position is required")
        @Min(value = 1, message = "Position must be at least 1")
        Integer position,

        @Size(max = 500, message = "Notes must not exceed 500 characters")
        String notes
) {}

