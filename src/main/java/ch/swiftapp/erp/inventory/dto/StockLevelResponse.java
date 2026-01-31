package ch.swiftapp.erp.inventory.dto;

import ch.swiftapp.erp.inventory.model.StockItemType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for a stock level entry.
 */
public record StockLevelResponse(
        UUID id,
        UUID itemId,
        StockItemType itemType,
        UUID warehouseId,
        String warehouseCode,
        String warehouseName,
        BigDecimal quantityOnHand,
        BigDecimal quantityReserved,
        BigDecimal quantityAvailable
) {}

