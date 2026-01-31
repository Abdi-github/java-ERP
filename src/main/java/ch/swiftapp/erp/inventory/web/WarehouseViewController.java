package ch.swiftapp.erp.inventory.web;

import ch.swiftapp.erp.inventory.dto.WarehouseRequest;
import ch.swiftapp.erp.inventory.service.WarehouseService;
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
 * Thymeleaf view controller for warehouse management at {@code /app/inventory/warehouses}.
 */
@Controller
@RequestMapping("/app/inventory/warehouses")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('INVENTORY:VIEW')")
public class WarehouseViewController {

    private final WarehouseService warehouseService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @PageableDefault(size = 20) Pageable pageable,
                       Model model) {
        var warehouses = (search != null && !search.isBlank())
                ? warehouseService.search(search, pageable)
                : warehouseService.findAll(pageable);

        model.addAttribute("warehouses", warehouses);
        model.addAttribute("search", search);
        return "app/inventory/warehouses/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("warehouse", warehouseService.findById(id));
        return "app/inventory/warehouses/detail";
    }

    @PreAuthorize("hasAuthority('INVENTORY:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("warehouseRequest", new WarehouseRequest(null, null, null, null, true, null, null));
        model.addAttribute("editMode", false);
        return "app/inventory/warehouses/form";
    }

    @PreAuthorize("hasAuthority('INVENTORY:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("warehouseRequest") WarehouseRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("editMode", false);
            return "app/inventory/warehouses/form";
        }
        try {
            warehouseService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Warehouse created successfully");
            return "redirect:/app/inventory/warehouses";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("editMode", false);
            return "app/inventory/warehouses/form";
        }
    }

    @PreAuthorize("hasAuthority('INVENTORY:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var warehouse = warehouseService.findById(id);
        var request = new WarehouseRequest(
                warehouse.code(), warehouse.name(),
                warehouse.description(), warehouse.address(), warehouse.active(),
                warehouse.nameTranslations(), warehouse.descriptionTranslations());
        model.addAttribute("warehouseRequest", request);
        model.addAttribute("warehouseId", id);
        model.addAttribute("editMode", true);
        return "app/inventory/warehouses/form";
    }

    @PreAuthorize("hasAuthority('INVENTORY:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("warehouseRequest") WarehouseRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("warehouseId", id);
            model.addAttribute("editMode", true);
            return "app/inventory/warehouses/form";
        }
        try {
            warehouseService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Warehouse updated successfully");
            return "redirect:/app/inventory/warehouses";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("warehouseId", id);
            model.addAttribute("editMode", true);
            return "app/inventory/warehouses/form";
        }
    }

    @PreAuthorize("hasAuthority('INVENTORY:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        warehouseService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Warehouse deleted successfully");
        return "redirect:/app/inventory/warehouses";
    }
}

