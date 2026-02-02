package ch.swiftapp.erp.sales.dto;

import ch.swiftapp.erp.shared.model.VatRate;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for creating or updating a {@link ch.swiftapp.erp.sales.model.SalesOrderLine}.
 */
public record SalesOrderLineRequest(

        @NotNull(message = "Product is required")
        UUID productId,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.0001", message = "Quantity must be > 0")
        BigDecimal quantity,

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.0000", message = "Unit price must be >= 0")
        BigDecimal unitPrice,

        @DecimalMin(value = "0.00", message = "Discount must be >= 0")
        @DecimalMax(value = "100.00", message = "Discount must be <= 100")
        BigDecimal discountPct,

        @NotNull(message = "VAT rate is required")
        VatRate vatRate,

        Integer position
) {}

