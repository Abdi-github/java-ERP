package ch.swiftapp.erp.shared.service;

import ch.swiftapp.erp.inventory.InventoryModuleApi;
import ch.swiftapp.erp.production.ProductionModuleApi;
import ch.swiftapp.erp.production.ProductionOrderSummary;
import ch.swiftapp.erp.purchasing.PurchasingModuleApi;
import ch.swiftapp.erp.sales.MonthlyRevenueSummary;
import ch.swiftapp.erp.sales.SalesModuleApi;
import ch.swiftapp.erp.sales.SalesOrderSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Aggregation service for the ERP main dashboard.
 *
 * <p>Collects KPIs and summaries from all operational modules via their public APIs,
 * ensuring strict Spring Modulith boundaries are respected.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    /** Low-stock threshold: items with ≤ 10 units on hand are flagged. */
    private static final BigDecimal LOW_STOCK_THRESHOLD = BigDecimal.TEN;

    private final SalesModuleApi salesApi;
    private final ProductionModuleApi productionApi;
    private final InventoryModuleApi inventoryApi;
    private final PurchasingModuleApi purchasingApi;

    /**
     * Build the top-level KPI snapshot for the current month.
     */
    public DashboardStats getStats() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        log.debug("Fetching dashboard stats for {}/{}", year, month);

        return new DashboardStats(
                salesApi.getOpenOrderCount(),
                salesApi.getMonthlyRevenue(year, month),
                inventoryApi.countLowStockItems(LOW_STOCK_THRESHOLD),
                productionApi.getOpenProductionOrderCount(),
                purchasingApi.getOpenPurchaseOrderCount(),
                purchasingApi.getMonthlySpend(year, month)
        );
    }

    /**
     * Return the 5 most recently created sales orders.
     */
    public List<SalesOrderSummary> getRecentSalesOrders() {
        return salesApi.getRecentSalesOrders(5);
    }

    /**
     * Return the 5 most recently created production orders.
     */
    public List<ProductionOrderSummary> getRecentProductionOrders() {
        return productionApi.getRecentProductionOrders(5);
    }

    /**
     * Return monthly revenue for the last 6 months (oldest → newest).
     */
    public List<MonthlyRevenueSummary> getLast6MonthsRevenue() {
        return salesApi.getLast6MonthsRevenue();
    }

    /**
     * Return sales order status breakdown for the doughnut chart.
     */
    public Map<String, Long> getSalesStatusBreakdown() {
        return salesApi.getSalesOrderStatusBreakdown();
    }
}

