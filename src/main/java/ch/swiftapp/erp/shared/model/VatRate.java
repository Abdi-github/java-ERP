package ch.swiftapp.erp.shared.model;

import java.math.BigDecimal;

/**
 * Swiss VAT (Mehrwertsteuer / TVA / IVA) rates as defined by Swiss tax law.
 *
 * <ul>
 *     <li><b>STANDARD_8_1</b> — 8.1% for most goods and services</li>
 *     <li><b>REDUCED_2_6</b> — 2.6% for food, medicine, books, newspapers</li>
 *     <li><b>ACCOMMODATION_3_8</b> — 3.8% for accommodation services</li>
 *     <li><b>EXEMPT</b> — 0% for healthcare, education, banking, insurance</li>
 * </ul>
 */
public enum VatRate {

    STANDARD_8_1(new BigDecimal("8.1")),
    REDUCED_2_6(new BigDecimal("2.6")),
    ACCOMMODATION_3_8(new BigDecimal("3.8")),
    EXEMPT(BigDecimal.ZERO);

    private final BigDecimal rate;

    VatRate(BigDecimal rate) {
        this.rate = rate;
    }

    /**
     * Returns the VAT percentage (e.g., 8.1 for standard rate).
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * Returns the VAT multiplier for calculating VAT amount (e.g., 0.081 for standard rate).
     */
    public BigDecimal getMultiplier() {
        return rate.divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP);
    }
}

