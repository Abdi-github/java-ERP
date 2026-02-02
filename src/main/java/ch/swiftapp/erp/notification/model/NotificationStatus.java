package ch.swiftapp.erp.notification.model;

/**
 * Lifecycle status of a {@link Notification}.
 *
 * <ul>
 *   <li>{@link #PENDING} — created, not yet dispatched</li>
 *   <li>{@link #SENT} — email delivered or in-app persisted successfully</li>
 *   <li>{@link #FAILED} — delivery failed; will be retried up to max retries</li>
 *   <li>{@link #READ} — in-app notification acknowledged by the user</li>
 *   <li>{@link #DISMISSED} — in-app notification dismissed without reading</li>
 * </ul>
 */
public enum NotificationStatus {
    PENDING,
    SENT,
    FAILED,
    READ,
    DISMISSED
}

