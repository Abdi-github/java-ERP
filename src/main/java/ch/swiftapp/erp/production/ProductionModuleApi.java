package ch.swiftapp.erp.production;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Public API for the Production module.
 */
public interface ProductionModuleApi {
    long getOpenProductionOrderCount();
    BigDecimal getPlannedOutputForProduct(UUID productId);
    BigDecimal getOrderActualCost(UUID orderId);

    /**
     * Get summaries of the most recent production orders.
     *
     * @param limit maximum number of orders to return
     * @return list of order summaries ordered by creation date descending
     */
    List<ProductionOrderSummary> getRecentProductionOrders(int limit);
}

