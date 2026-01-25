package ch.swiftapp.erp.shared.service;

import java.math.BigDecimal;

/**
 * Aggregated KPI snapshot for the ERP main dashboard.
 *
 * @param openSalesOrders     count of non-completed, non-cancelled sales orders
 * @param revenueThisMonth    total revenue from completed orders in the current month (CHF)
 * @param lowStockItems       count of stock-level records at or below the low-stock threshold
 * @param openProductionOrders count of non-completed, non-cancelled production orders
 * @param openPurchaseOrders  count of non-completed, non-cancelled purchase orders
 * @param spendThisMonth      total spend from completed purchase orders in the current month (CHF)
 */
public record DashboardStats(
        long openSalesOrders,
        BigDecimal revenueThisMonth,
        long lowStockItems,
        long openProductionOrders,
        long openPurchaseOrders,
        BigDecimal spendThisMonth
) {}

