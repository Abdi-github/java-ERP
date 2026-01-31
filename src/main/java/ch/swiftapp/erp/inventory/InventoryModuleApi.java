package ch.swiftapp.erp.inventory;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Public API for the Inventory module.
 *
 * <p>Other Spring Modulith modules should depend on this interface only —
 * never on internal model, repository, or service classes.</p>
 */
public interface InventoryModuleApi {

    /**
     * Get current stock quantity for a given item in a specific warehouse.
     *
     * @param itemId      the product or material UUID
     * @param warehouseId the warehouse UUID
     * @return current available quantity
     */
    BigDecimal getStockLevel(UUID itemId, UUID warehouseId);

    /**
     * Get total stock quantity across all warehouses for a given item.
     *
     * @param itemId the product or material UUID
     * @return total available quantity across all warehouses
     */
    BigDecimal getTotalStockLevel(UUID itemId);

    /**
     * Check if enough stock is available for an item.
     *
     * @param itemId      the product or material UUID
     * @param warehouseId the warehouse UUID
     * @param quantity    the required quantity
     * @return true if available stock >= required quantity
     */
    boolean isStockAvailable(UUID itemId, UUID warehouseId, BigDecimal quantity);

    /**
     * Count stock-level records where quantity on hand is at or below the given threshold.
     *
     * @param threshold the low-stock threshold (inclusive)
     * @return number of low-stock entries
     */
    long countLowStockItems(BigDecimal threshold);
}

