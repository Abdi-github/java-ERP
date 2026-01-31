package ch.swiftapp.erp.inventory.event;

import ch.swiftapp.erp.inventory.model.StockItemType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain event published when stock reaches or falls below the minimum threshold.
 */
public record LowStockAlertEvent(
        UUID itemId,
        StockItemType itemType,
        UUID warehouseId,
        BigDecimal currentQuantity,
        BigDecimal minimumThreshold
) {}

