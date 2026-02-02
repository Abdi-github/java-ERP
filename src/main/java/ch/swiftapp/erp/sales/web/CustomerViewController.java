package ch.swiftapp.erp.sales.web;

import ch.swiftapp.erp.sales.dto.CustomerRequest;
import ch.swiftapp.erp.sales.service.CustomerService;
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

/**
 * Thymeleaf view controller for customer management at {@code /app/sales/customers}.
 */
@Controller
@RequestMapping("/app/sales/customers")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('SALES:VIEW')")
public class CustomerViewController {

    private final CustomerService customerService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @PageableDefault(size = 20) Pageable pageable,
                       Model model) {
        var customers = (search != null && !search.isBlank())
                ? customerService.search(search, pageable)
                : customerService.findAll(pageable);

        model.addAttribute("customers", customers);
        model.addAttribute("search", search);
        return "app/sales/customers/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("customer", customerService.findById(id));
        return "app/sales/customers/detail";
    }

    @PreAuthorize("hasAuthority('SALES:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("customerRequest", new CustomerRequest(
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
        model.addAttribute("editMode", false);
        return "app/sales/customers/form";
    }

    @PreAuthorize("hasAuthority('SALES:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("customerRequest") CustomerRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("editMode", false);
            return "app/sales/customers/form";
        }
        try {
            var customer = customerService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Customer created successfully");
            return "redirect:/app/sales/customers/" + customer.id();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("editMode", false);
            return "app/sales/customers/form";
        }
    }

    @PreAuthorize("hasAuthority('SALES:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var customer = customerService.findById(id);
        var request = new CustomerRequest(
                customer.customerNumber(), customer.companyName(),
                customer.firstName(), customer.lastName(),
                customer.email(), customer.phone(),
                customer.street(), customer.city(), customer.postalCode(),
                customer.canton(), customer.country(), customer.vatNumber(),
                customer.paymentTerms(), customer.creditLimit(),
                customer.notes(), customer.active()
        );
        model.addAttribute("customerRequest", request);
        model.addAttribute("customerId", id);
        model.addAttribute("editMode", true);
        return "app/sales/customers/form";
    }

    @PreAuthorize("hasAuthority('SALES:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("customerRequest") CustomerRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("customerId", id);
            model.addAttribute("editMode", true);
            return "app/sales/customers/form";
        }
        try {
            customerService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Customer updated successfully");
            return "redirect:/app/sales/customers/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("customerId", id);
            model.addAttribute("editMode", true);
            return "app/sales/customers/form";
        }
    }

    @PreAuthorize("hasAuthority('SALES:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        customerService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Customer deleted successfully");
        return "redirect:/app/sales/customers";
    }
}

