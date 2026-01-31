package ch.swiftapp.erp.production.service;

import ch.swiftapp.erp.production.ProductionModuleApi;
import ch.swiftapp.erp.production.ProductionOrderSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component @RequiredArgsConstructor
public class ProductionModuleApiFacade implements ProductionModuleApi {
    private final ProductionOrderService service;

    @Override public long getOpenProductionOrderCount() { return service.countOpenOrders(); }
    @Override public BigDecimal getPlannedOutputForProduct(UUID productId) { return service.getPlannedOutputForProduct(productId); }
    @Override public BigDecimal getOrderActualCost(UUID orderId) { return service.getOrderActualCost(orderId); }
    @Override public List<ProductionOrderSummary> getRecentProductionOrders(int limit) { return service.getRecentOrders(limit); }
}

