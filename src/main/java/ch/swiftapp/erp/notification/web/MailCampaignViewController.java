package ch.swiftapp.erp.notification.web;

import ch.swiftapp.erp.notification.dto.MailCampaignRequest;
import ch.swiftapp.erp.notification.dto.MailCampaignResponse;
import ch.swiftapp.erp.notification.model.MailCampaignStatus;
import ch.swiftapp.erp.notification.service.MailCampaignService;
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
 * Thymeleaf view controller for mail campaign administration at {@code /app/admin/mail-campaigns}.
 */
@Controller
@RequestMapping("/app/admin/mail-campaigns")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('NOTIFICATIONS:MANAGE')")
public class MailCampaignViewController {

    private final MailCampaignService mailCampaignService;

    @PreAuthorize("hasAuthority('NOTIFICATIONS:VIEW')")
    @GetMapping
    public String list(@PageableDefault(size = 20) Pageable pageable, Model model) {
        model.addAttribute("campaigns", mailCampaignService.findAll(pageable));
        model.addAttribute("statuses", MailCampaignStatus.values());
        return "app/admin/mail-campaigns/list";
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:VIEW')")
    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("campaign", mailCampaignService.findById(id));
        return "app/admin/mail-campaigns/detail";
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("campaign", new MailCampaignRequest("", "", "", "de", "ALL_USERS", null, ""));
        return "app/admin/mail-campaigns/form";
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("campaign") MailCampaignRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            return "app/admin/mail-campaigns/form";
        }
        MailCampaignResponse created = mailCampaignService.create(request);
        redirectAttributes.addFlashAttribute("successMessage", "Kampagne '" + created.name() + "' erstellt.");
        return "redirect:/app/admin/mail-campaigns/" + created.id();
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:CREATE')")
    @PostMapping("/{id}/queue")
    public String queue(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        MailCampaignResponse campaign = mailCampaignService.queue(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Kampagne '" + campaign.name() + "' zur Versendung freigegeben.");
        return "redirect:/app/admin/mail-campaigns/" + id;
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS:CREATE')")
    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        MailCampaignResponse campaign = mailCampaignService.cancel(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Kampagne '" + campaign.name() + "' abgebrochen.");
        return "redirect:/app/admin/mail-campaigns/" + id;
    }
}

