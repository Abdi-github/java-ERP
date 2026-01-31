package ch.swiftapp.erp.inventory.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Current stock level for a specific item in a specific warehouse.
 *
 * <p>Represents the quantity on hand. Updated by stock movements.</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "stock_levels",
        uniqueConstraints = @UniqueConstraint(columnNames = {"item_id", "item_type", "warehouse_id"}))
public class StockLevel extends BaseEntity {

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private StockItemType itemType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Builder.Default
    @Column(name = "quantity_on_hand", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantityOnHand = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "quantity_reserved", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantityReserved = BigDecimal.ZERO;

    /**
     * Returns quantity available for new allocations.
     */
    public BigDecimal getQuantityAvailable() {
        return quantityOnHand.subtract(quantityReserved);
    }
}

