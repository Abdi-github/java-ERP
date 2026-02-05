package ch.swiftapp.erp.qualitycontrol.dto;
import ch.swiftapp.erp.qualitycontrol.model.NcrSeverity;
import ch.swiftapp.erp.qualitycontrol.model.NcrStatus;
import java.time.Instant; import java.util.UUID;
public record NcrResponse(UUID id, String ncrNumber, UUID qualityCheckId, String checkNumber, NcrSeverity severity, String description, String correctiveAction, NcrStatus status, Instant closedAt, Instant createdAt) {}

