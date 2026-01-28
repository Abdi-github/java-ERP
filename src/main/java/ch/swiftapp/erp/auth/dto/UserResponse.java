package ch.swiftapp.erp.auth.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.auth.model.User}.
 */
public record UserResponse(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        String displayName,
        Boolean enabled,
        Boolean locked,
        Set<String> roles,
        Instant createdAt,
        Instant updatedAt
) {}

