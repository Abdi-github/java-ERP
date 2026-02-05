package ch.swiftapp.erp.masterdata.event;

import java.util.UUID;

/**
 * Domain event published when a product is soft-deleted.
 *
 * @param productId the ID of the deleted product
 * @param sku       the product SKU
 */
public record ProductDeletedEvent(UUID productId, String sku) {}

