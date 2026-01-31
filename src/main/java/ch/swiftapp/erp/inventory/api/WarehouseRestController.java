package ch.swiftapp.erp.inventory.api;

import ch.swiftapp.erp.inventory.dto.WarehouseRequest;
import ch.swiftapp.erp.inventory.dto.WarehouseResponse;
import ch.swiftapp.erp.inventory.service.WarehouseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for warehouse management — JSON API at {@code /api/v1/warehouses}.
 */
@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
@Tag(name = "Warehouses", description = "Warehouse and storage location management")
@PreAuthorize("hasAuthority('INVENTORY:VIEW')")
public class WarehouseRestController {

    private final WarehouseService warehouseService;

    @GetMapping
    public Page<WarehouseResponse> list(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return warehouseService.search(search, pageable);
        }
        return warehouseService.findAll(pageable);
    }

    @GetMapping("/active")
    public List<WarehouseResponse> active() {
        return warehouseService.findAllActive();
    }

    @GetMapping("/{id}")
    public WarehouseResponse getById(@PathVariable UUID id) {
        return warehouseService.findById(id);
    }

    @PreAuthorize("hasAuthority('INVENTORY:CREATE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseResponse create(@Valid @RequestBody WarehouseRequest request) {
        return warehouseService.create(request);
    }

    @PreAuthorize("hasAuthority('INVENTORY:EDIT')")
    @PutMapping("/{id}")
    public WarehouseResponse update(@PathVariable UUID id,
                                    @Valid @RequestBody WarehouseRequest request) {
        return warehouseService.update(id, request);
    }

    @PreAuthorize("hasAuthority('INVENTORY:DELETE')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        warehouseService.delete(id);
    }
}

