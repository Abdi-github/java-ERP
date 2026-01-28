package ch.swiftapp.erp.auth.event;

import java.util.Set;
import java.util.UUID;

/**
 * Domain event published when a role's permissions are changed.
 *
 * <p>Listeners can use this to invalidate permission caches
 * or refresh active sessions.</p>
 *
 * @param roleId         the role UUID
 * @param roleName       the role name
 * @param permissionCodes the new set of permission codes
 */
public record RolePermissionsChangedEvent(UUID roleId, String roleName, Set<String> permissionCodes) {}

