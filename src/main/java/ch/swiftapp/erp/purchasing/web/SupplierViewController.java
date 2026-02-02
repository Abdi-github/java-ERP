package ch.swiftapp.erp.purchasing.web;

import ch.swiftapp.erp.purchasing.dto.SupplierRequest;
import ch.swiftapp.erp.purchasing.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/app/purchasing/suppliers")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PURCHASING:VIEW')")
public class SupplierViewController {

    private final SupplierService supplierService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @PageableDefault(size = 20) Pageable pageable, Model model) {
        model.addAttribute("suppliers", (search != null && !search.isBlank())
                ? supplierService.search(search, pageable) : supplierService.findAll(pageable));
        model.addAttribute("search", search);
        return "app/purchasing/suppliers/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("supplier", supplierService.findById(id));
        return "app/purchasing/suppliers/detail";
    }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("supplierRequest", new SupplierRequest(
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
        model.addAttribute("editMode", false);
        return "app/purchasing/suppliers/form";
    }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("supplierRequest") SupplierRequest request,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) { model.addAttribute("editMode", false); return "app/purchasing/suppliers/form"; }
        try {
            var s = supplierService.create(request);
            ra.addFlashAttribute("successMessage", "Supplier created");
            return "redirect:/app/purchasing/suppliers/" + s.id();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage()); model.addAttribute("editMode", false);
            return "app/purchasing/suppliers/form";
        }
    }

    @PreAuthorize("hasAuthority('PURCHASING:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var s = supplierService.findById(id);
        model.addAttribute("supplierRequest", new SupplierRequest(
                s.supplierNumber(), s.companyName(), s.firstName(), s.lastName(),
                s.email(), s.phone(), s.street(), s.city(), s.postalCode(),
                s.canton(), s.country(), s.vatNumber(), s.paymentTerms(),
                s.contactPerson(), s.website(), s.notes(), s.active()));
        model.addAttribute("supplierId", id);
        model.addAttribute("editMode", true);
        return "app/purchasing/suppliers/form";
    }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id, @Valid @ModelAttribute("supplierRequest") SupplierRequest request,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) { model.addAttribute("supplierId", id); model.addAttribute("editMode", true); return "app/purchasing/suppliers/form"; }
        try {
            supplierService.update(id, request);
            ra.addFlashAttribute("successMessage", "Supplier updated");
            return "redirect:/app/purchasing/suppliers/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage()); model.addAttribute("supplierId", id); model.addAttribute("editMode", true);
            return "app/purchasing/suppliers/form";
        }
    }

    @PreAuthorize("hasAuthority('PURCHASING:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        supplierService.delete(id);
        ra.addFlashAttribute("successMessage", "Supplier deleted");
        return "redirect:/app/purchasing/suppliers";
    }
}

