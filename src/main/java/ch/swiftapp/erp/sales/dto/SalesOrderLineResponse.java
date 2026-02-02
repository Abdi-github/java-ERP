package ch.swiftapp.erp.sales.dto;

import ch.swiftapp.erp.shared.model.VatRate;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.sales.model.SalesOrderLine}.
 */
public record SalesOrderLineResponse(
        UUID id,
        UUID productId,
        String productName,
        String description,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal discountPct,
        VatRate vatRate,
        BigDecimal lineTotal,
        BigDecimal vatAmount,
        Integer position
) {}

