package ch.swiftapp.erp.masterdata.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit of measure for materials and BOM quantities.
 *
 * <p>Examples: PCS (pieces), GRM (grams), MTR (meters), SET (sets).</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "units_of_measure")
public class UnitOfMeasure extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    /** Companion translation rows — one per supported locale (de, fr, it, en). */
    @BatchSize(size = 20)
    @OneToMany(mappedBy = "unitOfMeasure", fetch = FetchType.EAGER,
               cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnitOfMeasureTranslation> translations = new ArrayList<>();
}

