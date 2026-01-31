package ch.swiftapp.erp.production.web;

import ch.swiftapp.erp.production.dto.ProductionOrderLineRequest;
import ch.swiftapp.erp.production.dto.ProductionOrderRequest;
import ch.swiftapp.erp.production.model.ProductionOrderStatus;
import ch.swiftapp.erp.production.service.ProductionOrderService;
import ch.swiftapp.erp.production.service.WorkCenterService;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

@Controller @RequestMapping("/app/production/orders") @RequiredArgsConstructor
@PreAuthorize("hasAuthority('PRODUCTION:VIEW')")
public class ProductionOrderViewController {
    private final ProductionOrderService orderService;
    private final WorkCenterService workCenterService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(required = false) ProductionOrderStatus status,
                       @PageableDefault(size = 20) Pageable p, Model m) {
        var orders = (search != null && !search.isBlank()) ? orderService.search(search, p)
                : (status != null) ? orderService.findByStatus(status, p) : orderService.findAll(p);
        m.addAttribute("orders", orders);
        m.addAttribute("search", search); m.addAttribute("selectedStatus", status);
        m.addAttribute("statuses", ProductionOrderStatus.values());
        return "app/production/orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model m) { m.addAttribute("order", orderService.findById(id)); return "app/production/orders/detail"; }

    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @GetMapping("/new")
    public String createForm(Model m) {
        m.addAttribute("orderRequest", new ProductionOrderRequest(null, null, null, null, null, null, null, new ArrayList<>()));
        addFormRefs(m, false); return "app/production/orders/form";
    }

    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("orderRequest") ProductionOrderRequest r, BindingResult br, Model m, RedirectAttributes ra) {
        if (br.hasErrors()) { addFormRefs(m, false); return "app/production/orders/form"; }
        try { var o = orderService.create(r); ra.addFlashAttribute("successMessage", "Created"); return "redirect:/app/production/orders/" + o.id(); }
        catch (Exception e) { m.addAttribute("errorMessage", e.getMessage()); addFormRefs(m, false); return "app/production/orders/form"; }
    }

    @PreAuthorize("hasAuthority('PRODUCTION:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model m) {
        var o = orderService.findById(id);
        var lines = o.lines().stream().map(l -> new ProductionOrderLineRequest(l.materialId(), l.description(), l.plannedQuantity(), l.unitPrice(), l.position())).toList();
        m.addAttribute("orderRequest", new ProductionOrderRequest(o.productId(), o.workCenterId(), o.plannedQuantity(), o.plannedStartDate(), o.plannedEndDate(), o.priority(), o.notes(), lines));
        m.addAttribute("orderId", id); addFormRefs(m, true); return "app/production/orders/form";
    }

    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id, @Valid @ModelAttribute("orderRequest") ProductionOrderRequest r, BindingResult br, Model m, RedirectAttributes ra) {
        if (br.hasErrors()) { m.addAttribute("orderId", id); addFormRefs(m, true); return "app/production/orders/form"; }
        try { orderService.update(id, r); ra.addFlashAttribute("successMessage", "Updated"); return "redirect:/app/production/orders/" + id; }
        catch (Exception e) { m.addAttribute("errorMessage", e.getMessage()); m.addAttribute("orderId", id); addFormRefs(m, true); return "app/production/orders/form"; }
    }

    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}/release") public String release(@PathVariable UUID id, RedirectAttributes ra) { try { orderService.release(id); ra.addFlashAttribute("successMessage", "Released"); } catch (Exception e) { ra.addFlashAttribute("errorMessage", e.getMessage()); } return "redirect:/app/production/orders/" + id; }
    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}/start") public String start(@PathVariable UUID id, RedirectAttributes ra) { try { orderService.start(id); ra.addFlashAttribute("successMessage", "Started"); } catch (Exception e) { ra.addFlashAttribute("errorMessage", e.getMessage()); } return "redirect:/app/production/orders/" + id; }
    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}/complete") public String complete(@PathVariable UUID id, @RequestParam(required = false) BigDecimal completedQty, @RequestParam(required = false) BigDecimal scrapQty, RedirectAttributes ra) { try { orderService.complete(id, completedQty, scrapQty); ra.addFlashAttribute("successMessage", "Completed"); } catch (Exception e) { ra.addFlashAttribute("errorMessage", e.getMessage()); } return "redirect:/app/production/orders/" + id; }
    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}/hold") public String hold(@PathVariable UUID id, RedirectAttributes ra) { try { orderService.hold(id); ra.addFlashAttribute("successMessage", "On hold"); } catch (Exception e) { ra.addFlashAttribute("errorMessage", e.getMessage()); } return "redirect:/app/production/orders/" + id; }
    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}/resume") public String resume(@PathVariable UUID id, RedirectAttributes ra) { try { orderService.resume(id); ra.addFlashAttribute("successMessage", "Resumed"); } catch (Exception e) { ra.addFlashAttribute("errorMessage", e.getMessage()); } return "redirect:/app/production/orders/" + id; }
    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}/cancel") public String cancel(@PathVariable UUID id, @RequestParam(defaultValue = "") String reason, RedirectAttributes ra) { try { orderService.cancel(id, reason); ra.addFlashAttribute("successMessage", "Cancelled"); } catch (Exception e) { ra.addFlashAttribute("errorMessage", e.getMessage()); } return "redirect:/app/production/orders/" + id; }
    @PreAuthorize("hasAuthority('PRODUCTION:DELETE')")
    @PostMapping("/{id}/delete") public String delete(@PathVariable UUID id, RedirectAttributes ra) { try { orderService.delete(id); ra.addFlashAttribute("successMessage", "Deleted"); } catch (Exception e) { ra.addFlashAttribute("errorMessage", e.getMessage()); } return "redirect:/app/production/orders"; }

    private void addFormRefs(Model m, boolean edit) {
        m.addAttribute("workCenters", workCenterService.findAllActive(Pageable.unpaged()));
        m.addAttribute("editMode", edit);
    }
}

