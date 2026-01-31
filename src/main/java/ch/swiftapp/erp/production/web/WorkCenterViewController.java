package ch.swiftapp.erp.production.web;

import ch.swiftapp.erp.production.dto.WorkCenterRequest;
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
import java.util.UUID;

@Controller @RequestMapping("/app/production/work-centers") @RequiredArgsConstructor
@PreAuthorize("hasAuthority('PRODUCTION:VIEW')")
public class WorkCenterViewController {
    private final WorkCenterService service;

    @GetMapping
    public String list(@RequestParam(required = false) String search, @PageableDefault(size = 20) Pageable p, Model m) {
        m.addAttribute("workCenters", (search != null && !search.isBlank()) ? service.search(search, p) : service.findAll(p));
        m.addAttribute("search", search);
        return "app/production/work-centers/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model m) { m.addAttribute("wc", service.findById(id)); return "app/production/work-centers/detail"; }

    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @GetMapping("/new")
    public String createForm(Model m) {
        m.addAttribute("wcRequest", new WorkCenterRequest(null, null, null, null, null, null, null, null));
        m.addAttribute("editMode", false); return "app/production/work-centers/form";
    }

    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("wcRequest") WorkCenterRequest r, BindingResult br, Model m, RedirectAttributes ra) {
        if (br.hasErrors()) { m.addAttribute("editMode", false); return "app/production/work-centers/form"; }
        try { var wc = service.create(r); ra.addFlashAttribute("successMessage", "Created"); return "redirect:/app/production/work-centers/" + wc.id(); }
        catch (Exception e) { m.addAttribute("errorMessage", e.getMessage()); m.addAttribute("editMode", false); return "app/production/work-centers/form"; }
    }

    @PreAuthorize("hasAuthority('PRODUCTION:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model m) {
        var wc = service.findById(id);
        m.addAttribute("wcRequest", new WorkCenterRequest(wc.code(), wc.name(), wc.description(), wc.capacityPerDay(), wc.costPerHour(), wc.active(), wc.nameTranslations(), wc.descriptionTranslations()));
        m.addAttribute("wcId", id); m.addAttribute("editMode", true); return "app/production/work-centers/form";
    }

    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id, @Valid @ModelAttribute("wcRequest") WorkCenterRequest r, BindingResult br, Model m, RedirectAttributes ra) {
        if (br.hasErrors()) { m.addAttribute("wcId", id); m.addAttribute("editMode", true); return "app/production/work-centers/form"; }
        try { service.update(id, r); ra.addFlashAttribute("successMessage", "Updated"); return "redirect:/app/production/work-centers/" + id; }
        catch (Exception e) { m.addAttribute("errorMessage", e.getMessage()); m.addAttribute("wcId", id); m.addAttribute("editMode", true); return "app/production/work-centers/form"; }
    }

    @PreAuthorize("hasAuthority('PRODUCTION:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) { service.delete(id); ra.addFlashAttribute("successMessage", "Deleted"); return "redirect:/app/production/work-centers"; }
}

