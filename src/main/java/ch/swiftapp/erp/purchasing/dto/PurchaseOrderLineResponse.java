package ch.swiftapp.erp.purchasing.dto;

import ch.swiftapp.erp.shared.model.VatRate;
import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseOrderLineResponse(
        UUID id, UUID materialId, String materialName,
        String description, BigDecimal quantity, BigDecimal unitPrice,
        BigDecimal discountPct, VatRate vatRate,
        BigDecimal lineTotal, BigDecimal vatAmount, Integer position
) {}

