package ch.swiftapp.erp.inventory.dto;

import ch.swiftapp.erp.inventory.model.MovementType;
import ch.swiftapp.erp.inventory.model.StockItemType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for creating a stock movement.
 */
public record StockMovementRequest(

        @NotNull(message = "Movement type is required")
        MovementType movementType,

        @NotNull(message = "Item ID is required")
        UUID itemId,

        @NotNull(message = "Item type is required")
        StockItemType itemType,

        UUID sourceWarehouseId,

        UUID targetWarehouseId,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.0001", message = "Quantity must be greater than 0")
        BigDecimal quantity,

        @Size(max = 500, message = "Reason must not exceed 500 characters")
        String reason
) {}

