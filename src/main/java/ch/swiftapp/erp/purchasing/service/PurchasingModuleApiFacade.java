package ch.swiftapp.erp.purchasing.service;

import ch.swiftapp.erp.purchasing.PurchasingModuleApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PurchasingModuleApiFacade implements PurchasingModuleApi {

    private final PurchaseOrderService purchaseOrderService;

    @Override
    public long getOpenPurchaseOrderCount() { return purchaseOrderService.countOpenOrders(); }

    @Override
    public BigDecimal getMonthlySpend(int year, int month) { return purchaseOrderService.getMonthlySpend(year, month); }

    @Override
    public BigDecimal getOrderTotal(UUID orderId) { return purchaseOrderService.getOrderTotal(orderId); }
}

