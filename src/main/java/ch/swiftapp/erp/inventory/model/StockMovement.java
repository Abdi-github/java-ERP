package ch.swiftapp.erp.inventory.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Records a single stock movement event — goods in, goods out, transfer, or adjustment.
 *
 * <p>Stock movements are immutable audit records. Once created they are never modified.</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "stock_movements")
public class StockMovement extends BaseEntity {

    @Column(name = "reference_number", nullable = false, unique = true, length = 50)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private MovementType movementType;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private StockItemType itemType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_warehouse_id")
    private Warehouse sourceWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_warehouse_id")
    private Warehouse targetWarehouse;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "movement_date", nullable = false)
    private Instant movementDate;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "source_document_type", length = 50)
    private String sourceDocumentType;

    @Column(name = "source_document_id")
    private UUID sourceDocumentId;
}

