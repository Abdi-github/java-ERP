package ch.swiftapp.erp.production.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProductionOrderRequest(
        @NotNull UUID productId,
        UUID workCenterId,
        @NotNull @DecimalMin("0.0001") BigDecimal plannedQuantity,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        @Min(0) Integer priority,
        String notes,
        @Valid List<ProductionOrderLineRequest> lines
) {}

