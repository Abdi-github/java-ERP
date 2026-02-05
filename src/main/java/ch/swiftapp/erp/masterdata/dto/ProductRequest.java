package ch.swiftapp.erp.masterdata.dto;

import ch.swiftapp.erp.shared.model.VatRate;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for creating or updating a {@link ch.swiftapp.erp.masterdata.model.Product}.
 */
public record ProductRequest(

        @NotBlank(message = "SKU is required")
        @Size(max = 50, message = "SKU must not exceed 50 characters")
        String sku,

        @NotBlank(message = "Product name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,

        String description,

        UUID categoryId,

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.0000", message = "Unit price must be >= 0")
        BigDecimal unitPrice,

        @NotNull(message = "List price is required")
        @DecimalMin(value = "0.0000", message = "List price must be >= 0")
        BigDecimal listPrice,

        @NotNull(message = "VAT rate is required")
        VatRate vatRate,

        Boolean active,

        /** Optional locale → translated name map (de, fr, it, en). */
        Map<String, String> nameTranslations,

        /** Optional locale → translated description map (de, fr, it, en). */
        Map<String, String> descriptionTranslations
) {}

