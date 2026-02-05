package ch.swiftapp.erp.qualitycontrol.dto;
import java.time.Instant; import java.util.UUID;
public record InspectionPlanResponse(UUID id, String planNumber, String name, String description, UUID productId, UUID materialId, Boolean active, Instant createdAt, Instant updatedAt) {}

