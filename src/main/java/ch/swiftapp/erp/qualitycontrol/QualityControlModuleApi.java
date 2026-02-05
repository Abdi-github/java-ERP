package ch.swiftapp.erp.qualitycontrol;

import java.util.UUID;

/**
 * Public API for the Quality Control module.
 */
public interface QualityControlModuleApi {
    boolean hasOpenNcrs(UUID productionOrderId);
}

