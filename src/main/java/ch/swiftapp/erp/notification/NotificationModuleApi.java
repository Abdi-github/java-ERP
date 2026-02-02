package ch.swiftapp.erp.notification;

import java.util.UUID;

/**
 * Public API for the Notification module.
 *
 * <p>Other modules must <strong>only</strong> use this interface — never import
 * internal notification classes directly.</p>
 *
 * <p>The primary integration pattern is event-driven: other modules publish domain events
 * and this module listens. This interface covers the minority case where another module
 * needs to query notification state (e.g. unread count for the current user).</p>
 */
public interface NotificationModuleApi {

    /**
     * Count unread in-app notifications for the given user.
     *
     * @param userId the authenticated user's UUID
     * @return number of unread notifications
     */
    long countUnread(UUID userId);

    /**
     * Mark all in-app notifications as read for the given user.
     *
     * @param userId the authenticated user's UUID
     */
    void markAllRead(UUID userId);

    /**
     * Send an ad-hoc notification using a registered template code.
     *
     * <p>Use this for programmatic notifications that don't fit an existing domain event
     * (e.g. system alerts, admin broadcasts to a single user).</p>
     *
     * @param recipientUserId target user UUID
     * @param recipientEmail  target email address
     * @param templateCode    registered template code (e.g. {@code "DAILY_DIGEST"})
     * @param subject         email subject (overrides template default if not blank)
     * @param body            rendered message body
     * @param referenceType   optional business entity type (e.g. {@code "SALES_ORDER"})
     * @param referenceId     optional business entity UUID
     */
    void sendAdHoc(UUID recipientUserId, String recipientEmail,
                   String templateCode, String subject, String body,
                   String referenceType, UUID referenceId);
}

