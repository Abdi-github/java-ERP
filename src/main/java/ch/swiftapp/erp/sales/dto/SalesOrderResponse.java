package ch.swiftapp.erp.sales.dto;

import ch.swiftapp.erp.sales.model.SalesOrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.sales.model.SalesOrder}.
 */
public record SalesOrderResponse(
        UUID id,
        String orderNumber,
        UUID customerId,
        String customerName,
        SalesOrderStatus status,
        LocalDate orderDate,
        LocalDate deliveryDate,
        BigDecimal subtotal,
        BigDecimal vatAmount,
        BigDecimal totalAmount,
        String currency,
        String notes,
        String shippingStreet,
        String shippingCity,
        String shippingPostalCode,
        String shippingCanton,
        String shippingCountry,
        List<SalesOrderLineResponse> lines,
        Instant createdAt,
        Instant updatedAt
) {}

