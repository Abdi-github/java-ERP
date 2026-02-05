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
 * A finished product — typically a Swiss luxury watch.
 *
 * <p>Products are the items sold to customers. Each product may have
 * a {@link BillOfMaterial} defining its constituent materials.</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "products")
public class Product extends BaseEntity {

    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @Column(name = "list_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal listPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "vat_rate", nullable = false, length = 50)
    private VatRate vatRate;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    /** Companion translation rows — one per supported locale (de, fr, it, en). */
    @BatchSize(size = 20)
    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER,
               cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductTranslation> translations = new ArrayList<>();
}

