package ch.swiftapp.erp.masterdata.dto;

import ch.swiftapp.erp.shared.model.VatRate;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for creating or updating a {@link ch.swiftapp.erp.masterdata.model.Material}.
 */
public record MaterialRequest(

        @NotBlank(message = "SKU is required")
        @Size(max = 50, message = "SKU must not exceed 50 characters")
        String sku,

        @NotBlank(message = "Material name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,

        String description,

        UUID categoryId,

        UUID unitOfMeasureId,

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.0000", message = "Unit price must be >= 0")
        BigDecimal unitPrice,

        @NotNull(message = "VAT rate is required")
        VatRate vatRate,

        @NotNull(message = "Minimum stock is required")
        @DecimalMin(value = "0.0000", message = "Minimum stock must be >= 0")
        BigDecimal minimumStock,

        Map<String, String> nameTranslations,
        Map<String, String> descriptionTranslations
) {}
