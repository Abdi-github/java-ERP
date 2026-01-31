package ch.swiftapp.erp.production.event;

import java.util.UUID;

public record ProductionOrderCreatedEvent(UUID orderId, String orderNumber, UUID productId) {}

