package ch.swiftapp.erp.masterdata.model;

import ch.swiftapp.erp.shared.model.BaseTranslation;
import jakarta.persistence.*;
import lombok.*;

/**
 * Companion translation entity for {@link Product}.
 * One row per supported locale ({@code de}, {@code fr}, {@code it}, {@code en}).
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(
    name = "product_translations",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_product_translations_product_locale",
        columnNames = {"product_id", "locale"}
    )
)
public class ProductTranslation extends BaseTranslation {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Translated product name for this locale. */
    @Column(name = "name", length = 255)
    private String name;

    /** Translated product description for this locale. */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}

