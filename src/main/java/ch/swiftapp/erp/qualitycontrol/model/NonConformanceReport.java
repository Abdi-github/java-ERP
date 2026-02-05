package ch.swiftapp.erp.qualitycontrol.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Data @Entity @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "qualityCheck")
@ToString(exclude = "qualityCheck")
@Table(name = "non_conformance_reports")
public class NonConformanceReport extends BaseEntity {
    @Column(name = "ncr_number", nullable = false, unique = true, length = 30) private String ncrNumber;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "quality_check_id", nullable = false) private QualityCheck qualityCheck;
    @Enumerated(EnumType.STRING) @Column(name = "severity", nullable = false, length = 30) private NcrSeverity severity;
    @Column(name = "description", nullable = false, columnDefinition = "TEXT") private String description;
    @Column(name = "corrective_action", columnDefinition = "TEXT") private String correctiveAction;
    @Enumerated(EnumType.STRING) @Builder.Default @Column(name = "status", nullable = false, length = 30) private NcrStatus status = NcrStatus.OPEN;
    @Column(name = "closed_at") private Instant closedAt;
}

