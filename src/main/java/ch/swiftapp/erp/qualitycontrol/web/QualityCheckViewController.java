package ch.swiftapp.erp.qualitycontrol.web;

import ch.swiftapp.erp.qualitycontrol.dto.NcrRequest;
import ch.swiftapp.erp.qualitycontrol.dto.QualityCheckRequest;
import ch.swiftapp.erp.qualitycontrol.model.CheckResult;
import ch.swiftapp.erp.qualitycontrol.model.NcrSeverity;
import ch.swiftapp.erp.qualitycontrol.service.InspectionPlanService;
import ch.swiftapp.erp.qualitycontrol.service.NcrService;
import ch.swiftapp.erp.qualitycontrol.service.QualityCheckService;
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
 * Thymeleaf view controller for quality checks and NCRs at {@code /app/quality-control}.
 */
@Controller
@RequestMapping("/app/quality-control")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('QC:VIEW')")
public class QualityCheckViewController {

    private final QualityCheckService checkService;
    private final NcrService ncrService;
    private final InspectionPlanService inspectionPlanService;

    // ── Quality Checks ──────────────────────────────────────

    @GetMapping("/checks")
    public String listChecks(@PageableDefault(size = 20) Pageable p, Model model) {
        model.addAttribute("checks", checkService.findAll(p));
        return "app/quality-control/checks/list";
    }

    @GetMapping("/checks/{id}")
    public String checkDetail(@PathVariable UUID id, Model model) {
        model.addAttribute("check", checkService.findById(id));
        return "app/quality-control/checks/detail";
    }

    @PreAuthorize("hasAuthority('QC:CREATE')")
    @GetMapping("/checks/new")
    public String createCheckForm(Model model) {
        model.addAttribute("checkRequest", new QualityCheckRequest(null, null, null, null, null, null));
        model.addAttribute("plans", inspectionPlanService.findAllActive());
        model.addAttribute("results", CheckResult.values());
        return "app/quality-control/checks/form";
    }

    @PreAuthorize("hasAuthority('QC:CREATE')")
    @PostMapping("/checks")
    public String createCheck(@Valid @ModelAttribute("checkRequest") QualityCheckRequest request,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("plans", inspectionPlanService.findAllActive());
            model.addAttribute("results", CheckResult.values());
            return "app/quality-control/checks/form";
        }
        var qc = checkService.create(request);
        ra.addFlashAttribute("successMessage", "Quality check recorded");
        return "redirect:/app/quality-control/checks/" + qc.id();
    }

    // ── Non-Conformance Reports ─────────────────────────────

    @GetMapping("/ncrs")
    public String listNcrs(@PageableDefault(size = 20) Pageable p, Model model) {
        model.addAttribute("ncrs", ncrService.findAll(p));
        return "app/quality-control/ncrs/list";
    }

    @GetMapping("/ncrs/{id}")
    public String ncrDetail(@PathVariable UUID id, Model model) {
        model.addAttribute("ncr", ncrService.findById(id));
        return "app/quality-control/ncrs/detail";
    }

    @PreAuthorize("hasAuthority('QC:CREATE')")
    @GetMapping("/ncrs/new")
    public String createNcrForm(@RequestParam(required = false) UUID qualityCheckId, Model model) {
        model.addAttribute("ncrRequest", new NcrRequest(qualityCheckId, null, null, null));
        model.addAttribute("severities", NcrSeverity.values());
        return "app/quality-control/ncrs/form";
    }

    @PreAuthorize("hasAuthority('QC:CREATE')")
    @PostMapping("/ncrs")
    public String createNcr(@Valid @ModelAttribute("ncrRequest") NcrRequest request,
                            BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("severities", NcrSeverity.values());
            return "app/quality-control/ncrs/form";
        }
        var ncr = ncrService.create(request);
        ra.addFlashAttribute("successMessage", "NCR created");
        return "redirect:/app/quality-control/ncrs/" + ncr.id();
    }

    @PreAuthorize("hasAuthority('QC:CREATE')")
    @PostMapping("/ncrs/{id}/close")
    public String closeNcr(@PathVariable UUID id, RedirectAttributes ra) {
        ncrService.close(id);
        ra.addFlashAttribute("successMessage", "NCR closed");
        return "redirect:/app/quality-control/ncrs/" + id;
    }
}
