package ch.swiftapp.erp.masterdata.model;

import ch.swiftapp.erp.shared.model.BaseTranslation;
import jakarta.persistence.*;
import lombok.*;

/**
 * Companion translation entity for {@link UnitOfMeasure}.
 * One row per supported locale ({@code de}, {@code fr}, {@code it}, {@code en}).
 * The {@code name} field (e.g. "Stück" / "Pièce" / "Pezzo") appears on invoices
 * and must be rendered in the customer's language.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(
    name = "uom_translations",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_uom_translations_uom_locale",
        columnNames = {"uom_id", "locale"}
    )
)
public class UnitOfMeasureTranslation extends BaseTranslation {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uom_id", nullable = false)
    private UnitOfMeasure unitOfMeasure;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;
}

