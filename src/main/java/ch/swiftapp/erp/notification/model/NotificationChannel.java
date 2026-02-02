package ch.swiftapp.erp.notification.model;

/**
 * The delivery channel for a notification.
 *
 * <ul>
 *   <li>{@link #EMAIL} — sent via SMTP using JavaMailSender</li>
 *   <li>{@link #IN_APP} — persisted to the notifications table and shown
 *       in the notification bell in the Thymeleaf layout</li>
 *   <li>{@link #BOTH} — delivered via both channels simultaneously</li>
 * </ul>
 */
public enum NotificationChannel {
    EMAIL,
    IN_APP,
    BOTH
}

