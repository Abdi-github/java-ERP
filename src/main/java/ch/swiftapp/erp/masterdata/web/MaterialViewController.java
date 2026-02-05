package ch.swiftapp.erp.masterdata.web;

import ch.swiftapp.erp.masterdata.dto.MaterialRequest;
import ch.swiftapp.erp.masterdata.service.CategoryService;
import ch.swiftapp.erp.masterdata.service.MaterialService;
import ch.swiftapp.erp.masterdata.service.UnitOfMeasureService;
import ch.swiftapp.erp.shared.model.VatRate;
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
 * Thymeleaf view controller for material management at {@code /app/masterdata/materials}.
 */
@Controller
@RequestMapping("/app/masterdata/materials")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('MASTERDATA:VIEW')")
public class MaterialViewController {

    private final MaterialService materialService;
    private final CategoryService categoryService;
    private final UnitOfMeasureService unitOfMeasureService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @PageableDefault(size = 20) Pageable pageable,
                       Model model) {
        var materials = (search != null && !search.isBlank())
                ? materialService.search(search, pageable)
                : materialService.findAll(pageable);

        model.addAttribute("materials", materials);
        model.addAttribute("search", search);
        return "app/masterdata/materials/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("material", materialService.findById(id));
        return "app/masterdata/materials/detail";
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("materialRequest", new MaterialRequest(null, null, null, null, null, null, null, null, null, null));
        populateFormDropdowns(model);
        model.addAttribute("editMode", false);
        return "app/masterdata/materials/form";
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("materialRequest") MaterialRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDropdowns(model);
            model.addAttribute("editMode", false);
            return "app/masterdata/materials/form";
        }
        try {
            var material = materialService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Material created successfully");
            return "redirect:/app/masterdata/materials/" + material.id();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            populateFormDropdowns(model);
            model.addAttribute("editMode", false);
            return "app/masterdata/materials/form";
        }
    }

    @PreAuthorize("hasAuthority('MASTERDATA:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var material = materialService.findById(id);
        var request = new MaterialRequest(
                material.sku(), material.name(), material.description(),
                material.categoryId(), material.unitOfMeasureId(),
                material.unitPrice(), material.vatRate(), material.minimumStock(),
                material.nameTranslations(), material.descriptionTranslations()
        );
        model.addAttribute("materialRequest", request);
        model.addAttribute("materialId", id);
        populateFormDropdowns(model);
        model.addAttribute("editMode", true);
        return "app/masterdata/materials/form";
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("materialRequest") MaterialRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("materialId", id);
            populateFormDropdowns(model);
            model.addAttribute("editMode", true);
            return "app/masterdata/materials/form";
        }
        try {
            materialService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Material updated successfully");
            return "redirect:/app/masterdata/materials/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("materialId", id);
            populateFormDropdowns(model);
            model.addAttribute("editMode", true);
            return "app/masterdata/materials/form";
        }
    }

    @PreAuthorize("hasAuthority('MASTERDATA:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        materialService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Material deleted successfully");
        return "redirect:/app/masterdata/materials";
    }

    private void populateFormDropdowns(Model model) {
        model.addAttribute("categories", categoryService.findAllFlat());
        model.addAttribute("unitsOfMeasure", unitOfMeasureService.findAllFlat());
        model.addAttribute("vatRates", VatRate.values());
    }
}

