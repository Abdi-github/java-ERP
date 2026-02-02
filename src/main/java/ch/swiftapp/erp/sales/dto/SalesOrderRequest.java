package ch.swiftapp.erp.sales.dto;

import ch.swiftapp.erp.sales.model.SalesOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating or updating a {@link ch.swiftapp.erp.sales.model.SalesOrder}.
 */
public record SalesOrderRequest(

        @NotNull(message = "Customer is required")
        UUID customerId,

        LocalDate orderDate,

        LocalDate deliveryDate,

        String notes,

        // ── Shipping address ──────────────────────────────
        String shippingStreet,
        String shippingCity,
        String shippingPostalCode,
        String shippingCanton,
        String shippingCountry,

        @Valid
        List<SalesOrderLineRequest> lines
) {}

