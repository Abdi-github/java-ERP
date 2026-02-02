package ch.swiftapp.erp.accounting.web;

import ch.swiftapp.erp.accounting.dto.JournalEntryRequest;
import ch.swiftapp.erp.accounting.service.AccountService;
import ch.swiftapp.erp.accounting.service.JournalEntryService;
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
 * Thymeleaf view controller for journal entries at {@code /app/accounting/journal-entries}.
 */
@Controller
@RequestMapping("/app/accounting/journal-entries")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ACCOUNTING:VIEW')")
public class JournalEntryViewController {

    private final JournalEntryService journalEntryService;
    private final AccountService accountService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @PageableDefault(size = 20) Pageable pageable,
                       Model model) {
        var entries = (search != null && !search.isBlank())
                ? journalEntryService.search(search, pageable)
                : journalEntryService.findAll(pageable);
        model.addAttribute("entries", entries);
        model.addAttribute("search", search);
        return "app/accounting/journal-entries/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("entry", journalEntryService.findById(id));
        return "app/accounting/journal-entries/detail";
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("accounts", accountService.findAllActive());
        return "app/accounting/journal-entries/form";
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute JournalEntryRequest request,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("accounts", accountService.findAllActive());
            model.addAttribute("errorMessage", "Validation failed — please check your input");
            return "app/accounting/journal-entries/form";
        }
        try {
            var entry = journalEntryService.create(request);
            ra.addFlashAttribute("successMessage", "Journal entry created: " + entry.entryNumber());
            return "redirect:/app/accounting/journal-entries/" + entry.id();
        } catch (Exception e) {
            model.addAttribute("accounts", accountService.findAllActive());
            model.addAttribute("errorMessage", e.getMessage());
            return "app/accounting/journal-entries/form";
        }
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:CREATE')")
    @PostMapping("/{id}/post")
    public String post(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            journalEntryService.post(id);
            redirectAttributes.addFlashAttribute("successMessage", "Journal entry posted successfully");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/app/accounting/journal-entries/" + id;
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:CREATE')")
    @PostMapping("/{id}/reverse")
    public String reverse(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            var reversal = journalEntryService.reverse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Journal entry reversed. Reversal: " + reversal.entryNumber());
            return "redirect:/app/accounting/journal-entries/" + reversal.id();
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/app/accounting/journal-entries/" + id;
        }
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            journalEntryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Journal entry deleted");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/app/accounting/journal-entries";
    }
}

