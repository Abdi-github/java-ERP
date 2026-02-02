package ch.swiftapp.erp.sales.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import ch.swiftapp.erp.shared.model.VatRate;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A single line item within a {@link SalesOrder}.
 *
 * <p>Each line references a product by UUID (modulith boundary — no direct JPA
 * relation to the masterdata module's Product entity).</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"salesOrder"})
@ToString(exclude = {"salesOrder"})
@Table(name = "sales_order_lines")
public class SalesOrderLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    /** Product UUID — cross-module reference (no JPA relation to masterdata). */
    @Column(name = "product_id", nullable = false)
    private java.util.UUID productId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @Builder.Default
    @Column(name = "discount_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPct = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "vat_rate", nullable = false, length = 50)
    private VatRate vatRate = VatRate.STANDARD_8_1;

    @Builder.Default
    @Column(name = "line_total", nullable = false, precision = 19, scale = 4)
    private BigDecimal lineTotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "position", nullable = false)
    private Integer position = 0;

    /**
     * Calculate and set the line total after applying discount.
     * {@code lineTotal = quantity * unitPrice * (1 - discountPct/100)}
     */
    public void calculateLineTotal() {
        BigDecimal gross = quantity.multiply(unitPrice);
        if (discountPct != null && discountPct.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                    discountPct.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            this.lineTotal = gross.multiply(discountMultiplier).setScale(4, RoundingMode.HALF_UP);
        } else {
            this.lineTotal = gross.setScale(4, RoundingMode.HALF_UP);
        }
    }

    /**
     * Returns the VAT amount for this line.
     */
    public BigDecimal getVatAmount() {
        return lineTotal.multiply(vatRate.getMultiplier()).setScale(4, RoundingMode.HALF_UP);
    }
}

