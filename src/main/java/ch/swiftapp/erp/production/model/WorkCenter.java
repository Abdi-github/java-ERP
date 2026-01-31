package ch.swiftapp.erp.production.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data @Entity @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "work_centers")
public class WorkCenter extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "capacity_per_day", nullable = false, precision = 19, scale = 4)
    private BigDecimal capacityPerDay = BigDecimal.ONE;

    @Builder.Default
    @Column(name = "cost_per_hour", nullable = false, precision = 19, scale = 4)
    private BigDecimal costPerHour = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    /** Companion translation rows — one per supported locale (de, fr, it, en). */
    @BatchSize(size = 20)
    @OneToMany(mappedBy = "workCenter", fetch = FetchType.EAGER,
               cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkCenterTranslation> translations = new ArrayList<>();
}

