package ch.swiftapp.erp.sales.service;

import ch.swiftapp.erp.sales.MonthlyRevenueSummary;
import ch.swiftapp.erp.sales.SalesModuleApi;
import ch.swiftapp.erp.sales.SalesOrderSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the public Sales module API.
 *
 * <p>This facade is the only class other modules should interact with.
 * It delegates to internal services for the actual business logic.</p>
 */
@Component
@RequiredArgsConstructor
public class SalesModuleApiFacade implements SalesModuleApi {

    private final SalesOrderService salesOrderService;

    @Override
    public BigDecimal getMonthlyRevenue(int year, int month) {
        return salesOrderService.getMonthlyRevenue(year, month);
    }

    @Override
    public long getOpenOrderCount() {
        return salesOrderService.countOpenOrders();
    }

    @Override
    public BigDecimal getOrderTotal(UUID orderId) {
        return salesOrderService.getOrderTotal(orderId);
    }

    @Override
    public List<SalesOrderSummary> getRecentSalesOrders(int limit) {
        return salesOrderService.getRecentOrders(limit);
    }

    @Override
    public List<MonthlyRevenueSummary> getLast6MonthsRevenue() {
        return salesOrderService.getLast6MonthsRevenue();
    }

    @Override
    public Map<String, Long> getSalesOrderStatusBreakdown() {
        return salesOrderService.getStatusBreakdown();
    }
}

