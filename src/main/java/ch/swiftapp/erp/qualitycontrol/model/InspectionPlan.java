package ch.swiftapp.erp.qualitycontrol.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data @Entity @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "inspection_plans")
public class InspectionPlan extends BaseEntity {
    @Column(name = "plan_number", nullable = false, unique = true, length = 30) private String planNumber;
    @Column(name = "name", nullable = false) private String name;
    @Column(name = "description", columnDefinition = "TEXT") private String description;
    @Column(name = "product_id") private UUID productId;
    @Column(name = "material_id") private UUID materialId;
    @Builder.Default @Column(name = "active", nullable = false) private Boolean active = true;
    @Column(name = "deleted_at") private Instant deletedAt;
}

