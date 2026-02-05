package ch.swiftapp.erp.qualitycontrol.web;

import ch.swiftapp.erp.qualitycontrol.dto.InspectionPlanRequest;
import ch.swiftapp.erp.qualitycontrol.service.InspectionPlanService;
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

@Controller @RequestMapping("/app/quality-control/inspection-plans") @RequiredArgsConstructor
@PreAuthorize("hasAuthority('QC:VIEW')")
public class InspectionPlanViewController {
    private final InspectionPlanService service;

    @GetMapping
    public String list(@RequestParam(required = false) String search, @PageableDefault(size = 20) Pageable p, Model model) {
        model.addAttribute("plans", (search != null && !search.isBlank()) ? service.search(search, p) : service.findAll(p));
        model.addAttribute("search", search); return "app/quality-control/inspection-plans/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) { model.addAttribute("plan", service.findById(id)); return "app/quality-control/inspection-plans/detail"; }

    @PreAuthorize("hasAuthority('QC:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("planRequest", new InspectionPlanRequest(null, null, null, null, null, null));
        model.addAttribute("editMode", false); return "app/quality-control/inspection-plans/form";
    }

    @PreAuthorize("hasAuthority('QC:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("planRequest") InspectionPlanRequest r, BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) { model.addAttribute("editMode", false); return "app/quality-control/inspection-plans/form"; }
        try {
            var plan = service.create(r); ra.addFlashAttribute("successMessage", "Inspection plan created");
            return "redirect:/app/quality-control/inspection-plans/" + plan.id();
        } catch (IllegalArgumentException e) { model.addAttribute("errorMessage", e.getMessage()); model.addAttribute("editMode", false); return "app/quality-control/inspection-plans/form"; }
    }

    @PreAuthorize("hasAuthority('QC:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var p = service.findById(id);
        model.addAttribute("planRequest", new InspectionPlanRequest(p.planNumber(), p.name(), p.description(), p.productId(), p.materialId(), p.active()));
        model.addAttribute("planId", id); model.addAttribute("editMode", true); return "app/quality-control/inspection-plans/form";
    }

    @PreAuthorize("hasAuthority('QC:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id, @Valid @ModelAttribute("planRequest") InspectionPlanRequest r, BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) { model.addAttribute("planId", id); model.addAttribute("editMode", true); return "app/quality-control/inspection-plans/form"; }
        service.update(id, r); ra.addFlashAttribute("successMessage", "Inspection plan updated"); return "redirect:/app/quality-control/inspection-plans/" + id;
    }

    @PreAuthorize("hasAuthority('QC:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) { service.delete(id); ra.addFlashAttribute("successMessage", "Inspection plan deleted"); return "redirect:/app/quality-control/inspection-plans"; }
}

