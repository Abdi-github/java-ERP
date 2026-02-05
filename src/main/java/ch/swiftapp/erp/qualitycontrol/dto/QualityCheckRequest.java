package ch.swiftapp.erp.qualitycontrol.dto;
import ch.swiftapp.erp.qualitycontrol.model.CheckResult;
import jakarta.validation.constraints.*; import java.time.LocalDate; import java.util.UUID;
public record QualityCheckRequest(@NotNull UUID inspectionPlanId, UUID productionOrderId, String checkedBy, @NotNull LocalDate checkDate, @NotNull CheckResult result, String notes) {}

