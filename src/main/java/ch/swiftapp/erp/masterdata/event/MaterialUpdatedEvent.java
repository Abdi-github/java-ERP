package ch.swiftapp.erp.masterdata.event;

import java.util.UUID;

/**
 * Domain event published when a material is updated.
 *
 * @param materialId the ID of the updated material
 * @param sku        the material SKU
 * @param name       the material name
 */
public record MaterialUpdatedEvent(UUID materialId, String sku, String name) {}

