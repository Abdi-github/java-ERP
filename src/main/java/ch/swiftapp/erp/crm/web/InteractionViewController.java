package ch.swiftapp.erp.crm.web;

import ch.swiftapp.erp.crm.dto.InteractionRequest;
import ch.swiftapp.erp.crm.model.InteractionType;
import ch.swiftapp.erp.crm.service.InteractionService;
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
 * Thymeleaf view controller for CRM interactions at {@code /app/crm/interactions}.
 */
@Controller
@RequestMapping("/app/crm/interactions")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('CRM:VIEW')")
public class InteractionViewController {

    private final InteractionService interactionService;

    @GetMapping
    public String list(@PageableDefault(size = 20) Pageable pageable, Model model) {
        model.addAttribute("interactions", interactionService.findAll(pageable));
        return "app/crm/interactions/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("interaction", interactionService.findById(id));
        return "app/crm/interactions/detail";
    }

    @PreAuthorize("hasAuthority('CRM:CREATE')")
    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) UUID contactId, Model model) {
        model.addAttribute("interactionRequest",
                new InteractionRequest(contactId, null, null, null, null, null));
        model.addAttribute("interactionTypes", InteractionType.values());
        return "app/crm/interactions/form";
    }

    @PreAuthorize("hasAuthority('CRM:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("interactionRequest") InteractionRequest request,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("interactionTypes", InteractionType.values());
            return "app/crm/interactions/form";
        }
        var interaction = interactionService.create(request);
        ra.addFlashAttribute("successMessage", "Interaction created");
        return "redirect:/app/crm/contacts/" + interaction.contactId();
    }
}



