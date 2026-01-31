package ch.swiftapp.erp.production.model;

import ch.swiftapp.erp.shared.model.BaseTranslation;
import jakarta.persistence.*;
import lombok.*;

/**
 * Companion translation entity for {@link WorkCenter}.
 * One row per supported locale ({@code de}, {@code fr}, {@code it}, {@code en}).
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(
    name = "work_center_translations",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_work_center_translations_wc_locale",
        columnNames = {"work_center_id", "locale"}
    )
)
public class WorkCenterTranslation extends BaseTranslation {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_center_id", nullable = false)
    private WorkCenter workCenter;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}

