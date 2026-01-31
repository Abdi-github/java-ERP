package ch.swiftapp.erp.production.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductionOrderLineResponse(
        UUID id, UUID materialId, String materialName,
        String description, BigDecimal plannedQuantity, BigDecimal actualQuantity,
        BigDecimal unitPrice, BigDecimal lineCost, Integer position
) {}

