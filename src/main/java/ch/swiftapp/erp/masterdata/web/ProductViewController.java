package ch.swiftapp.erp.masterdata.web;

import ch.swiftapp.erp.masterdata.dto.ProductRequest;
import ch.swiftapp.erp.masterdata.service.BillOfMaterialService;
import ch.swiftapp.erp.masterdata.service.CategoryService;
import ch.swiftapp.erp.masterdata.service.ProductService;
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
 * Thymeleaf view controller for product management at {@code /app/masterdata/products}.
 */
@Controller
@RequestMapping("/app/masterdata/products")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('MASTERDATA:VIEW')")
public class ProductViewController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BillOfMaterialService bomService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @PageableDefault(size = 20) Pageable pageable,
                       Model model) {
        var products = (search != null && !search.isBlank())
                ? productService.search(search, pageable)
                : productService.findAll(pageable);

        model.addAttribute("products", products);
        model.addAttribute("search", search);
        return "app/masterdata/products/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("bomLines", bomService.findByProductId(id));
        return "app/masterdata/products/detail";
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("productRequest", new ProductRequest(null, null, null, null, null, null, null, null, null, null));
        model.addAttribute("categories", categoryService.findAllFlat());
        model.addAttribute("vatRates", VatRate.values());
        model.addAttribute("editMode", false);
        return "app/masterdata/products/form";
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("productRequest") ProductRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllFlat());
            model.addAttribute("vatRates", VatRate.values());
            model.addAttribute("editMode", false);
            return "app/masterdata/products/form";
        }
        try {
            var product = productService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Product created successfully");
            return "redirect:/app/masterdata/products/" + product.id();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.findAllFlat());
            model.addAttribute("vatRates", VatRate.values());
            model.addAttribute("editMode", false);
            return "app/masterdata/products/form";
        }
    }

    @PreAuthorize("hasAuthority('MASTERDATA:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var product = productService.findById(id);
        var request = new ProductRequest(
                product.sku(), product.name(), product.description(),
                product.categoryId(), product.unitPrice(), product.listPrice(),
                product.vatRate(), product.active(),
                product.nameTranslations(), product.descriptionTranslations()
        );
        model.addAttribute("productRequest", request);
        model.addAttribute("productId", id);
        model.addAttribute("categories", categoryService.findAllFlat());
        model.addAttribute("vatRates", VatRate.values());
        model.addAttribute("editMode", true);
        return "app/masterdata/products/form";
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("productRequest") ProductRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryService.findAllFlat());
            model.addAttribute("vatRates", VatRate.values());
            model.addAttribute("editMode", true);
            return "app/masterdata/products/form";
        }
        try {
            productService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully");
            return "redirect:/app/masterdata/products/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryService.findAllFlat());
            model.addAttribute("vatRates", VatRate.values());
            model.addAttribute("editMode", true);
            return "app/masterdata/products/form";
        }
    }

    @PreAuthorize("hasAuthority('MASTERDATA:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        productService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully");
        return "redirect:/app/masterdata/products";
    }
}

