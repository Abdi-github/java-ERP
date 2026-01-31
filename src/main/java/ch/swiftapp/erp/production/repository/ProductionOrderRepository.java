package ch.swiftapp.erp.production.repository;

import ch.swiftapp.erp.production.model.ProductionOrder;
import ch.swiftapp.erp.production.model.ProductionOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, UUID> {
    Page<ProductionOrder> findAllByDeletedAtIsNull(Pageable pageable);
    Page<ProductionOrder> findAllByDeletedAtIsNullAndStatus(ProductionOrderStatus status, Pageable pageable);

    @Query("SELECT o FROM ProductionOrder o WHERE o.deletedAt IS NULL AND LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<ProductionOrder> search(String q, Pageable pageable);

    @Query("SELECT COUNT(o) FROM ProductionOrder o WHERE o.deletedAt IS NULL AND o.status NOT IN (ch.swiftapp.erp.production.model.ProductionOrderStatus.COMPLETED, ch.swiftapp.erp.production.model.ProductionOrderStatus.CANCELLED)")
    long countOpenOrders();

    @Query("SELECT COALESCE(SUM(o.plannedQuantity), 0) FROM ProductionOrder o WHERE o.deletedAt IS NULL AND o.productId = :productId AND o.status NOT IN (ch.swiftapp.erp.production.model.ProductionOrderStatus.COMPLETED, ch.swiftapp.erp.production.model.ProductionOrderStatus.CANCELLED)")
    BigDecimal sumPlannedQuantityByProductId(UUID productId);

    @Query("SELECT MAX(o.orderNumber) FROM ProductionOrder o WHERE o.orderNumber LIKE :prefix")
    Optional<String> findMaxOrderNumberByPrefix(String prefix);
}

