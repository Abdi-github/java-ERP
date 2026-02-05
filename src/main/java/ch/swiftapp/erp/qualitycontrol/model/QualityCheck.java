package ch.swiftapp.erp.qualitycontrol.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Entity @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "inspectionPlan")
@ToString(exclude = "inspectionPlan")
@Table(name = "quality_checks")
public class QualityCheck extends BaseEntity {
    @Column(name = "check_number", nullable = false, unique = true, length = 30) private String checkNumber;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "inspection_plan_id", nullable = false) private InspectionPlan inspectionPlan;
    @Column(name = "production_order_id") private UUID productionOrderId;
    @Column(name = "checked_by") private String checkedBy;
    @Builder.Default @Column(name = "check_date", nullable = false) private LocalDate checkDate = LocalDate.now();
    @Enumerated(EnumType.STRING) @Column(name = "result", nullable = false, length = 30) private CheckResult result;
    @Column(name = "notes", columnDefinition = "TEXT") private String notes;
}

