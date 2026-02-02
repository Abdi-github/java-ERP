package ch.swiftapp.erp.notification.dto;

import ch.swiftapp.erp.notification.model.NotificationChannel;
import ch.swiftapp.erp.notification.model.NotificationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.notification.model.Notification}.
 */
public record NotificationResponse(
        UUID id,
        UUID recipientUserId,
        String recipientEmail,
        String templateCode,
        NotificationChannel channel,
        NotificationStatus status,
        String subject,
        String body,
        String referenceType,
        UUID referenceId,
        Integer retryCount,
        Instant sentAt,
        Instant readAt,
        Instant createdAt
) {}

