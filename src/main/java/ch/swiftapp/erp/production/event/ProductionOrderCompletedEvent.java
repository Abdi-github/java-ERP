package ch.swiftapp.erp.production.event;

import java.math.BigDecimal;
import java.util.UUID;

/** Triggers inventory stock-in for the finished product. */
public record ProductionOrderCompletedEvent(UUID orderId, String orderNumber, UUID productId, BigDecimal completedQuantity) {}

