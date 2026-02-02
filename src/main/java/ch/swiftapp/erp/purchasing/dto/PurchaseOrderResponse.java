package ch.swiftapp.erp.purchasing.dto;

import ch.swiftapp.erp.purchasing.model.PurchaseOrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PurchaseOrderResponse(
        UUID id, String orderNumber, UUID supplierId, String supplierName,
        PurchaseOrderStatus status, LocalDate orderDate,
        LocalDate expectedDeliveryDate, LocalDate actualDeliveryDate,
        BigDecimal subtotal, BigDecimal vatAmount, BigDecimal totalAmount,
        String currency, String notes,
        List<PurchaseOrderLineResponse> lines,
        Instant createdAt, Instant updatedAt
) {}

