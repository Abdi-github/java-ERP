package ch.swiftapp.erp.qualitycontrol.dto;
import ch.swiftapp.erp.qualitycontrol.model.CheckResult;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record QualityCheckResponse(UUID id, String checkNumber, UUID inspectionPlanId, String inspectionPlanName, UUID productionOrderId, String checkedBy, LocalDate checkDate, CheckResult result, String notes, Instant createdAt) {}

