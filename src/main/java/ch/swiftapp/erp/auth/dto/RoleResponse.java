package ch.swiftapp.erp.auth.dto;

import java.util.Set;
import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.auth.model.Role}.
 *
 * @param id           the role UUID
 * @param name         the role name (e.g. {@code ADMIN})
 * @param description  human-readable description
 * @param permissions  set of permission codes assigned to this role
 */
public record RoleResponse(
        UUID id,
        String name,
        String description,
        Set<String> permissions
) {
    /**
     * Convenience constructor for backwards compatibility (no permissions).
     */
    public RoleResponse(UUID id, String name, String description) {
        this(id, name, description, Set.of());
    }
}

