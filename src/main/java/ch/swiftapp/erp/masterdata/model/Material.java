package ch.swiftapp.erp.masterdata.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import ch.swiftapp.erp.shared.model.VatRate;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Raw material or component used in watch manufacturing.
 *
 * <p>Examples: movements, sapphire crystals, straps, dials, screws, gold cases.
 * Materials are consumed by production orders and tracked in inventory.</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "materials")
public class Material extends BaseEntity {

    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_of_measure_id")
    private UnitOfMeasure unitOfMeasure;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "vat_rate", nullable = false, length = 50)
    private VatRate vatRate;

    @Column(name = "minimum_stock", nullable = false, precision = 19, scale = 4)
    private BigDecimal minimumStock;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    /** Companion translation rows — one per supported locale (de, fr, it, en). */
    @BatchSize(size = 20)
    @OneToMany(mappedBy = "material", fetch = FetchType.EAGER,
               cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaterialTranslation> translations = new ArrayList<>();
}

