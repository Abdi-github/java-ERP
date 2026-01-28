package ch.swiftapp.erp.auth.dto;

import java.util.UUID;

/**
 * Response DTO for {@link ch.swiftapp.erp.auth.model.Permission}.
 *
 * @param id          the permission UUID
 * @param code        the permission code (e.g. {@code SALES:VIEW})
 * @param description human-readable description
 * @param module      the ERP module this permission belongs to
 */
public record PermissionResponse(
        UUID id,
        String code,
        String description,
        String module
) {}

