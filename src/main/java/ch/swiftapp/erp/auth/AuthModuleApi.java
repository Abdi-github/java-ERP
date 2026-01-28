package ch.swiftapp.erp.auth;

import ch.swiftapp.erp.auth.dto.UserResponse;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Public API for the Auth module.
 *
 * <p>Other Spring Modulith modules should depend on this interface only —
 * never on internal model, repository, or service classes.</p>
 */
public interface AuthModuleApi {

    /**
     * Find a user by their ID.
     */
    Optional<UserResponse> findUserById(UUID id);

    /**
     * Find a user by their username.
     */
    Optional<UserResponse> findUserByUsername(String username);

    /**
     * Get the currently authenticated username.
     *
     * @return the username, or "system" if not authenticated
     */
    String getCurrentUsername();

    /**
     * Find all enabled (non-deleted, non-locked) users.
     */
    List<UserResponse> findAllEnabledUsers();

    /**
     * Find all enabled users that have a specific role.
     */
    List<UserResponse> findAllByRole(String roleName);

    /**
     * Get the set of permission codes for a user (aggregated from all roles).
     *
     * @param userId the user UUID
     * @return set of permission codes (e.g. {@code SALES:VIEW}, {@code INVENTORY:CREATE})
     */
    Set<String> getPermissionsForUser(UUID userId);

    /**
     * Check whether a user has a specific permission.
     *
     * @param username       the username
     * @param permissionCode the permission code (e.g. {@code SALES:VIEW})
     * @return true if the user has the permission
     */
    boolean hasPermission(String username, String permissionCode);
}

