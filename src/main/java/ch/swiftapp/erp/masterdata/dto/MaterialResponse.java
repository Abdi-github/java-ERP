package ch.swiftapp.erp.masterdata.dto;

import ch.swiftapp.erp.shared.model.VatRate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.masterdata.model.Material}.
 */
public record MaterialResponse(
        UUID id,
        String sku,
        String name,
        String description,
        UUID categoryId,
        String categoryName,
        UUID unitOfMeasureId,
        String unitOfMeasureCode,
        BigDecimal unitPrice,
        VatRate vatRate,
        BigDecimal minimumStock,
        Instant createdAt,
        Instant updatedAt,
        Map<String, String> nameTranslations,
        Map<String, String> descriptionTranslations
) {}

