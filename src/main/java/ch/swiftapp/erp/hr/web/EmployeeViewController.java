package ch.swiftapp.erp.hr.web;

import ch.swiftapp.erp.hr.dto.EmployeeRequest;
import ch.swiftapp.erp.hr.service.DepartmentService;
import ch.swiftapp.erp.hr.service.EmployeeService;
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
@RequestMapping("/app/hr/employees")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('HR:VIEW')")
public class EmployeeViewController {
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    @GetMapping
    public String list(@RequestParam(required = false) String search, @PageableDefault(size = 20) Pageable pageable, Model model) {
        model.addAttribute("employees", (search != null && !search.isBlank()) ? employeeService.search(search, pageable) : employeeService.findAll(pageable));
        model.addAttribute("search", search);
        return "app/hr/employees/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("employee", employeeService.findById(id));
        return "app/hr/employees/detail";
    }

    @PreAuthorize("hasAuthority('HR:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("employeeRequest", new EmployeeRequest(null, null, null, null, null, null, null, null, null, null, null));
        model.addAttribute("departments", departmentService.findAllActive());
        model.addAttribute("editMode", false);
        return "app/hr/employees/form";
    }

    @PreAuthorize("hasAuthority('HR:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("employeeRequest") EmployeeRequest request, BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) { model.addAttribute("departments", departmentService.findAllActive()); model.addAttribute("editMode", false); return "app/hr/employees/form"; }
        try {
            var emp = employeeService.create(request);
            ra.addFlashAttribute("successMessage", "Employee created");
            return "redirect:/app/hr/employees/" + emp.id();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage()); model.addAttribute("departments", departmentService.findAllActive()); model.addAttribute("editMode", false);
            return "app/hr/employees/form";
        }
    }

    @PreAuthorize("hasAuthority('HR:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var emp = employeeService.findById(id);
        model.addAttribute("employeeRequest", new EmployeeRequest(emp.employeeNumber(), emp.firstName(), emp.lastName(), emp.email(), emp.phone(), emp.hireDate(), emp.terminationDate(), emp.departmentId(), emp.position(), emp.salary(), emp.active()));
        model.addAttribute("employeeId", id);
        model.addAttribute("departments", departmentService.findAllActive());
        model.addAttribute("editMode", true);
        return "app/hr/employees/form";
    }

    @PreAuthorize("hasAuthority('HR:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id, @Valid @ModelAttribute("employeeRequest") EmployeeRequest request, BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) { model.addAttribute("employeeId", id); model.addAttribute("departments", departmentService.findAllActive()); model.addAttribute("editMode", true); return "app/hr/employees/form"; }
        employeeService.update(id, request);
        ra.addFlashAttribute("successMessage", "Employee updated");
        return "redirect:/app/hr/employees/" + id;
    }

    @PreAuthorize("hasAuthority('HR:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        employeeService.delete(id);
        ra.addFlashAttribute("successMessage", "Employee deleted");
        return "redirect:/app/hr/employees";
    }
}

