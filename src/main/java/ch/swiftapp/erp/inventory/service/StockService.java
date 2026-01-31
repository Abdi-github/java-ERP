package ch.swiftapp.erp.inventory.service;

import ch.swiftapp.erp.inventory.dto.StockLevelResponse;
import ch.swiftapp.erp.inventory.dto.StockMovementRequest;
import ch.swiftapp.erp.inventory.dto.StockMovementResponse;
import ch.swiftapp.erp.inventory.event.StockMovementRecordedEvent;
import ch.swiftapp.erp.inventory.model.*;
import ch.swiftapp.erp.inventory.repository.StockLevelRepository;
import ch.swiftapp.erp.inventory.repository.StockMovementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing stock levels and recording stock movements.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StockService {

    private final StockLevelRepository stockLevelRepository;
    private final StockMovementRepository stockMovementRepository;
    private final WarehouseService warehouseService;
    private final ApplicationEventPublisher eventPublisher;

    private static final ZoneId ZURICH_ZONE = ZoneId.of("Europe/Zurich");

    // ── Stock Level Queries ───────────────────────────────────

    /**
     * Get stock level for an item in a specific warehouse.
     */
    public BigDecimal getStockLevel(UUID itemId, StockItemType itemType, UUID warehouseId) {
        return stockLevelRepository.findByItemIdAndItemTypeAndWarehouseId(itemId, itemType, warehouseId)
                .map(StockLevel::getQuantityAvailable)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Get total stock across all warehouses for an item.
     */
    public BigDecimal getTotalStockLevel(UUID itemId, StockItemType itemType) {
        return stockLevelRepository.findAllByItemIdAndItemType(itemId, itemType).stream()
                .map(StockLevel::getQuantityAvailable)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Check if sufficient stock is available.
     */
    public boolean isStockAvailable(UUID itemId, StockItemType itemType, UUID warehouseId, BigDecimal quantity) {
        return getStockLevel(itemId, itemType, warehouseId).compareTo(quantity) >= 0;
    }

    /**
     * Get all stock levels for an item across all warehouses.
     */
    public List<StockLevelResponse> getStockLevelsForItem(UUID itemId, StockItemType itemType) {
        return stockLevelRepository.findAllByItemWithWarehouse(itemId, itemType).stream()
                .map(this::toStockLevelResponse)
                .toList();
    }

    /**
     * Get all stock levels with pagination.
     */
    public Page<StockLevelResponse> findAllStockLevels(Pageable pageable) {
        return stockLevelRepository.findAllWithWarehouse(pageable)
                .map(this::toStockLevelResponse);
    }

    /**
     * Get all stock levels for a warehouse.
     */
    public Page<StockLevelResponse> getStockLevelsForWarehouse(UUID warehouseId, Pageable pageable) {
        return stockLevelRepository.findAllByWarehouseId(warehouseId, pageable)
                .map(this::toStockLevelResponse);
    }

    // ── Stock Movements ───────────────────────────────────────

    /**
     * List all movements with pagination.
     */
    public Page<StockMovementResponse> findAllMovements(Pageable pageable) {
        return stockMovementRepository.findAllByOrderByMovementDateDesc(pageable)
                .map(this::toMovementResponse);
    }

    /**
     * List movements for a specific item.
     */
    public Page<StockMovementResponse> findMovementsForItem(UUID itemId, StockItemType itemType, Pageable pageable) {
        return stockMovementRepository.findAllByItemIdAndItemTypeOrderByMovementDateDesc(itemId, itemType, pageable)
                .map(this::toMovementResponse);
    }

    /**
     * Record a stock movement and update stock levels accordingly.
     */
    @Transactional
    public StockMovementResponse recordMovement(StockMovementRequest request) {
        log.info("Recording stock movement: type={} item={} qty={}",
                request.movementType(), request.itemId(), request.quantity());

        validateMovementRequest(request);

        var movement = new StockMovement();
        movement.setReferenceNumber(generateReferenceNumber(request.movementType()));
        movement.setMovementType(request.movementType());
        movement.setItemId(request.itemId());
        movement.setItemType(request.itemType());
        movement.setQuantity(request.quantity());
        movement.setMovementDate(Instant.now());
        movement.setReason(request.reason());

        if (request.sourceWarehouseId() != null) {
            movement.setSourceWarehouse(warehouseService.findEntityById(request.sourceWarehouseId()));
        }
        if (request.targetWarehouseId() != null) {
            movement.setTargetWarehouse(warehouseService.findEntityById(request.targetWarehouseId()));
        }

        // Update stock levels based on movement type
        switch (request.movementType()) {
            case GOODS_RECEIPT -> {
                addStock(request.itemId(), request.itemType(),
                        request.targetWarehouseId(), request.quantity());
            }
            case GOODS_ISSUE -> {
                removeStock(request.itemId(), request.itemType(),
                        request.sourceWarehouseId(), request.quantity());
            }
            case PRODUCTION_ISSUE -> {
                // Materials leave the warehouse and go to production floor
                removeStock(request.itemId(), request.itemType(),
                        request.sourceWarehouseId(), request.quantity());
            }
            case PRODUCTION_RECEIPT -> {
                // Finished goods arrive from production into finished-goods warehouse
                addStock(request.itemId(), request.itemType(),
                        request.targetWarehouseId(), request.quantity());
            }
            case SHIPMENT -> {
                // Goods leave the warehouse to go to the customer
                removeStock(request.itemId(), request.itemType(),
                        request.sourceWarehouseId(), request.quantity());
            }
            case TRANSFER -> {
                removeStock(request.itemId(), request.itemType(),
                        request.sourceWarehouseId(), request.quantity());
                addStock(request.itemId(), request.itemType(),
                        request.targetWarehouseId(), request.quantity());
            }
            case ADJUSTMENT -> {
                if (request.targetWarehouseId() != null) {
                    adjustStock(request.itemId(), request.itemType(),
                            request.targetWarehouseId(), request.quantity());
                } else if (request.sourceWarehouseId() != null) {
                    adjustStock(request.itemId(), request.itemType(),
                            request.sourceWarehouseId(), request.quantity().negate());
                }
            }
            case RETURN -> {
                if (request.targetWarehouseId() != null) {
                    addStock(request.itemId(), request.itemType(),
                            request.targetWarehouseId(), request.quantity());
                }
            }
            case SCRAP -> {
                // Scrapped stock is removed from the source warehouse (written off)
                removeStock(request.itemId(), request.itemType(),
                        request.sourceWarehouseId(), request.quantity());
            }
        }

        movement = stockMovementRepository.save(movement);

        eventPublisher.publishEvent(new StockMovementRecordedEvent(
                movement.getId(), movement.getReferenceNumber(),
                movement.getMovementType(), movement.getItemId(),
                movement.getItemType(),
                request.sourceWarehouseId(), request.targetWarehouseId(),
                movement.getQuantity()));

        log.info("Recorded stock movement ref={} type={}", movement.getReferenceNumber(), movement.getMovementType());
        return toMovementResponse(movement);
    }

    /**
     * Count stock records where quantity on hand is at or below the given threshold.
     *
     * @param threshold low-stock threshold (inclusive)
     * @return number of low-stock stock-level records
     */
    public long countLowStockItems(BigDecimal threshold) {
        return stockLevelRepository.countLowStock(threshold);
    }

    // ── Internal helpers ──────────────────────────────────────

    private void validateMovementRequest(StockMovementRequest request) {
        switch (request.movementType()) {
            case GOODS_RECEIPT, PRODUCTION_RECEIPT -> {
                if (request.targetWarehouseId() == null) {
                    throw new IllegalArgumentException("Target warehouse is required for goods/production receipt");
                }
            }
            case GOODS_ISSUE, PRODUCTION_ISSUE, SHIPMENT, SCRAP -> {
                if (request.sourceWarehouseId() == null) {
                    throw new IllegalArgumentException("Source warehouse is required for " + request.movementType());
                }
            }
            case TRANSFER -> {
                if (request.sourceWarehouseId() == null || request.targetWarehouseId() == null) {
                    throw new IllegalArgumentException("Both source and target warehouse are required for transfers");
                }
                if (request.sourceWarehouseId().equals(request.targetWarehouseId())) {
                    throw new IllegalArgumentException("Source and target warehouse must be different");
                }
            }
            case ADJUSTMENT, RETURN -> { /* warehouse validated contextually */ }
        }
    }

    private void addStock(UUID itemId, StockItemType itemType, UUID warehouseId, BigDecimal quantity) {
        var stockLevel = getOrCreateStockLevel(itemId, itemType, warehouseId);
        stockLevel.setQuantityOnHand(stockLevel.getQuantityOnHand().add(quantity));
        stockLevelRepository.save(stockLevel);
    }

    private void removeStock(UUID itemId, StockItemType itemType, UUID warehouseId, BigDecimal quantity) {
        var stockLevel = stockLevelRepository
                .findByItemIdAndItemTypeAndWarehouseId(itemId, itemType, warehouseId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No stock record found for item=" + itemId + " warehouse=" + warehouseId));

        if (stockLevel.getQuantityAvailable().compareTo(quantity) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient stock: available=" + stockLevel.getQuantityAvailable() + " requested=" + quantity);
        }

        stockLevel.setQuantityOnHand(stockLevel.getQuantityOnHand().subtract(quantity));
        stockLevelRepository.save(stockLevel);
    }

    private void adjustStock(UUID itemId, StockItemType itemType, UUID warehouseId, BigDecimal adjustmentQty) {
        var stockLevel = getOrCreateStockLevel(itemId, itemType, warehouseId);
        stockLevel.setQuantityOnHand(stockLevel.getQuantityOnHand().add(adjustmentQty));
        if (stockLevel.getQuantityOnHand().compareTo(BigDecimal.ZERO) < 0) {
            stockLevel.setQuantityOnHand(BigDecimal.ZERO);
        }
        stockLevelRepository.save(stockLevel);
    }

    private StockLevel getOrCreateStockLevel(UUID itemId, StockItemType itemType, UUID warehouseId) {
        return stockLevelRepository.findByItemIdAndItemTypeAndWarehouseId(itemId, itemType, warehouseId)
                .orElseGet(() -> {
                    var warehouse = warehouseService.findEntityById(warehouseId);
                    var sl = new StockLevel();
                    sl.setItemId(itemId);
                    sl.setItemType(itemType);
                    sl.setWarehouse(warehouse);
                    sl.setQuantityOnHand(BigDecimal.ZERO);
                    sl.setQuantityReserved(BigDecimal.ZERO);
                    return sl;
                });
    }

    private String generateReferenceNumber(MovementType type) {
        var prefix = switch (type) {
            case GOODS_RECEIPT      -> "GR";
            case GOODS_ISSUE        -> "GI";
            case PRODUCTION_ISSUE   -> "PI";
            case PRODUCTION_RECEIPT -> "PR";
            case SHIPMENT           -> "SH";
            case TRANSFER           -> "TR";
            case ADJUSTMENT         -> "AD";
            case RETURN             -> "RT";
            case SCRAP              -> "SC";
        };
        var datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        var seq = stockMovementRepository.findMaxReferenceNumberByPrefix(prefix + "-" + datePart + "-%") + 1;
        return "%s-%s-%04d".formatted(prefix, datePart, seq);
    }

    private StockLevelResponse toStockLevelResponse(StockLevel entity) {
        return new StockLevelResponse(
                entity.getId(),
                entity.getItemId(),
                entity.getItemType(),
                entity.getWarehouse().getId(),
                entity.getWarehouse().getCode(),
                entity.getWarehouse().getName(),
                entity.getQuantityOnHand(),
                entity.getQuantityReserved(),
                entity.getQuantityAvailable()
        );
    }

    private StockMovementResponse toMovementResponse(StockMovement entity) {
        return new StockMovementResponse(
                entity.getId(),
                entity.getReferenceNumber(),
                entity.getMovementType(),
                entity.getItemId(),
                entity.getItemType(),
                entity.getSourceWarehouse() != null ? entity.getSourceWarehouse().getId() : null,
                entity.getSourceWarehouse() != null ? entity.getSourceWarehouse().getCode() : null,
                entity.getTargetWarehouse() != null ? entity.getTargetWarehouse().getId() : null,
                entity.getTargetWarehouse() != null ? entity.getTargetWarehouse().getCode() : null,
                entity.getQuantity(),
                entity.getMovementDate() != null ? entity.getMovementDate().atZone(ZURICH_ZONE) : null,
                entity.getReason(),
                entity.getSourceDocumentType(),
                entity.getSourceDocumentId(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().atZone(ZURICH_ZONE) : null
        );
    }
}

