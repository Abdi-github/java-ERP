package ch.swiftapp.erp.inventory.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A physical storage location for products and materials.
 *
 * <p>Examples: Main Warehouse, Retail Store Stock Room, Assembly Workshop Storage.</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "warehouses")
public class Warehouse extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    /** Companion translation rows — one per supported locale (de, fr, it, en). */
    @BatchSize(size = 20)
    @OneToMany(mappedBy = "warehouse", fetch = FetchType.EAGER,
               cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WarehouseTranslation> translations = new ArrayList<>();
}

