package ch.swiftapp.erp.auth.event;

import java.util.Set;
import java.util.UUID;

/**
 * Domain event published when a user's roles are changed.
 *
 * @param userId   the user ID
 * @param username the username
 * @param newRoles the new set of role names
 */
public record UserRoleChangedEvent(UUID userId, String username, Set<String> newRoles) {}

