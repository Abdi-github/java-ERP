package ch.swiftapp.erp.sales;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Public API for the Sales module.
 *
 * <p>Other Spring Modulith modules should depend on this interface only —
 * never on internal model, repository, or service classes.</p>
 */
public interface SalesModuleApi {

    /**
     * Get total sales revenue for a given period.
     *
     * @param year  the year
     * @param month the month (1-12)
     * @return total revenue in CHF
     */
    BigDecimal getMonthlyRevenue(int year, int month);

    /**
     * Get count of open (non-completed) sales orders.
     *
     * @return count of open orders
     */
    long getOpenOrderCount();

    /**
     * Get order total amount by order ID.
     *
     * @param orderId the sales order UUID
     * @return total amount
     */
    BigDecimal getOrderTotal(UUID orderId);

    /**
     * Get summaries of the most recent sales orders.
     *
     * @param limit maximum number of orders to return
     * @return list of order summaries ordered by creation date descending
     */
    List<SalesOrderSummary> getRecentSalesOrders(int limit);

    /**
     * Get monthly revenue summaries for the past 6 months (including current month).
     *
     * @return list of monthly revenue summaries ordered from oldest to newest
     */
    List<MonthlyRevenueSummary> getLast6MonthsRevenue();

    /**
     * Get a count of sales orders grouped by status.
     *
     * @return map of status name → count
     */
    Map<String, Long> getSalesOrderStatusBreakdown();
}

