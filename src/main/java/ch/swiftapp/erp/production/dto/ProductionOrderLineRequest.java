package ch.swiftapp.erp.production.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductionOrderLineRequest(
        @NotNull UUID materialId,
        @Size(max = 500) String description,
        @NotNull @DecimalMin("0.0001") BigDecimal plannedQuantity,
        @DecimalMin("0.0000") BigDecimal unitPrice,
        Integer position
) {}

