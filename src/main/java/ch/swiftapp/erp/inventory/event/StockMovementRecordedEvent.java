package ch.swiftapp.erp.inventory.event;

import ch.swiftapp.erp.inventory.model.MovementType;
import ch.swiftapp.erp.inventory.model.StockItemType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain event published when a stock movement is recorded.
 */
public record StockMovementRecordedEvent(
        UUID movementId,
        String referenceNumber,
        MovementType movementType,
        UUID itemId,
        StockItemType itemType,
        UUID sourceWarehouseId,
        UUID targetWarehouseId,
        BigDecimal quantity
) {}

