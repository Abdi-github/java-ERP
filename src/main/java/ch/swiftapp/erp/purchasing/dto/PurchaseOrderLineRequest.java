package ch.swiftapp.erp.purchasing.dto;

import ch.swiftapp.erp.shared.model.VatRate;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseOrderLineRequest(
        @NotNull(message = "Material is required") UUID materialId,
        @Size(max = 500) String description,
        @NotNull @DecimalMin("0.0001") BigDecimal quantity,
        @NotNull @DecimalMin("0.0000") BigDecimal unitPrice,
        @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal discountPct,
        @NotNull VatRate vatRate,
        Integer position
) {}

