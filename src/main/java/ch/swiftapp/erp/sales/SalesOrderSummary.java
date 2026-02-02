package ch.swiftapp.erp.sales;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Lightweight summary of a sales order for cross-module consumption (e.g. dashboard).
 */
public record SalesOrderSummary(
        UUID id,
        String orderNumber,
        String customerName,
        String status,
        BigDecimal totalAmount,
        String currency,
        LocalDate orderDate
) {}

