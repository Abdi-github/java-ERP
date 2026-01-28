package ch.swiftapp.erp.auth.web;

import ch.swiftapp.erp.auth.dto.RoleRequest;
import ch.swiftapp.erp.auth.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.UUID;

/**
 * Thymeleaf view controller for role &amp; permission management at {@code /app/admin/roles}.
 */
@Controller
@RequestMapping("/app/admin/roles")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ADMIN:ROLES_VIEW')")
public class RoleViewController {

    private final RoleService roleService;

    @GetMapping
    public String list(@PageableDefault(size = 20) Pageable pageable, Model model) {
        model.addAttribute("roles", roleService.findAll(pageable));
        return "app/admin/roles/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("role", roleService.findById(id));
        model.addAttribute("permissionsByModule", roleService.findAllPermissionsGroupedByModule());
        return "app/admin/roles/detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ADMIN:ROLES_MANAGE')")
    public String createForm(Model model) {
        model.addAttribute("roleRequest", new RoleRequest(null, null, null));
        model.addAttribute("permissionsByModule", roleService.findAllPermissionsGroupedByModule());
        model.addAttribute("editMode", false);
        return "app/admin/roles/form";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN:ROLES_MANAGE')")
    public String create(@Valid @ModelAttribute("roleRequest") RoleRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("permissionsByModule", roleService.findAllPermissionsGroupedByModule());
            model.addAttribute("editMode", false);
            return "app/admin/roles/form";
        }
        try {
            var role = roleService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Role created successfully");
            return "redirect:/app/admin/roles/" + role.id();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("permissionsByModule", roleService.findAllPermissionsGroupedByModule());
            model.addAttribute("editMode", false);
            return "app/admin/roles/form";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('ADMIN:ROLES_MANAGE')")
    public String editForm(@PathVariable UUID id, Model model) {
        var role = roleService.findById(id);
        // Resolve permission IDs from codes
        var allPermissions = roleService.findAllPermissions();
        var assignedIds = allPermissions.stream()
                .filter(p -> role.permissions().contains(p.code()))
                .map(ch.swiftapp.erp.auth.dto.PermissionResponse::id)
                .collect(java.util.stream.Collectors.toSet());

        var request = new RoleRequest(role.name(), role.description(), assignedIds);
        model.addAttribute("roleRequest", request);
        model.addAttribute("roleId", id);
        model.addAttribute("permissionsByModule", roleService.findAllPermissionsGroupedByModule());
        model.addAttribute("assignedPermissionIds", assignedIds);
        model.addAttribute("editMode", true);
        return "app/admin/roles/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN:ROLES_MANAGE')")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("roleRequest") RoleRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roleId", id);
            model.addAttribute("permissionsByModule", roleService.findAllPermissionsGroupedByModule());
            model.addAttribute("editMode", true);
            return "app/admin/roles/form";
        }
        try {
            roleService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Role updated successfully");
            return "redirect:/app/admin/roles/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roleId", id);
            model.addAttribute("permissionsByModule", roleService.findAllPermissionsGroupedByModule());
            model.addAttribute("editMode", true);
            return "app/admin/roles/form";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('ADMIN:ROLES_MANAGE')")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            roleService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Role deleted successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/app/admin/roles";
    }
}

