package ch.swiftapp.erp.masterdata.event;

import java.util.UUID;

/**
 * Domain event published when a product is updated.
 *
 * @param productId the ID of the updated product
 * @param sku       the product SKU
 * @param name      the product name
 */
public record ProductUpdatedEvent(UUID productId, String sku, String name) {}

