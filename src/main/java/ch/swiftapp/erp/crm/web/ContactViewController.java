package ch.swiftapp.erp.crm.web;

import ch.swiftapp.erp.crm.dto.ContactRequest;
import ch.swiftapp.erp.crm.service.ContactService;
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

@Controller @RequestMapping("/app/crm/contacts") @RequiredArgsConstructor @Slf4j
@PreAuthorize("hasAuthority('CRM:VIEW')")
public class ContactViewController {
    private final ContactService contactService;
    private final InteractionService interactionService;

    @GetMapping
    public String list(@RequestParam(required = false) String search, @PageableDefault(size = 20) Pageable pageable, Model model) {
        model.addAttribute("contacts", (search != null && !search.isBlank()) ? contactService.search(search, pageable) : contactService.findAll(pageable));
        model.addAttribute("search", search);
        return "app/crm/contacts/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, @PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("contact", contactService.findById(id));
        model.addAttribute("interactions", interactionService.findByContactId(id, pageable));
        return "app/crm/contacts/detail";
    }

    @PreAuthorize("hasAuthority('CRM:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("contactRequest", new ContactRequest(null, null, null, null, null, null, null, null, null));
        model.addAttribute("editMode", false);
        return "app/crm/contacts/form";
    }

    @PreAuthorize("hasAuthority('CRM:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("contactRequest") ContactRequest request, BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) { model.addAttribute("editMode", false); return "app/crm/contacts/form"; }
        var contact = contactService.create(request);
        ra.addFlashAttribute("successMessage", "Contact created");
        return "redirect:/app/crm/contacts/" + contact.id();
    }

    @PreAuthorize("hasAuthority('CRM:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var c = contactService.findById(id);
        model.addAttribute("contactRequest", new ContactRequest(c.firstName(), c.lastName(), c.email(), c.phone(), c.company(), c.position(), c.customerId(), c.notes(), c.active()));
        model.addAttribute("contactId", id);
        model.addAttribute("editMode", true);
        return "app/crm/contacts/form";
    }

    @PreAuthorize("hasAuthority('CRM:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id, @Valid @ModelAttribute("contactRequest") ContactRequest request, BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) { model.addAttribute("contactId", id); model.addAttribute("editMode", true); return "app/crm/contacts/form"; }
        contactService.update(id, request);
        ra.addFlashAttribute("successMessage", "Contact updated");
        return "redirect:/app/crm/contacts/" + id;
    }

    @PreAuthorize("hasAuthority('CRM:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        contactService.delete(id);
        ra.addFlashAttribute("successMessage", "Contact deleted");
        return "redirect:/app/crm/contacts";
    }
}

