package ch.swiftapp.erp.sales.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A sales order placed by a customer for one or more watches/products.
 *
 * <p>Lifecycle: DRAFT → CONFIRMED → PROCESSING → SHIPPED → DELIVERED → COMPLETED.
 * A cancelled order may occur at any stage before COMPLETED.</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"customer", "lines"})
@ToString(exclude = {"customer", "lines"})
@Table(name = "sales_orders")
public class SalesOrder extends BaseEntity {

    @Column(name = "order_number", nullable = false, unique = true, length = 30)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private SalesOrderStatus status = SalesOrderStatus.DRAFT;

    @Builder.Default
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate = LocalDate.now();

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Builder.Default
    @Column(name = "subtotal", nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "vat_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal vatAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "CHF";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ── Shipping address ──────────────────────────────────────
    @Column(name = "shipping_street")
    private String shippingStreet;

    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    @Column(name = "shipping_canton", length = 50)
    private String shippingCanton;

    @Builder.Default
    @Column(name = "shipping_country", length = 3)
    private String shippingCountry = "CH";

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Builder.Default
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<SalesOrderLine> lines = new ArrayList<>();

    // ── Business methods ──────────────────────────────────────

    /**
     * Add a line item to this order.
     */
    public void addLine(SalesOrderLine line) {
        lines.add(line);
        line.setSalesOrder(this);
    }

    /**
     * Remove a line item from this order.
     */
    public void removeLine(SalesOrderLine line) {
        lines.remove(line);
        line.setSalesOrder(null);
    }

    /**
     * Recalculate subtotal, VAT, and total from order lines.
     */
    public void recalculateTotals() {
        this.subtotal = lines.stream()
                .map(SalesOrderLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.vatAmount = lines.stream()
                .map(SalesOrderLine::getVatAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalAmount = subtotal.add(vatAmount);
    }
}

