package ch.swiftapp.erp.sales.model;

/**
 * Lifecycle status of a sales order.
 *
 * <pre>
 * DRAFT → CONFIRMED → PROCESSING → SHIPPED → DELIVERED → COMPLETED
 *                          ↓
 *                       CANCELLED
 * </pre>
 */
public enum SalesOrderStatus {
    /** Initial state — editable, not committed */
    DRAFT,
    /** Customer-confirmed, ready for fulfilment */
    CONFIRMED,
    /** Being picked/packed in the warehouse */
    PROCESSING,
    /** Goods shipped to customer */
    SHIPPED,
    /** Goods delivered and acknowledged */
    DELIVERED,
    /** Invoice has been issued to the customer */
    INVOICED,
    /** Fully completed (invoiced and closed) */
    COMPLETED,
    /** Cancelled before completion */
    CANCELLED
}

