package ch.swiftapp.erp.auth.api;

import ch.swiftapp.erp.auth.dto.PermissionResponse;
import ch.swiftapp.erp.auth.dto.RoleRequest;
import ch.swiftapp.erp.auth.dto.RoleResponse;
import ch.swiftapp.erp.auth.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for role &amp; permission management — JSON API at {@code /api/v1/roles}.
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleRestController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN:ROLES_VIEW')")
    public Page<RoleResponse> list(Pageable pageable) {
        return roleService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN:ROLES_VIEW')")
    public RoleResponse getById(@PathVariable UUID id) {
        return roleService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN:ROLES_MANAGE')")
    public RoleResponse create(@Valid @RequestBody RoleRequest request) {
        return roleService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN:ROLES_MANAGE')")
    public RoleResponse update(@PathVariable UUID id,
                               @Valid @RequestBody RoleRequest request) {
        return roleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN:ROLES_MANAGE')")
    public void delete(@PathVariable UUID id) {
        roleService.delete(id);
    }

    // ── Permission endpoints ──────────────────────────────────

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('ADMIN:ROLES_VIEW')")
    public List<PermissionResponse> listPermissions() {
        return roleService.findAllPermissions();
    }

    @GetMapping("/permissions/grouped")
    @PreAuthorize("hasAuthority('ADMIN:ROLES_VIEW')")
    public Map<String, List<PermissionResponse>> listPermissionsGrouped() {
        return roleService.findAllPermissionsGroupedByModule();
    }
}

