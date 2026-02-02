package ch.swiftapp.erp.purchasing;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Public API for the Purchasing module.
 *
 * <p>Other Spring Modulith modules should depend on this interface only —
 * never on internal model, repository, or service classes.</p>
 */
public interface PurchasingModuleApi {

    /**
     * Get count of open (non-completed, non-cancelled) purchase orders.
     */
    long getOpenPurchaseOrderCount();

    /**
     * Get total spend for completed purchase orders in a given month.
     */
    BigDecimal getMonthlySpend(int year, int month);

    /**
     * Get total amount for a specific purchase order.
     */
    BigDecimal getOrderTotal(UUID orderId);
}

