package ch.swiftapp.erp.production.api;

import ch.swiftapp.erp.production.dto.ProductionOrderRequest;
import ch.swiftapp.erp.production.dto.ProductionOrderResponse;
import ch.swiftapp.erp.production.model.ProductionOrderStatus;
import ch.swiftapp.erp.production.service.ProductionOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/production-orders") @RequiredArgsConstructor
@Tag(name = "Production Orders", description = "Manufacturing production order management")
@PreAuthorize("hasAuthority('PRODUCTION:VIEW')")
public class ProductionOrderRestController {
    private final ProductionOrderService service;

    @GetMapping
    public Page<ProductionOrderResponse> list(@RequestParam(required = false) String search,
            @RequestParam(required = false) ProductionOrderStatus status, Pageable p) {
        if (search != null && !search.isBlank()) return service.search(search, p);
        if (status != null) return service.findByStatus(status, p);
        return service.findAll(p);
    }
    @GetMapping("/{id}") public ProductionOrderResponse getById(@PathVariable UUID id) { return service.findById(id); }
    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ProductionOrderResponse create(@Valid @RequestBody ProductionOrderRequest r) { return service.create(r); }
    @PreAuthorize("hasAuthority('PRODUCTION:EDIT')")
    @PutMapping("/{id}") public ProductionOrderResponse update(@PathVariable UUID id, @Valid @RequestBody ProductionOrderRequest r) { return service.update(id, r); }
    @PreAuthorize("hasAuthority('PRODUCTION:DELETE')")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}/release") public ProductionOrderResponse release(@PathVariable UUID id) { return service.release(id); }
    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}/start") public ProductionOrderResponse start(@PathVariable UUID id) { return service.start(id); }
    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}/complete") public ProductionOrderResponse complete(@PathVariable UUID id,
            @RequestParam(required = false) BigDecimal completedQty, @RequestParam(required = false) BigDecimal scrapQty) { return service.complete(id, completedQty, scrapQty); }
    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}/hold") public ProductionOrderResponse hold(@PathVariable UUID id) { return service.hold(id); }
    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}/resume") public ProductionOrderResponse resume(@PathVariable UUID id) { return service.resume(id); }
    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping("/{id}/cancel") public ProductionOrderResponse cancel(@PathVariable UUID id, @RequestParam(defaultValue = "") String reason) { return service.cancel(id, reason); }
}

