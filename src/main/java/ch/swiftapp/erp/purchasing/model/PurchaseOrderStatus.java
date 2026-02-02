package ch.swiftapp.erp.purchasing.model;

/**
 * Lifecycle status of a purchase order.
 *
 * <pre>
 * DRAFT → SUBMITTED → CONFIRMED → PARTIALLY_RECEIVED → RECEIVED → COMPLETED
 *                                       ↓
 *                                    CANCELLED
 * </pre>
 */
public enum PurchaseOrderStatus {
    /** Initial state — editable, not sent to supplier */
    DRAFT,
    /** Sent to supplier, awaiting confirmation */
    SUBMITTED,
    /** Order placed with the supplier (synonym for SUBMITTED in some flows) */
    ORDERED,
    /** Supplier confirmed, awaiting delivery */
    CONFIRMED,
    /** Some goods received, remainder pending */
    PARTIALLY_RECEIVED,
    /** All goods received */
    RECEIVED,
    /** Fully completed (invoiced and closed) */
    COMPLETED,
    /** Cancelled before completion */
    CANCELLED
}

