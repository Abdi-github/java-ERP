package ch.swiftapp.erp.sales.event;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain event published when a new sales order is created.
 *
 * @param orderId     the ID of the newly created order
 * @param orderNumber the order number (e.g., "SO-2026-00001")
 * @param customerId  the customer who placed the order
 */
public record SalesOrderCreatedEvent(UUID orderId, String orderNumber, UUID customerId) {}

