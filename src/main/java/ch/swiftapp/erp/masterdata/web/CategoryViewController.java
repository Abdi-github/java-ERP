package ch.swiftapp.erp.masterdata.web;

import ch.swiftapp.erp.masterdata.dto.CategoryRequest;
import ch.swiftapp.erp.masterdata.service.CategoryService;
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
 * Thymeleaf view controller for category management at {@code /app/masterdata/categories}.
 */
@Controller
@RequestMapping("/app/masterdata/categories")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('MASTERDATA:VIEW')")
public class CategoryViewController {

    private final CategoryService categoryService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @PageableDefault(size = 20) Pageable pageable,
                       Model model) {
        var categories = (search != null && !search.isBlank())
                ? categoryService.search(search, pageable)
                : categoryService.findAll(pageable);

        model.addAttribute("categories", categories);
        model.addAttribute("search", search);
        return "app/masterdata/categories/list";
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("categoryRequest", new CategoryRequest(null, null, null, null, null));
        model.addAttribute("parentCategories", categoryService.findAllFlat());
        model.addAttribute("editMode", false);
        return "app/masterdata/categories/form";
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("categoryRequest") CategoryRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("parentCategories", categoryService.findAllFlat());
            model.addAttribute("editMode", false);
            return "app/masterdata/categories/form";
        }
        try {
            categoryService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Category created successfully");
            return "redirect:/app/masterdata/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("parentCategories", categoryService.findAllFlat());
            model.addAttribute("editMode", false);
            return "app/masterdata/categories/form";
        }
    }

    @PreAuthorize("hasAuthority('MASTERDATA:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var category = categoryService.findById(id);
        var request = new CategoryRequest(category.name(), category.description(), category.parentId(),
                category.nameTranslations(), category.descriptionTranslations());
        model.addAttribute("categoryRequest", request);
        model.addAttribute("categoryId", id);
        model.addAttribute("parentCategories", categoryService.findAllFlat());
        model.addAttribute("editMode", true);
        return "app/masterdata/categories/form";
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("categoryRequest") CategoryRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categoryId", id);
            model.addAttribute("parentCategories", categoryService.findAllFlat());
            model.addAttribute("editMode", true);
            return "app/masterdata/categories/form";
        }
        try {
            categoryService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully");
            return "redirect:/app/masterdata/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categoryId", id);
            model.addAttribute("parentCategories", categoryService.findAllFlat());
            model.addAttribute("editMode", true);
            return "app/masterdata/categories/form";
        }
    }

    @PreAuthorize("hasAuthority('MASTERDATA:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully");
        return "redirect:/app/masterdata/categories";
    }
}

