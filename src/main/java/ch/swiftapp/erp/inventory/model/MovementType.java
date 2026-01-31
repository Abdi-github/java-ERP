package ch.swiftapp.erp.inventory.model;

/**
 * Type of stock movement.
 *
 * <ul>
 *     <li>{@code GOODS_RECEIPT} — incoming stock from a purchase order</li>
 *     <li>{@code GOODS_ISSUE} — outgoing stock for general consumption</li>
 *     <li>{@code PRODUCTION_ISSUE} — materials issued from warehouse to production</li>
 *     <li>{@code PRODUCTION_RECEIPT} — finished goods received from production into stock</li>
 *     <li>{@code SHIPMENT} — goods shipped out to a customer against a sales order</li>
 *     <li>{@code TRANSFER} — movement between warehouses / locations</li>
 *     <li>{@code ADJUSTMENT} — manual stock correction (inventory count)</li>
 *     <li>{@code RETURN} — return from customer or to supplier</li>
 *     <li>{@code SCRAP} — stock written off as waste / non-conformance</li>
 * </ul>
 */
public enum MovementType {
    /** Incoming stock from a purchase order */
    GOODS_RECEIPT,
    /** Outgoing stock for general consumption */
    GOODS_ISSUE,
    /** Materials issued from warehouse to a production order */
    PRODUCTION_ISSUE,
    /** Finished goods received from production into stock */
    PRODUCTION_RECEIPT,
    /** Goods shipped to a customer against a sales order */
    SHIPMENT,
    /** Movement between warehouses or storage locations */
    TRANSFER,
    /** Manual stock correction (inventory count) */
    ADJUSTMENT,
    /** Return from customer or to supplier */
    RETURN,
    /** Stock written off as waste or non-conformance scrap */
    SCRAP
}

