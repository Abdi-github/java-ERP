package ch.swiftapp.erp.sales.event;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain event published when a sales order is confirmed by the customer.
 *
 * @param orderId     the order UUID
 * @param orderNumber the order number
 * @param totalAmount the confirmed total amount in CHF
 */
public record SalesOrderConfirmedEvent(UUID orderId, String orderNumber, BigDecimal totalAmount) {}

