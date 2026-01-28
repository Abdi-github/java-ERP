package ch.swiftapp.erp.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

/**
 * Request DTO for creating or updating a {@link ch.swiftapp.erp.auth.model.Role}.
 *
 * @param name          unique role name (e.g. {@code SALES_MANAGER})
 * @param description   human-readable description
 * @param permissionIds set of permission UUIDs to assign
 */
public record RoleRequest(

        @NotBlank(message = "Role name is required")
        @Size(max = 50, message = "Role name must not exceed 50 characters")
        String name,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description,

        Set<UUID> permissionIds
) {}

