package ch.swiftapp.erp.sales.web;

import ch.swiftapp.erp.masterdata.MasterdataModuleApi;
import ch.swiftapp.erp.sales.dto.SalesOrderLineRequest;
import ch.swiftapp.erp.sales.dto.SalesOrderRequest;
import ch.swiftapp.erp.sales.model.SalesOrderStatus;
import ch.swiftapp.erp.sales.service.CustomerService;
import ch.swiftapp.erp.sales.service.SalesOrderService;
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

import java.util.ArrayList;
import java.util.UUID;

/**
 * Thymeleaf view controller for sales order management at {@code /app/sales/orders}.
 */
@Controller
@RequestMapping("/app/sales/orders")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('SALES:VIEW')")
public class SalesOrderViewController {

    private final SalesOrderService salesOrderService;
    private final CustomerService customerService;
    private final MasterdataModuleApi masterdataApi;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(required = false) SalesOrderStatus status,
                       @PageableDefault(size = 20) Pageable pageable,
                       Model model) {
        var orders = (search != null && !search.isBlank())
                ? salesOrderService.search(search, pageable)
                : (status != null)
                        ? salesOrderService.findByStatus(status, pageable)
                        : salesOrderService.findAll(pageable);

        model.addAttribute("orders", orders);
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", SalesOrderStatus.values());
        return "app/sales/orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("order", salesOrderService.findById(id));
        return "app/sales/orders/detail";
    }

    @PreAuthorize("hasAuthority('SALES:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("orderRequest", new SalesOrderRequest(
                null, null, null, null, null, null, null, null, null, new ArrayList<>()));
        model.addAttribute("customers", customerService.findAllActive(Pageable.unpaged()));
        model.addAttribute("vatRates", VatRate.values());
        model.addAttribute("editMode", false);
        return "app/sales/orders/form";
    }

    @PreAuthorize("hasAuthority('SALES:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("orderRequest") SalesOrderRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("customers", customerService.findAllActive(Pageable.unpaged()));
            model.addAttribute("vatRates", VatRate.values());
            model.addAttribute("editMode", false);
            return "app/sales/orders/form";
        }
        try {
            var order = salesOrderService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Sales order created successfully");
            return "redirect:/app/sales/orders/" + order.id();
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("customers", customerService.findAllActive(Pageable.unpaged()));
            model.addAttribute("vatRates", VatRate.values());
            model.addAttribute("editMode", false);
            return "app/sales/orders/form";
        }
    }

    @PreAuthorize("hasAuthority('SALES:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var order = salesOrderService.findById(id);
        var lineRequests = order.lines().stream()
                .map(l -> new SalesOrderLineRequest(
                        l.productId(), l.description(), l.quantity(), l.unitPrice(),
                        l.discountPct(), l.vatRate(), l.position()))
                .toList();

        var request = new SalesOrderRequest(
                order.customerId(), order.orderDate(), order.deliveryDate(), order.notes(),
                order.shippingStreet(), order.shippingCity(), order.shippingPostalCode(),
                order.shippingCanton(), order.shippingCountry(), lineRequests);

        model.addAttribute("orderRequest", request);
        model.addAttribute("orderId", id);
        model.addAttribute("customers", customerService.findAllActive(Pageable.unpaged()));
        model.addAttribute("vatRates", VatRate.values());
        model.addAttribute("editMode", true);
        return "app/sales/orders/form";
    }

    @PreAuthorize("hasAuthority('SALES:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("orderRequest") SalesOrderRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("orderId", id);
            model.addAttribute("customers", customerService.findAllActive(Pageable.unpaged()));
            model.addAttribute("vatRates", VatRate.values());
            model.addAttribute("editMode", true);
            return "app/sales/orders/form";
        }
        try {
            salesOrderService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Sales order updated successfully");
            return "redirect:/app/sales/orders/" + id;
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("orderId", id);
            model.addAttribute("customers", customerService.findAllActive(Pageable.unpaged()));
            model.addAttribute("vatRates", VatRate.values());
            model.addAttribute("editMode", true);
            return "app/sales/orders/form";
        }
    }

    // ── Status transitions ────────────────────────────────────

    @PreAuthorize("hasAuthority('SALES:CREATE')")
    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            salesOrderService.confirm(id);
            redirectAttributes.addFlashAttribute("successMessage", "Order confirmed successfully");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/app/sales/orders/" + id;
    }

    @PreAuthorize("hasAuthority('SALES:CREATE')")
    @PostMapping("/{id}/advance")
    public String advance(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            salesOrderService.advanceStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Order status advanced");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/app/sales/orders/" + id;
    }

    @PreAuthorize("hasAuthority('SALES:CREATE')")
    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable UUID id,
                         @RequestParam(required = false, defaultValue = "") String reason,
                         RedirectAttributes redirectAttributes) {
        try {
            salesOrderService.cancel(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Order cancelled");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/app/sales/orders/" + id;
    }

    @PreAuthorize("hasAuthority('SALES:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            salesOrderService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Order deleted");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/app/sales/orders";
    }
}

