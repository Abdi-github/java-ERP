package ch.swiftapp.erp.sales.api;

import ch.swiftapp.erp.sales.dto.SalesOrderRequest;
import ch.swiftapp.erp.sales.dto.SalesOrderResponse;
import ch.swiftapp.erp.sales.model.SalesOrderStatus;
import ch.swiftapp.erp.sales.service.SalesOrderService;
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
 * REST controller for sales order management — JSON API at {@code /api/v1/sales-orders}.
 */
@RestController
@RequestMapping("/api/v1/sales-orders")
@RequiredArgsConstructor
@Tag(name = "Sales Orders", description = "Sales order lifecycle management")
@PreAuthorize("hasAuthority('SALES:VIEW')")
public class SalesOrderRestController {

    private final SalesOrderService salesOrderService;

    @GetMapping
    @PreAuthorize("hasAuthority('SALES:VIEW')")
    public Page<SalesOrderResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) SalesOrderStatus status,
            @RequestParam(required = false) UUID customerId,
            Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return salesOrderService.search(search, pageable);
        }
        if (status != null) {
            return salesOrderService.findByStatus(status, pageable);
        }
        if (customerId != null) {
            return salesOrderService.findByCustomer(customerId, pageable);
        }
        return salesOrderService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SALES:VIEW')")
    public SalesOrderResponse getById(@PathVariable UUID id) {
        return salesOrderService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SALES:CREATE')")
    public SalesOrderResponse create(@Valid @RequestBody SalesOrderRequest request) {
        return salesOrderService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SALES:EDIT')")
    public SalesOrderResponse update(@PathVariable UUID id,
                                     @Valid @RequestBody SalesOrderRequest request) {
        return salesOrderService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SALES:DELETE')")
    public void delete(@PathVariable UUID id) {
        salesOrderService.delete(id);
    }

    // ── Status transitions ────────────────────────────────────

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('SALES:EDIT')")
    public SalesOrderResponse confirm(@PathVariable UUID id) {
        return salesOrderService.confirm(id);
    }

    @PostMapping("/{id}/advance")
    @PreAuthorize("hasAuthority('SALES:EDIT')")
    public SalesOrderResponse advance(@PathVariable UUID id) {
        return salesOrderService.advanceStatus(id);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('SALES:EDIT')")
    public SalesOrderResponse cancel(@PathVariable UUID id,
                                     @RequestParam(required = false, defaultValue = "") String reason) {
        return salesOrderService.cancel(id, reason);
    }
}
