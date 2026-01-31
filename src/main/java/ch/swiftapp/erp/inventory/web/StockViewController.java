package ch.swiftapp.erp.inventory.web;

import ch.swiftapp.erp.inventory.dto.StockMovementRequest;
import ch.swiftapp.erp.inventory.model.MovementType;
import ch.swiftapp.erp.inventory.model.StockItemType;
import ch.swiftapp.erp.inventory.service.StockService;
import ch.swiftapp.erp.inventory.service.WarehouseService;
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
 * Thymeleaf view controller for stock management at {@code /app/inventory/stock}.
 */
@Controller
@RequestMapping("/app/inventory/stock")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('INVENTORY:VIEW')")
public class StockViewController {

    private final StockService stockService;
    private final WarehouseService warehouseService;

    @GetMapping
    public String list(@PageableDefault(size = 20) Pageable pageable, Model model) {
        model.addAttribute("stockLevels", stockService.findAllStockLevels(pageable));
        model.addAttribute("warehouses", warehouseService.findAllActive());
        return "app/inventory/stock/list";
    }

    @GetMapping("/movements")
    public String listMovements(@PageableDefault(size = 20) Pageable pageable, Model model) {
        var movements = stockService.findAllMovements(pageable);

        log.debug("Rendering stock movements: page={}, totalElements={}, elementsOnPage={}",
                movements.getNumber(), movements.getTotalElements(), movements.getNumberOfElements());

        model.addAttribute("movements", movements);
        model.addAttribute("movementTotal", movements.getTotalElements());
        return "app/inventory/stock/movements";
    }

    @PreAuthorize("hasAuthority('INVENTORY:CREATE')")
    @GetMapping("/movements/new")
    public String createMovementForm(Model model) {
        model.addAttribute("movementRequest", new StockMovementRequest(
                null, null, null, null, null, null, null));
        model.addAttribute("warehouses", warehouseService.findAllActive());
        model.addAttribute("movementTypes", MovementType.values());
        model.addAttribute("itemTypes", StockItemType.values());
        return "app/inventory/stock/movement-form";
    }

    @PreAuthorize("hasAuthority('INVENTORY:CREATE')")
    @PostMapping("/movements")
    public String createMovement(@Valid @ModelAttribute("movementRequest") StockMovementRequest request,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("warehouses", warehouseService.findAllActive());
            model.addAttribute("movementTypes", MovementType.values());
            model.addAttribute("itemTypes", StockItemType.values());
            return "app/inventory/stock/movement-form";
        }
        try {
            stockService.recordMovement(request);
            redirectAttributes.addFlashAttribute("successMessage", "Stock movement recorded successfully");
            return "redirect:/app/inventory/stock/movements";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("warehouses", warehouseService.findAllActive());
            model.addAttribute("movementTypes", MovementType.values());
            model.addAttribute("itemTypes", StockItemType.values());
            return "app/inventory/stock/movement-form";
        }
    }

    @GetMapping("/levels/warehouse/{warehouseId}")
    public String warehouseStock(@PathVariable UUID warehouseId,
                                 @PageableDefault(size = 20) Pageable pageable,
                                 Model model) {
        model.addAttribute("stockLevels", stockService.getStockLevelsForWarehouse(warehouseId, pageable));
        model.addAttribute("warehouse", warehouseService.findById(warehouseId));
        return "app/inventory/stock/warehouse-levels";
    }
}

