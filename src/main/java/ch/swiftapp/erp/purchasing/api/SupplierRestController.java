package ch.swiftapp.erp.purchasing.api;

import ch.swiftapp.erp.purchasing.dto.SupplierRequest;
import ch.swiftapp.erp.purchasing.dto.SupplierResponse;
import ch.swiftapp.erp.purchasing.service.SupplierService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "Supplier management for purchasing")
@PreAuthorize("hasAuthority('PURCHASING:VIEW')")
public class SupplierRestController {

    private final SupplierService supplierService;

    @GetMapping
    public Page<SupplierResponse> list(@RequestParam(required = false) String search, Pageable pageable) {
        return (search != null && !search.isBlank()) ? supplierService.search(search, pageable) : supplierService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public SupplierResponse getById(@PathVariable UUID id) { return supplierService.findById(id); }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public SupplierResponse create(@Valid @RequestBody SupplierRequest request) { return supplierService.create(request); }

    @PreAuthorize("hasAuthority('PURCHASING:EDIT')")
    @PutMapping("/{id}")
    public SupplierResponse update(@PathVariable UUID id, @Valid @RequestBody SupplierRequest request) { return supplierService.update(id, request); }

    @PreAuthorize("hasAuthority('PURCHASING:DELETE')")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { supplierService.delete(id); }
}

