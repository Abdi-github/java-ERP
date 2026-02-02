package ch.swiftapp.erp.notification.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * A mass-mail campaign targeting a user segment or all customers.
 *
 * <p>Campaigns are processed by {@link ch.swiftapp.erp.notification.service.MailCampaignService}
 * in paginated batches using a dedicated thread pool, respecting SMTP rate limits.
 * Progress is tracked via {@link #sentCount} and {@link #failedCount}.</p>
 *
 * <p>A campaign is restartable: if status is {@link MailCampaignStatus#FAILED} or
 * the app restarts mid-campaign, the scheduler can re-queue from where it left off.</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "mail_campaigns")
public class MailCampaign extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Registered template code used to render each recipient's email. */
    @Column(name = "template_code", nullable = false, length = 100)
    private String templateCode;

    /** BCP-47 locale for template resolution: {@code de}, {@code fr}, {@code it}, {@code en}. */
    @Builder.Default
    @Column(name = "locale", nullable = false, length = 10)
    private String locale = "de";

    /**
     * Target audience identifier.
     * Examples: {@code ALL_USERS}, {@code ROLE_SALES}, {@code ALL_CUSTOMERS}.
     */
    @Column(name = "target_segment", length = 100)
    private String targetSegment;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private MailCampaignStatus status = MailCampaignStatus.DRAFT;

    @Builder.Default
    @Column(name = "total_recipients", nullable = false)
    private Integer totalRecipients = 0;

    @Builder.Default
    @Column(name = "sent_count", nullable = false)
    private Integer sentCount = 0;

    @Builder.Default
    @Column(name = "failed_count", nullable = false)
    private Integer failedCount = 0;

    /** When to start sending — null means immediate dispatch on approval. */
    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    /** Optional override for the template's default subject line. */
    @Column(name = "subject_override", length = 500)
    private String subjectOverride;
}

