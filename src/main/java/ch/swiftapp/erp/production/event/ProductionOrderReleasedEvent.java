package ch.swiftapp.erp.production.event;

import java.util.UUID;

public record ProductionOrderReleasedEvent(UUID orderId, String orderNumber) {}

