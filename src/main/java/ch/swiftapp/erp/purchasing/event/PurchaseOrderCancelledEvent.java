package ch.swiftapp.erp.purchasing.event;

import java.util.UUID;

public record PurchaseOrderCancelledEvent(UUID orderId, String orderNumber, String reason) {}

