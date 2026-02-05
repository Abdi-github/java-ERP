package ch.swiftapp.erp.masterdata.model;

import ch.swiftapp.erp.shared.model.BaseTranslation;
import jakarta.persistence.*;
import lombok.*;

/**
 * Companion translation entity for {@link Category}.
 * One row per supported locale ({@code de}, {@code fr}, {@code it}, {@code en}).
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(
    name = "category_translations",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_category_translations_category_locale",
        columnNames = {"category_id", "locale"}
    )
)
public class CategoryTranslation extends BaseTranslation {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}

