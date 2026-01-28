package ch.swiftapp.erp.auth.service;

import ch.swiftapp.erp.auth.dto.UserRequest;
import ch.swiftapp.erp.auth.dto.UserResponse;
import ch.swiftapp.erp.auth.dto.RoleResponse;
import ch.swiftapp.erp.auth.event.UserCreatedEvent;
import ch.swiftapp.erp.auth.event.UserRoleChangedEvent;
import ch.swiftapp.erp.auth.model.Permission;
import ch.swiftapp.erp.auth.model.Role;
import ch.swiftapp.erp.auth.model.User;
import ch.swiftapp.erp.auth.repository.RoleRepository;
import ch.swiftapp.erp.auth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing users and roles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * List all active, non-deleted users with pagination.
     */
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAllByDeletedAtIsNull(pageable)
                .map(this::toResponse);
    }

    /**
     * Search users by username, email, or name.
     */
    public Page<UserResponse> search(String query, Pageable pageable) {
        return userRepository.searchUsers(query, pageable)
                .map(this::toResponse);
    }

    /**
     * Find a user by ID.
     */
    public UserResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    /**
     * Find a user by ID, returning Optional.
     */
    public Optional<UserResponse> findByIdOptional(UUID id) {
        return userRepository.findById(id)
                .filter(u -> u.getDeletedAt() == null)
                .map(this::toResponse);
    }

    /**
     * Find a user by username.
     */
    public Optional<UserResponse> findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCaseAndDeletedAtIsNull(username)
                .map(this::toResponse);
    }

    /**
     * Create a new user.
     */
    @Transactional
    public UserResponse create(UserRequest request) {
        log.info("Creating user: username={}", request.username());

        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new IllegalArgumentException("Username already exists: " + request.username());
        }
        
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        var user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(request.enabled() != null ? request.enabled() : true);
        user.setLocked(false);

        if (request.roleNames() != null && !request.roleNames().isEmpty()) {
            var roles = resolveRoles(request.roleNames());
            user.setRoles(roles);
        }

        user = userRepository.save(user);

        // TODO: Verify UserCreatedEvent includes all user details
        eventPublisher.publishEvent(new UserCreatedEvent(
                user.getId(), user.getUsername(), user.getEmail()));

        log.info("Created user id={} username={}", user.getId(), user.getUsername());
        return toResponse(user);
    }

    /**
     * Update an existing user.
     */
    @Transactional
    public UserResponse update(UUID id, UserRequest request) {
        log.info("Updating user id={}", id);

        var user = findEntityById(id);

        if (!user.getUsername().equalsIgnoreCase(request.username())
                && userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new IllegalArgumentException("Username already exists: " + request.username());
        }
        
        if (!user.getEmail().equalsIgnoreCase(request.email())
                && userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(request.enabled() != null ? request.enabled() : user.getEnabled());

        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        if (request.roleNames() != null) {
            var oldRoles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
            var newRoles = resolveRoles(request.roleNames());
            user.setRoles(newRoles);

            if (!oldRoles.equals(request.roleNames())) {
                eventPublisher.publishEvent(new UserRoleChangedEvent(
                        user.getId(), user.getUsername(), request.roleNames()));
            }
        }

        user = userRepository.save(user);
        
        log.info("Updated user id={}", id);
        return toResponse(user);
    }

    /**
     * Soft-delete a user.
     */
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting user id={}", id);
        var user = findEntityById(id);
        
        user.setDeletedAt(Instant.now());
        
        userRepository.save(user);
    }
    }

    /**
     * Find all enabled, non-deleted, non-locked users.
     */
    public List<UserResponse> findAllEnabled() {
        return userRepository.findAllByEnabledTrueAndLockedFalseAndDeletedAtIsNull()
                .stream().map(this::toResponse).toList();
    }

    /**
     * Find all enabled users with a specific role.
     */
    public List<UserResponse> findAllByRole(String roleName) {
        return userRepository.findAllByRoleName(roleName)
                .stream().map(this::toResponse).toList();
    }

    /**
     * List all available roles.
     */
    public List<RoleResponse> findAllRoles() {
        return roleRepository.findAll().stream()
                .map(r -> new RoleResponse(
                        r.getId(),
                        r.getName(),
                        r.getDescription(),
                        r.getPermissions().stream().map(Permission::getCode).collect(Collectors.toSet())))
                .toList();
    }

    // ── Private helpers ───────────────────────────────────────

    private User findEntityById(UUID id) {
        return userRepository.findById(id)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        var roles = new HashSet<Role>();
        for (String name : roleNames) {
            roleRepository.findByNameIgnoreCase(name)
                    .ifPresent(roles::add);
        }
        return roles;
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getDisplayName(),
                user.getEnabled(),
                user.getLocked(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}

