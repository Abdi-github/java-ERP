package ch.swiftapp.erp.production.dto;

import ch.swiftapp.erp.production.model.ProductionOrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProductionOrderResponse(
        UUID id, String orderNumber, UUID productId, String productName,
        UUID workCenterId, String workCenterName,
        ProductionOrderStatus status,
        BigDecimal plannedQuantity, BigDecimal completedQuantity, BigDecimal scrapQuantity,
        LocalDate plannedStartDate, LocalDate plannedEndDate,
        LocalDate actualStartDate, LocalDate actualEndDate,
        BigDecimal estimatedCost, BigDecimal actualCost, String currency,
        Integer priority, String notes,
        List<ProductionOrderLineResponse> lines,
        Instant createdAt, Instant updatedAt
) {}

