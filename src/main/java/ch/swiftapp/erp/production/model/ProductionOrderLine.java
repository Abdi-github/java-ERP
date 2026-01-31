package ch.swiftapp.erp.production.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Data @Entity @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"productionOrder"})
@ToString(exclude = {"productionOrder"})
@Table(name = "production_order_lines")
public class ProductionOrderLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_id", nullable = false)
    private ProductionOrder productionOrder;

    @Column(name = "material_id", nullable = false)
    private UUID materialId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "planned_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal plannedQuantity;

    @Builder.Default
    @Column(name = "actual_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal actualQuantity = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "line_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal lineCost = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "position", nullable = false)
    private Integer position = 0;

    public void calculateLineCost() {
        this.lineCost = plannedQuantity.multiply(unitPrice).setScale(4, RoundingMode.HALF_UP);
    }
}

