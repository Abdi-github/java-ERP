package ch.swiftapp.erp.inventory.service;

import ch.swiftapp.erp.inventory.InventoryModuleApi;
import ch.swiftapp.erp.inventory.model.StockItemType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implementation of the public Inventory module API.
 *
 * <p>This facade is the only class other modules should interact with.
 * It delegates to internal services for the actual business logic.</p>
 */
@Component
@RequiredArgsConstructor
public class InventoryModuleApiFacade implements InventoryModuleApi {

    private final StockService stockService;

    /** Default item type when not specified by the caller. */
    private static final StockItemType DEFAULT_TYPE = StockItemType.PRODUCT;

    @Override
    public BigDecimal getStockLevel(UUID itemId, UUID warehouseId) {
        return stockService.getStockLevel(itemId, DEFAULT_TYPE, warehouseId);
    }

    @Override
    public BigDecimal getTotalStockLevel(UUID itemId) {
        return stockService.getTotalStockLevel(itemId, DEFAULT_TYPE);
    }

    @Override
    public boolean isStockAvailable(UUID itemId, UUID warehouseId, BigDecimal quantity) {
        return stockService.isStockAvailable(itemId, DEFAULT_TYPE, warehouseId, quantity);
    }

    @Override
    public long countLowStockItems(BigDecimal threshold) {
        return stockService.countLowStockItems(threshold);
    }
}

