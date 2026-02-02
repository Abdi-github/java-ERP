package ch.swiftapp.erp.notification.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * A notification delivered to a specific user via one or more channels.
 *
 * <p>Every dispatched notification is persisted here, providing:</p>
 * <ul>
 *   <li>An in-app notification centre (status: PENDING → SENT → READ)</li>
 *   <li>A full audit trail of all user-facing communications (nDSG compliance)</li>
 *   <li>A retry queue for failed deliveries (via {@link #retryCount})</li>
 * </ul>
 *
 * <p>Cross-module references (e.g. {@code recipientUserId}) are stored as plain
 * {@link UUID} columns — never as JPA {@code @ManyToOne} to avoid module coupling.</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "notifications")
public class Notification extends BaseEntity {

    /** The internal user who should receive this notification (cross-module UUID ref). */
    @Column(name = "recipient_user_id", nullable = false)
    private UUID recipientUserId;

    /** Email address for EMAIL-channel delivery. May be null for IN_APP-only. */
    @Column(name = "recipient_email", length = 255)
    private String recipientEmail;

    /** Template code that was used to generate this notification. */
    @Column(name = "template_code", length = 100)
    private String templateCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status = NotificationStatus.PENDING;

    /** Email subject line (null for IN_APP-only notifications). */
    @Column(name = "subject", length = 500)
    private String subject;

    /** Rendered body — plain-text summary for IN_APP, HTML for EMAIL. */
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    /**
     * The type of the business entity that triggered this notification.
     * e.g. {@code "SALES_ORDER"}, {@code "PRODUCTION_ORDER"}
     */
    @Column(name = "reference_type", length = 100)
    private String referenceType;

    /** UUID of the related business entity — enables deep-link in notification centre. */
    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Builder.Default
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "read_at")
    private Instant readAt;
}

