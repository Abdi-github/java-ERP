package ch.swiftapp.erp.inventory.repository;

import ch.swiftapp.erp.inventory.model.StockItemType;
import ch.swiftapp.erp.inventory.model.StockLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link StockLevel} entities.
 */
@Repository
public interface StockLevelRepository extends JpaRepository<StockLevel, UUID> {

    Optional<StockLevel> findByItemIdAndItemTypeAndWarehouseId(UUID itemId, StockItemType itemType, UUID warehouseId);

    List<StockLevel> findAllByItemIdAndItemType(UUID itemId, StockItemType itemType);

    Page<StockLevel> findAllByWarehouseId(UUID warehouseId, Pageable pageable);

    @Query("""
            SELECT sl FROM StockLevel sl
            JOIN FETCH sl.warehouse
            """)
    Page<StockLevel> findAllWithWarehouse(Pageable pageable);

    @Query("""
            SELECT sl FROM StockLevel sl
            JOIN FETCH sl.warehouse
            WHERE sl.itemId = :itemId AND sl.itemType = :itemType
            """)
    List<StockLevel> findAllByItemWithWarehouse(UUID itemId, StockItemType itemType);

    @Query("""
            SELECT sl FROM StockLevel sl
            JOIN FETCH sl.warehouse
            WHERE sl.quantityOnHand <= :threshold AND sl.itemType = :itemType
            """)
    List<StockLevel> findLowStock(StockItemType itemType, java.math.BigDecimal threshold);

    /**
     * Count all stock records (across all types and warehouses) where quantity on hand
     * is at or below the given threshold.
     */
    @Query("SELECT COUNT(sl) FROM StockLevel sl WHERE sl.quantityOnHand <= :threshold")
    long countLowStock(java.math.BigDecimal threshold);
}

