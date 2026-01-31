package ch.swiftapp.erp.inventory.dto;

import ch.swiftapp.erp.inventory.model.MovementType;
import ch.swiftapp.erp.inventory.model.StockItemType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Response DTO for a stock movement.
 */
public record StockMovementResponse(
        UUID id,
        String referenceNumber,
        MovementType movementType,
        UUID itemId,
        StockItemType itemType,
        UUID sourceWarehouseId,
        String sourceWarehouseCode,
        UUID targetWarehouseId,
        String targetWarehouseCode,
        BigDecimal quantity,
        ZonedDateTime movementDate,
        String reason,
        String sourceDocumentType,
        UUID sourceDocumentId,
        ZonedDateTime createdAt
) {}

