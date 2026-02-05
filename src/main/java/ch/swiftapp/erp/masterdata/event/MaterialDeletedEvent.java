package ch.swiftapp.erp.masterdata.event;

import java.util.UUID;

/**
 * Domain event published when a material is soft-deleted.
 *
 * @param materialId the ID of the deleted material
 * @param sku        the material SKU
 */
public record MaterialDeletedEvent(UUID materialId, String sku) {}

