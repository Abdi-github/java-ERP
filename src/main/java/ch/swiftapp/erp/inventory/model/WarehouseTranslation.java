package ch.swiftapp.erp.inventory.model;

import ch.swiftapp.erp.shared.model.BaseTranslation;
import jakarta.persistence.*;
import lombok.*;

/**
 * Companion translation entity for {@link Warehouse}.
 * One row per supported locale ({@code de}, {@code fr}, {@code it}, {@code en}).
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(
    name = "warehouse_translations",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_warehouse_translations_warehouse_locale",
        columnNames = {"warehouse_id", "locale"}
    )
)
public class WarehouseTranslation extends BaseTranslation {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}

