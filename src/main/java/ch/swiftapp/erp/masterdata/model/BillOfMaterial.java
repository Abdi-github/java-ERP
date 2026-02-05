package ch.swiftapp.erp.masterdata.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Bill of Materials line — links a {@link Product} to a constituent {@link Material}.
 *
 * <p>Defines how many units of a material are required to manufacture one unit
 * of a product. Used by the production module to plan material requirements.</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "bill_of_materials",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "material_id"}))
public class BillOfMaterial extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_of_measure_id")
    private UnitOfMeasure unitOfMeasure;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "notes", length = 500)
    private String notes;
}

