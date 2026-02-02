package ch.swiftapp.erp.purchasing.web;

import ch.swiftapp.erp.purchasing.dto.PurchaseOrderLineRequest;
import ch.swiftapp.erp.purchasing.dto.PurchaseOrderRequest;
import ch.swiftapp.erp.purchasing.model.PurchaseOrderStatus;
import ch.swiftapp.erp.purchasing.service.PurchaseOrderService;
import ch.swiftapp.erp.purchasing.service.SupplierService;
import ch.swiftapp.erp.shared.model.VatRate;
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

import java.util.ArrayList;
import java.util.UUID;

@Controller
@RequestMapping("/app/purchasing/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PURCHASING:VIEW')")
public class PurchaseOrderViewController {

    private final PurchaseOrderService orderService;
    private final SupplierService supplierService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(required = false) PurchaseOrderStatus status,
                       @PageableDefault(size = 20) Pageable pageable, Model model) {
        var orders = (search != null && !search.isBlank()) ? orderService.search(search, pageable)
                : (status != null) ? orderService.findByStatus(status, pageable)
                : orderService.findAll(pageable);
        model.addAttribute("orders", orders);
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", PurchaseOrderStatus.values());
        return "app/purchasing/orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("order", orderService.findById(id));
        return "app/purchasing/orders/detail";
    }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("orderRequest", new PurchaseOrderRequest(null, null, null, null, new ArrayList<>()));
        model.addAttribute("suppliers", supplierService.findAllActive(Pageable.unpaged()));
        model.addAttribute("vatRates", VatRate.values());
        model.addAttribute("editMode", false);
        return "app/purchasing/orders/form";
    }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("orderRequest") PurchaseOrderRequest request,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) { addFormRefs(model, false); return "app/purchasing/orders/form"; }
        try {
            var o = orderService.create(request);
            ra.addFlashAttribute("successMessage", "Purchase order created");
            return "redirect:/app/purchasing/orders/" + o.id();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage()); addFormRefs(model, false);
            return "app/purchasing/orders/form";
        }
    }

    @PreAuthorize("hasAuthority('PURCHASING:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var o = orderService.findById(id);
        var lines = o.lines().stream().map(l -> new PurchaseOrderLineRequest(
                l.materialId(), l.description(), l.quantity(), l.unitPrice(),
                l.discountPct(), l.vatRate(), l.position())).toList();
        model.addAttribute("orderRequest", new PurchaseOrderRequest(
                o.supplierId(), o.orderDate(), o.expectedDeliveryDate(), o.notes(), lines));
        model.addAttribute("orderId", id);
        addFormRefs(model, true);
        return "app/purchasing/orders/form";
    }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id, @Valid @ModelAttribute("orderRequest") PurchaseOrderRequest request,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) { model.addAttribute("orderId", id); addFormRefs(model, true); return "app/purchasing/orders/form"; }
        try {
            orderService.update(id, request);
            ra.addFlashAttribute("successMessage", "Purchase order updated");
            return "redirect:/app/purchasing/orders/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage()); model.addAttribute("orderId", id); addFormRefs(model, true);
            return "app/purchasing/orders/form";
        }
    }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping("/{id}/submit")
    public String submit(@PathVariable UUID id, RedirectAttributes ra) {
        try { orderService.submit(id); ra.addFlashAttribute("successMessage", "Order submitted"); }
        catch (Exception e) { ra.addFlashAttribute("errorMessage", e.getMessage()); }
        return "redirect:/app/purchasing/orders/" + id;
    }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable UUID id, RedirectAttributes ra) {
        try { orderService.confirm(id); ra.addFlashAttribute("successMessage", "Order confirmed"); }
        catch (Exception e) { ra.addFlashAttribute("errorMessage", e.getMessage()); }
        return "redirect:/app/purchasing/orders/" + id;
    }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping("/{id}/receive")
    public String receive(@PathVariable UUID id, RedirectAttributes ra) {
        try { orderService.receive(id); ra.addFlashAttribute("successMessage", "Goods received"); }
        catch (Exception e) { ra.addFlashAttribute("errorMessage", e.getMessage()); }
        return "redirect:/app/purchasing/orders/" + id;
    }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping("/{id}/complete")
    public String complete(@PathVariable UUID id, RedirectAttributes ra) {
        try { orderService.complete(id); ra.addFlashAttribute("successMessage", "Order completed"); }
        catch (Exception e) { ra.addFlashAttribute("errorMessage", e.getMessage()); }
        return "redirect:/app/purchasing/orders/" + id;
    }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable UUID id, @RequestParam(defaultValue = "") String reason, RedirectAttributes ra) {
        try { orderService.cancel(id, reason); ra.addFlashAttribute("successMessage", "Order cancelled"); }
        catch (Exception e) { ra.addFlashAttribute("errorMessage", e.getMessage()); }
        return "redirect:/app/purchasing/orders/" + id;
    }

    @PreAuthorize("hasAuthority('PURCHASING:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        try { orderService.delete(id); ra.addFlashAttribute("successMessage", "Order deleted"); }
        catch (Exception e) { ra.addFlashAttribute("errorMessage", e.getMessage()); }
        return "redirect:/app/purchasing/orders";
    }

    private void addFormRefs(Model model, boolean editMode) {
        model.addAttribute("suppliers", supplierService.findAllActive(Pageable.unpaged()));
        model.addAttribute("vatRates", VatRate.values());
        model.addAttribute("editMode", editMode);
    }
}

