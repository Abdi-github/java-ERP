package ch.swiftapp.erp.production;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Lightweight summary of a production order for cross-module consumption (e.g. dashboard).
 */
public record ProductionOrderSummary(
        UUID id,
        String orderNumber,
        String productName,
        String status,
        BigDecimal plannedQuantity,
        LocalDate plannedEndDate
) {}

