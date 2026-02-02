package ch.swiftapp.erp.sales;

import java.math.BigDecimal;

/**
 * Monthly revenue summary for cross-module consumption (e.g. dashboard charts).
 */
public record MonthlyRevenueSummary(
        int year,
        int month,
        String monthLabel,
        BigDecimal revenue
) {}

