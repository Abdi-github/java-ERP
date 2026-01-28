package ch.swiftapp.erp.auth.service;

import ch.swiftapp.erp.auth.dto.PermissionResponse;
import ch.swiftapp.erp.auth.dto.RoleRequest;
import ch.swiftapp.erp.auth.dto.RoleResponse;
import ch.swiftapp.erp.auth.event.RolePermissionsChangedEvent;
import ch.swiftapp.erp.auth.model.Permission;
import ch.swiftapp.erp.auth.model.Role;
import ch.swiftapp.erp.auth.repository.PermissionRepository;
import ch.swiftapp.erp.auth.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing roles and their permission assignments.
 *
 * <p>Provides CRUD operations for roles and allows assigning/revoking
 * granular permissions. Publishes {@link RolePermissionsChangedEvent}
 * when a role's permissions are modified.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ApplicationEventPublisher eventPublisher;

    // ── Role Queries ──────────────────────────────────────────

    /**
     * List all roles with pagination.
     */
    public Page<RoleResponse> findAll(Pageable pageable) {
        return roleRepository.findAll(pageable).map(this::toResponse);
    }

    /**
     * List all roles (unpaged).
     */
    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Find a role by ID.
     */
    public RoleResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    // ── Role CRUD ─────────────────────────────────────────────

    /**
     * Create a new role with optional permission assignments.
     */
    @Transactional
    public RoleResponse create(RoleRequest request) {
        log.info("Creating role: name={}", request.name());

        if (roleRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Role already exists: " + request.name());
        }

        var role = new Role();
        role.setName(request.name().toUpperCase());
        role.setDescription(request.description());

        if (request.permissionIds() != null && !request.permissionIds().isEmpty()) {
            var permissions = resolvePermissions(request.permissionIds());
            role.setPermissions(permissions);
        }

        role = roleRepository.save(role);
        log.info("Created role id={} name={} with {} permissions",
                role.getId(), role.getName(), role.getPermissions().size());

        return toResponse(role);
    }

    /**
     * Update an existing role and its permission assignments.
     */
    @Transactional
    public RoleResponse update(UUID id, RoleRequest request) {
        log.info("Updating role id={}", id);

        var role = findEntityById(id);

        if (!role.getName().equalsIgnoreCase(request.name())
                && roleRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Role already exists: " + request.name());
        }

        role.setName(request.name().toUpperCase());
        role.setDescription(request.description());

        if (request.permissionIds() != null) {
            var newPermissions = resolvePermissions(request.permissionIds());
            role.getPermissions().clear();
            role.getPermissions().addAll(newPermissions);

            var permCodes = newPermissions.stream()
                    .map(Permission::getCode)
                    .collect(Collectors.toSet());

            eventPublisher.publishEvent(new RolePermissionsChangedEvent(
                    role.getId(), role.getName(), permCodes));
        }

        role = roleRepository.save(role);
        log.info("Updated role id={} name={}", role.getId(), role.getName());
        return toResponse(role);
    }

    /**
     * Delete a role by ID.
     */
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting role id={}", id);
        var role = findEntityById(id);

        // Prevent deletion of built-in ADMIN role
        if ("ADMIN".equalsIgnoreCase(role.getName())) {
            throw new IllegalArgumentException("The ADMIN role cannot be deleted");
        }

        roleRepository.delete(role);
        log.info("Deleted role id={} name={}", id, role.getName());
    }

    // ── Permission Queries ────────────────────────────────────

    /**
     * List all permissions ordered by module and code.
     */
    public List<PermissionResponse> findAllPermissions() {
        return permissionRepository.findAllByOrderByModuleAscCodeAsc().stream()
                .map(this::toPermissionResponse)
                .toList();
    }

    /**
     * List all permissions grouped by module.
     */
    public Map<String, List<PermissionResponse>> findAllPermissionsGroupedByModule() {
        return findAllPermissions().stream()
                .collect(Collectors.groupingBy(
                        PermissionResponse::module,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    // ── Internal helpers ──────────────────────────────────────

    /**
     * Find a role entity by ID (for internal use).
     */
    Role findEntityById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + id));
    }

    private Set<Permission> resolvePermissions(Set<UUID> permissionIds) {
        var permissions = permissionRepository.findAllByIdIn(permissionIds);
        return new HashSet<>(permissions);
    }

    private RoleResponse toResponse(Role role) {
        var permCodes = role.getPermissions().stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());

        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                permCodes
        );
    }

    private PermissionResponse toPermissionResponse(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getCode(),
                permission.getDescription(),
                permission.getModule()
        );
    }
}

