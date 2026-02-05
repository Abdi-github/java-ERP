package ch.swiftapp.erp.masterdata.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for a single BOM line.
 */
public record BomLineResponse(
        UUID id,
        UUID productId,
        UUID materialId,
        String materialSku,
        String materialName,
        BigDecimal quantity,
        UUID unitOfMeasureId,
        String unitOfMeasureCode,
        Integer position,
        String notes
) {}

