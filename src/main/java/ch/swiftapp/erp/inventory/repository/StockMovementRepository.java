package ch.swiftapp.erp.inventory.repository;

import ch.swiftapp.erp.inventory.model.MovementType;
import ch.swiftapp.erp.inventory.model.StockItemType;
import ch.swiftapp.erp.inventory.model.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

/**
 * Repository for {@link StockMovement} entities.
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    Page<StockMovement> findAllByOrderByMovementDateDesc(Pageable pageable);

    Page<StockMovement> findAllByItemIdAndItemTypeOrderByMovementDateDesc(
            UUID itemId, StockItemType itemType, Pageable pageable);

    Page<StockMovement> findAllByMovementTypeOrderByMovementDateDesc(
            MovementType movementType, Pageable pageable);

    @Query("""
            SELECT sm FROM StockMovement sm
            WHERE sm.movementDate BETWEEN :from AND :to
            ORDER BY sm.movementDate DESC
            """)
    Page<StockMovement> findAllByDateRange(Instant from, Instant to, Pageable pageable);

    boolean existsByReferenceNumber(String referenceNumber);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(sm.referenceNumber, 5) AS long)), 0) FROM StockMovement sm WHERE sm.referenceNumber LIKE :prefix")
    long findMaxReferenceNumberByPrefix(String prefix);
}

