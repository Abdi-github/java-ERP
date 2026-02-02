package ch.swiftapp.erp.accounting.web;

import ch.swiftapp.erp.accounting.dto.AccountRequest;
import ch.swiftapp.erp.accounting.model.AccountType;
import ch.swiftapp.erp.accounting.service.AccountService;
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
 * Thymeleaf view controller for chart of accounts at {@code /app/accounting/accounts}.
 */
@Controller
@RequestMapping("/app/accounting/accounts")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ACCOUNTING:VIEW')")
public class AccountViewController {

    private final AccountService accountService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @PageableDefault(size = 20) Pageable pageable,
                       Model model) {
        var accounts = (search != null && !search.isBlank())
                ? accountService.search(search, pageable)
                : accountService.findAll(pageable);
        model.addAttribute("accounts", accounts);
        model.addAttribute("search", search);
        return "app/accounting/accounts/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("account", accountService.findById(id));
        model.addAttribute("balance", accountService.getBalance(id).orElse(java.math.BigDecimal.ZERO));
        return "app/accounting/accounts/detail";
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:CREATE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("accountRequest", new AccountRequest(null, null, null, null, null, null));
        model.addAttribute("accountTypes", AccountType.values());
        model.addAttribute("allAccounts", accountService.findAllActive());
        model.addAttribute("editMode", false);
        return "app/accounting/accounts/form";
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:CREATE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("accountRequest") AccountRequest request,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("accountTypes", AccountType.values());
            model.addAttribute("allAccounts", accountService.findAllActive());
            model.addAttribute("editMode", false);
            return "app/accounting/accounts/form";
        }
        try {
            var account = accountService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Account created successfully");
            return "redirect:/app/accounting/accounts/" + account.id();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("accountTypes", AccountType.values());
            model.addAttribute("allAccounts", accountService.findAllActive());
            model.addAttribute("editMode", false);
            return "app/accounting/accounts/form";
        }
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:EDIT')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        var account = accountService.findById(id);
        var request = new AccountRequest(
                account.accountNumber(), account.name(), account.description(),
                account.accountType(), account.parentId(), account.active());
        model.addAttribute("accountRequest", request);
        model.addAttribute("accountId", id);
        model.addAttribute("accountTypes", AccountType.values());
        model.addAttribute("allAccounts", accountService.findAllActive());
        model.addAttribute("editMode", true);
        return "app/accounting/accounts/form";
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:CREATE')")
    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("accountRequest") AccountRequest request,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("accountId", id);
            model.addAttribute("accountTypes", AccountType.values());
            model.addAttribute("allAccounts", accountService.findAllActive());
            model.addAttribute("editMode", true);
            return "app/accounting/accounts/form";
        }
        try {
            accountService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Account updated successfully");
            return "redirect:/app/accounting/accounts/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("accountId", id);
            model.addAttribute("accountTypes", AccountType.values());
            model.addAttribute("allAccounts", accountService.findAllActive());
            model.addAttribute("editMode", true);
            return "app/accounting/accounts/form";
        }
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:DELETE')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        accountService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Account deleted successfully");
        return "redirect:/app/accounting/accounts";
    }
}

