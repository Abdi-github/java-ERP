package ch.swiftapp.erp.sales.event;

import java.util.UUID;

/**
 * Domain event published when a sales order is cancelled.
 *
 * @param orderId     the order UUID
 * @param orderNumber the order number
 * @param reason      the cancellation reason
 */
public record SalesOrderCancelledEvent(UUID orderId, String orderNumber, String reason) {}

