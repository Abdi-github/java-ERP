package ch.swiftapp.erp.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Request DTO for creating or scheduling a {@link ch.swiftapp.erp.notification.model.MailCampaign}.
 */
public record MailCampaignRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 2000) String description,
        @NotBlank @Size(max = 100) String templateCode,
        @Size(max = 10) String locale,
        @Size(max = 100) String targetSegment,
        Instant scheduledAt,
        @Size(max = 500) String subjectOverride
) {}

