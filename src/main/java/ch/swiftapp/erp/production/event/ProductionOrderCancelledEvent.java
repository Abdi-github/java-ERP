package ch.swiftapp.erp.production.event;

import java.util.UUID;

public record ProductionOrderCancelledEvent(UUID orderId, String orderNumber, String reason) {}

