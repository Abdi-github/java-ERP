package ch.swiftapp.erp.accounting.api;

import ch.swiftapp.erp.accounting.dto.AccountRequest;
import ch.swiftapp.erp.accounting.dto.AccountResponse;
import ch.swiftapp.erp.accounting.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST controller for chart of accounts — JSON API at {@code /api/v1/accounts}.
 */
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Chart of accounts (Swiss accounting plan)")
public class AccountRestController {

    private final AccountService accountService;

    @GetMapping
    public Page<AccountResponse> list(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return accountService.search(search, pageable);
        }
        return accountService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public AccountResponse getById(@PathVariable UUID id) {
        return accountService.findById(id);
    }

    @GetMapping("/{id}/balance")
    public BigDecimal getBalance(@PathVariable UUID id) {
        return accountService.getBalance(id).orElse(BigDecimal.ZERO);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody AccountRequest request) {
        return accountService.create(request);
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable UUID id,
                                  @Valid @RequestBody AccountRequest request) {
        return accountService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        accountService.delete(id);
    }
}

