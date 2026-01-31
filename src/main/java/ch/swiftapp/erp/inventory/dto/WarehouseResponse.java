package ch.swiftapp.erp.inventory.dto;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
/**
 * Response DTO for {@link ch.swiftapp.erp.inventory.model.Warehouse}.
 */
public record WarehouseResponse(
        UUID id,
        String code,
        String name,
        String description,
        String address,
        Boolean active,
        Instant createdAt,
        Instant updatedAt,
        Map<String, String> nameTranslations,
        Map<String, String> descriptionTranslations
) {}
