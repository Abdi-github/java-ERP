package ch.swiftapp.erp.sales.api;

import ch.swiftapp.erp.sales.dto.CustomerRequest;
import ch.swiftapp.erp.sales.dto.CustomerResponse;
import ch.swiftapp.erp.sales.service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for customer management — JSON API at {@code /api/v1/customers}.
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Sales customer management")
@PreAuthorize("hasAuthority('SALES:VIEW')")
public class CustomerRestController {

    private final CustomerService customerService;

    @GetMapping
    public Page<CustomerResponse> list(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return customerService.search(search, pageable);
        }
        return customerService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable UUID id) {
        return customerService.findById(id);
    }

    @PreAuthorize("hasAuthority('SALES:CREATE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CustomerRequest request) {
        return customerService.create(request);
    }

    @PreAuthorize("hasAuthority('SALES:EDIT')")
    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable UUID id,
                                   @Valid @RequestBody CustomerRequest request) {
        return customerService.update(id, request);
    }

    @PreAuthorize("hasAuthority('SALES:DELETE')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        customerService.delete(id);
    }
}

