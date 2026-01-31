package ch.swiftapp.erp.production.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data @Entity @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"workCenter", "lines"})
@ToString(exclude = {"workCenter", "lines"})
@Table(name = "production_orders")
public class ProductionOrder extends BaseEntity {

    @Column(name = "order_number", nullable = false, unique = true, length = 30)
    private String orderNumber;

    /** Product being manufactured — cross-module reference. */
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_center_id")
    private WorkCenter workCenter;

    @Enumerated(EnumType.STRING) @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private ProductionOrderStatus status = ProductionOrderStatus.PLANNED;

    @Column(name = "planned_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal plannedQuantity;

    @Builder.Default
    @Column(name = "completed_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal completedQuantity = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "scrap_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal scrapQuantity = BigDecimal.ZERO;

    @Column(name = "planned_start_date") private LocalDate plannedStartDate;
    @Column(name = "planned_end_date")   private LocalDate plannedEndDate;
    @Column(name = "actual_start_date")  private LocalDate actualStartDate;
    @Column(name = "actual_end_date")    private LocalDate actualEndDate;

    @Builder.Default
    @Column(name = "estimated_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal estimatedCost = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "actual_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal actualCost = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "CHF";

    @Builder.Default
    @Column(name = "priority", nullable = false)
    private Integer priority = 0;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Builder.Default
    @OneToMany(mappedBy = "productionOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<ProductionOrderLine> lines = new ArrayList<>();

    public void addLine(ProductionOrderLine line) { lines.add(line); line.setProductionOrder(this); }
    public void removeLine(ProductionOrderLine line) { lines.remove(line); line.setProductionOrder(null); }

    public void recalculateEstimatedCost() {
        this.estimatedCost = lines.stream()
                .map(ProductionOrderLine::getLineCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

