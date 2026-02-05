package ch.swiftapp.erp.masterdata.event;

import java.util.UUID;

/**
 * Domain event published when a new product is created.
 *
 * @param productId the ID of the newly created product
 * @param sku       the product SKU
 * @param name      the product name
 */
public record ProductCreatedEvent(UUID productId, String sku, String name) {}

