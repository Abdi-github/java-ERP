package ch.swiftapp.erp.purchasing.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseOrderConfirmedEvent(UUID orderId, String orderNumber, BigDecimal totalAmount) {}

