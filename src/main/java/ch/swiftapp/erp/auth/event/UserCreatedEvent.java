package ch.swiftapp.erp.auth.event;

import java.util.UUID;

/**
 * Domain event published when a new user is created.
 *
 * @param userId   the ID of the newly created user
 * @param username the username
 * @param email    the user's email
 */
public record UserCreatedEvent(UUID userId, String username, String email) {}

