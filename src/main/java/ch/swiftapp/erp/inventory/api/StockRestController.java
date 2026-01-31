package ch.swiftapp.erp.inventory.api;

import ch.swiftapp.erp.inventory.dto.StockLevelResponse;
import ch.swiftapp.erp.inventory.dto.StockMovementRequest;
import ch.swiftapp.erp.inventory.dto.StockMovementResponse;
import ch.swiftapp.erp.inventory.model.StockItemType;
import ch.swiftapp.erp.inventory.service.StockService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for stock management — JSON API at {@code /api/v1/stock}.
 */
@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Stock levels, movements, and inventory tracking")
@PreAuthorize("hasAuthority('INVENTORY:VIEW')")
public class StockRestController {

    private final StockService stockService;

    // ── Stock Levels ──────────────────────────────────────

    @GetMapping("/levels/{itemId}")
    public List<StockLevelResponse> getStockLevels(
            @PathVariable UUID itemId,
            @RequestParam(defaultValue = "PRODUCT") StockItemType itemType) {
        return stockService.getStockLevelsForItem(itemId, itemType);
    }

    @GetMapping("/levels/warehouse/{warehouseId}")
    public Page<StockLevelResponse> getWarehouseStock(
            @PathVariable UUID warehouseId,
            Pageable pageable) {
        return stockService.getStockLevelsForWarehouse(warehouseId, pageable);
    }

    @GetMapping("/levels/{itemId}/total")
    public BigDecimal getTotalStock(
            @PathVariable UUID itemId,
            @RequestParam(defaultValue = "PRODUCT") StockItemType itemType) {
        return stockService.getTotalStockLevel(itemId, itemType);
    }

    @GetMapping("/levels/{itemId}/available")
    public boolean checkAvailability(
            @PathVariable UUID itemId,
            @RequestParam UUID warehouseId,
            @RequestParam BigDecimal quantity,
            @RequestParam(defaultValue = "PRODUCT") StockItemType itemType) {
        return stockService.isStockAvailable(itemId, itemType, warehouseId, quantity);
    }

    // ── Stock Movements ───────────────────────────────────

    @GetMapping("/movements")
    public Page<StockMovementResponse> listMovements(Pageable pageable) {
        return stockService.findAllMovements(pageable);
    }

    @GetMapping("/movements/item/{itemId}")
    public Page<StockMovementResponse> listMovementsForItem(
            @PathVariable UUID itemId,
            @RequestParam(defaultValue = "PRODUCT") StockItemType itemType,
            Pageable pageable) {
        return stockService.findMovementsForItem(itemId, itemType, pageable);
    }

    @PreAuthorize("hasAuthority('INVENTORY:CREATE')")
    @PostMapping("/movements")
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovementResponse recordMovement(@Valid @RequestBody StockMovementRequest request) {
        return stockService.recordMovement(request);
    }
}

