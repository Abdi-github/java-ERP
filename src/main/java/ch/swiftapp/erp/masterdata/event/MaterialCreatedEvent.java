package ch.swiftapp.erp.masterdata.event;

import java.util.UUID;

/**
 * Domain event published when a new material is created.
 *
 * @param materialId the ID of the newly created material
 * @param sku        the material SKU
 * @param name       the material name
 */
public record MaterialCreatedEvent(UUID materialId, String sku, String name) {}

