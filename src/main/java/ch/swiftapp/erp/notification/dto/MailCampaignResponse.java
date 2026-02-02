package ch.swiftapp.erp.notification.dto;

import ch.swiftapp.erp.notification.model.MailCampaignStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.notification.model.MailCampaign}.
 */
public record MailCampaignResponse(
        UUID id,
        String name,
        String description,
        String templateCode,
        String locale,
        String targetSegment,
        MailCampaignStatus status,
        Integer totalRecipients,
        Integer sentCount,
        Integer failedCount,
        Instant scheduledAt,
        Instant startedAt,
        Instant completedAt,
        String subjectOverride,
        String createdBy,
        Instant createdAt,
        Instant updatedAt
) {}

