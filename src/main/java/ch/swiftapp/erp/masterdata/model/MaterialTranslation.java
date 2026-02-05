package ch.swiftapp.erp.masterdata.model;

import ch.swiftapp.erp.shared.model.BaseTranslation;
import jakarta.persistence.*;
import lombok.*;

/**
 * Companion translation entity for {@link Material}.
 * One row per supported locale ({@code de}, {@code fr}, {@code it}, {@code en}).
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(
    name = "material_translations",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_material_translations_material_locale",
        columnNames = {"material_id", "locale"}
    )
)
public class MaterialTranslation extends BaseTranslation {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}

