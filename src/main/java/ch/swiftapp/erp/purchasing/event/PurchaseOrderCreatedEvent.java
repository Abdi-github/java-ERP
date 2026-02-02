package ch.swiftapp.erp.purchasing.event;

import java.util.UUID;

public record PurchaseOrderCreatedEvent(UUID orderId, String orderNumber, UUID supplierId) {}

