package ch.swiftapp.erp.hr.web;

import ch.swiftapp.erp.hr.dto.DepartmentRequest;
import ch.swiftapp.erp.hr.service.DepartmentService;
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

import java.util.UUID;

@Controller
@RequestMapping("/app/hr/departments")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('HR:VIEW')")
public class DepartmentViewController {
    private final DepartmentService departmentService;

    @GetMapping
    public String list(@RequestParam(required = false) String search, @PageableDefault(size = 20) Pageable pageable, Model model) {
        model.addAttribute("departments", (search != null && !search.isBlank()) ? departmentService.search(search, pageable) : departmentService.findAll(pageable));
        model.addAttribute("search", search);
        return "app/hr/departments/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("department", departmentService.findById(id));
        return "app/hr/departments/detail";
    }

    @PreAuthorize("hasAuthority('HR:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("departmentRequest", new DepartmentRequest(null, null, null, null, null, null, null));
        model.addAttribute("editMode", false);
        return "app/hr/departments/form";
    }

    @PreAuthorize("hasAuthority('HR:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("departmentRequest") DepartmentRequest request, BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) { model.addAttribute("editMode", false); return "app/hr/departments/form"; }
        try {
            var dept = departmentService.create(request);
            ra.addFlashAttribute("successMessage", "Department created");
            return "redirect:/app/hr/departments/" + dept.id();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage()); model.addAttribute("editMode", false);
            return "app/hr/departments/form";
        }
    }

    @PreAuthorize("hasAuthority('HR:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var dept = departmentService.findById(id);
        model.addAttribute("departmentRequest", new DepartmentRequest(dept.code(), dept.name(), dept.description(), dept.managerId(), dept.active(), dept.nameTranslations(), dept.descriptionTranslations()));
        model.addAttribute("departmentId", id);
        model.addAttribute("editMode", true);
        return "app/hr/departments/form";
    }

    @PreAuthorize("hasAuthority('HR:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id, @Valid @ModelAttribute("departmentRequest") DepartmentRequest request, BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) { model.addAttribute("departmentId", id); model.addAttribute("editMode", true); return "app/hr/departments/form"; }
        departmentService.update(id, request);
        ra.addFlashAttribute("successMessage", "Department updated");
        return "redirect:/app/hr/departments/" + id;
    }

    @PreAuthorize("hasAuthority('HR:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        departmentService.delete(id);
        ra.addFlashAttribute("successMessage", "Department deleted");
        return "redirect:/app/hr/departments";
    }
}

