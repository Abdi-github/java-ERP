package ch.swiftapp.erp.purchasing.api;

import ch.swiftapp.erp.purchasing.dto.PurchaseOrderRequest;
import ch.swiftapp.erp.purchasing.dto.PurchaseOrderResponse;
import ch.swiftapp.erp.purchasing.model.PurchaseOrderStatus;
import ch.swiftapp.erp.purchasing.service.PurchaseOrderService;
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
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Purchase Orders", description = "Purchase order lifecycle management")
@PreAuthorize("hasAuthority('PURCHASING:VIEW')")
public class PurchaseOrderRestController {

    private final PurchaseOrderService service;

    @GetMapping
    public Page<PurchaseOrderResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) PurchaseOrderStatus status,
            @RequestParam(required = false) UUID supplierId,
            Pageable pageable) {
        if (search != null && !search.isBlank()) return service.search(search, pageable);
        if (status != null) return service.findByStatus(status, pageable);
        if (supplierId != null) return service.findBySupplier(supplierId, pageable);
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public PurchaseOrderResponse getById(@PathVariable UUID id) { return service.findById(id); }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrderResponse create(@Valid @RequestBody PurchaseOrderRequest request) { return service.create(request); }

    @PreAuthorize("hasAuthority('PURCHASING:EDIT')")
    @PutMapping("/{id}")
    public PurchaseOrderResponse update(@PathVariable UUID id, @Valid @RequestBody PurchaseOrderRequest request) { return service.update(id, request); }

    @PreAuthorize("hasAuthority('PURCHASING:DELETE')")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }

    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping("/{id}/submit") public PurchaseOrderResponse submit(@PathVariable UUID id) { return service.submit(id); }
    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping("/{id}/confirm") public PurchaseOrderResponse confirm(@PathVariable UUID id) { return service.confirm(id); }
    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping("/{id}/receive") public PurchaseOrderResponse receive(@PathVariable UUID id) { return service.receive(id); }
    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping("/{id}/complete") public PurchaseOrderResponse complete(@PathVariable UUID id) { return service.complete(id); }
    @PreAuthorize("hasAuthority('PURCHASING:CREATE')")
    @PostMapping("/{id}/cancel") public PurchaseOrderResponse cancel(@PathVariable UUID id, @RequestParam(defaultValue = "") String reason) { return service.cancel(id, reason); }
}

