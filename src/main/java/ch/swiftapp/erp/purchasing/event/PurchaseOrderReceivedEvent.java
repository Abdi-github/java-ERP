package ch.swiftapp.erp.purchasing.event;

import java.util.UUID;

/** Published when goods are received — may trigger inventory stock-in. */
public record PurchaseOrderReceivedEvent(UUID orderId, String orderNumber) {}

