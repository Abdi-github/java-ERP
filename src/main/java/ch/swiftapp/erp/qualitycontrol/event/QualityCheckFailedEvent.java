package ch.swiftapp.erp.qualitycontrol.event;
import java.util.UUID;
public record QualityCheckFailedEvent(UUID qualityCheckId, String checkNumber, UUID productionOrderId) {}

