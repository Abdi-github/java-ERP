package ch.swiftapp.erp.notification.model;

import ch.swiftapp.erp.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * A DB-stored notification template keyed by {@code code + channel + locale}.
 *
 * <p>Allows non-developer administrators to update notification wording
 * without a code deployment. The {@code bodyTemplate} field contains either
 * a Thymeleaf template fragment path (for HTML emails) or an inline text
 * template (for in-app notifications).</p>
 *
 * <p>Resolution order when looking up a template: exact locale → fallback to {@code de}
 * (company primary) → any active template for the code.</p>
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "notification_templates",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_notification_template",
                columnNames = {"code", "channel", "locale"}))
public class NotificationTemplate extends BaseEntity {

    /** Semantic identifier — e.g. {@code SALES_ORDER_CONFIRMED}. */
    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    /** BCP-47 language subtag: {@code de}, {@code fr}, {@code it}, {@code en}. */
    @Column(name = "locale", nullable = false, length = 10)
    private String locale;

    /** Email subject line — may contain simple placeholders like {@code #{orderNumber}}. */
    @Column(name = "subject", length = 500)
    private String subject;

    /**
     * For EMAIL templates: Thymeleaf template fragment path
     * (e.g. {@code email/sales-order-confirmed}).
     * For IN_APP templates: inline text with simple placeholders.
     */
    @Column(name = "body_template", nullable = false, columnDefinition = "TEXT")
    private String bodyTemplate;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;
}

