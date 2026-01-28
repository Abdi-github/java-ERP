package ch.swiftapp.erp.auth.web;

import ch.swiftapp.erp.auth.dto.UserRequest;
import ch.swiftapp.erp.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.UUID;

/**
 * Thymeleaf view controller for user administration at {@code /app/admin/users}.
 */
@Controller
@RequestMapping("/app/admin/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ADMIN:USERS_VIEW')")
public class UserViewController {

    private final UserService userService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @PageableDefault(size = 20) Pageable pageable,
                       Model model) {
        var users = (search != null && !search.isBlank())
                ? userService.search(search, pageable)
                : userService.findAll(pageable);
        model.addAttribute("users", users);
        model.addAttribute("search", search);
        return "app/admin/users/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "app/admin/users/detail";
    }

    @PreAuthorize("hasAuthority('ADMIN:USERS_MANAGE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("userRequest", new UserRequest(null, null, null, null, null, null, null));
        model.addAttribute("allRoles", userService.findAllRoles());
        model.addAttribute("editMode", false);
        return "app/admin/users/form";
    }

    @PreAuthorize("hasAuthority('ADMIN:USERS_MANAGE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("userRequest") UserRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", userService.findAllRoles());
            model.addAttribute("editMode", false);
            return "app/admin/users/form";
        }
        try {
            var user = userService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully");
            return "redirect:/app/admin/users/" + user.id();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("allRoles", userService.findAllRoles());
            model.addAttribute("editMode", false);
            return "app/admin/users/form";
        }
    }

    @PreAuthorize("hasAuthority('ADMIN:USERS_MANAGE')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var user = userService.findById(id);
        var request = new UserRequest(
                user.username(), user.email(), null,
                user.firstName(), user.lastName(),
                user.enabled(), user.roles()
        );
        model.addAttribute("userRequest", request);
        model.addAttribute("userId", id);
        model.addAttribute("allRoles", userService.findAllRoles());
        model.addAttribute("editMode", true);
        return "app/admin/users/form";
    }

    @PreAuthorize("hasAuthority('ADMIN:USERS_MANAGE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("userRequest") UserRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("userId", id);
            model.addAttribute("allRoles", userService.findAllRoles());
            model.addAttribute("editMode", true);
            return "app/admin/users/form";
        }
        try {
            userService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
            return "redirect:/app/admin/users/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("userId", id);
            model.addAttribute("allRoles", userService.findAllRoles());
            model.addAttribute("editMode", true);
            return "app/admin/users/form";
        }
    }

    @PreAuthorize("hasAuthority('ADMIN:USERS_MANAGE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        userService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        return "redirect:/app/admin/users";
    }
}

