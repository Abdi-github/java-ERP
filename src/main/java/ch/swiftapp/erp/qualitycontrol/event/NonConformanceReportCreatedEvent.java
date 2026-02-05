package ch.swiftapp.erp.qualitycontrol.event;
import java.util.UUID;
public record NonConformanceReportCreatedEvent(UUID ncrId, String ncrNumber, UUID qualityCheckId) {}

