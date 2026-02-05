package ch.swiftapp.erp.qualitycontrol.dto;
import jakarta.validation.constraints.*; import java.util.UUID;
public record InspectionPlanRequest(@NotBlank @Size(max = 30) String planNumber, @NotBlank @Size(max = 255) String name, String description, UUID productId, UUID materialId, Boolean active) {}

