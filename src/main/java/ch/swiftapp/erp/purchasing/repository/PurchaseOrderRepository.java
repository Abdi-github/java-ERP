package ch.swiftapp.erp.purchasing.repository;

import ch.swiftapp.erp.purchasing.model.PurchaseOrder;
import ch.swiftapp.erp.purchasing.model.PurchaseOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    Page<PurchaseOrder> findAllByDeletedAtIsNull(Pageable pageable);

    Page<PurchaseOrder> findAllByDeletedAtIsNullAndStatus(PurchaseOrderStatus status, Pageable pageable);

    Page<PurchaseOrder> findAllBySupplierIdAndDeletedAtIsNull(UUID supplierId, Pageable pageable);

    @Query("""
            SELECT o FROM PurchaseOrder o LEFT JOIN FETCH o.supplier s
            WHERE o.deletedAt IS NULL
              AND (LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(COALESCE(s.companyName, '')) LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<PurchaseOrder> search(String q, Pageable pageable);

    boolean existsByOrderNumberIgnoreCase(String orderNumber);

    @Query("""
            SELECT COUNT(o) FROM PurchaseOrder o
            WHERE o.deletedAt IS NULL
              AND o.status NOT IN (ch.swiftapp.erp.purchasing.model.PurchaseOrderStatus.COMPLETED,
                                   ch.swiftapp.erp.purchasing.model.PurchaseOrderStatus.CANCELLED)
            """)
    long countOpenOrders();

    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0) FROM PurchaseOrder o
            WHERE o.deletedAt IS NULL
              AND o.status = ch.swiftapp.erp.purchasing.model.PurchaseOrderStatus.COMPLETED
              AND o.orderDate BETWEEN :start AND :end
            """)
    BigDecimal sumSpendByDateRange(LocalDate start, LocalDate end);

    @Query("SELECT MAX(o.orderNumber) FROM PurchaseOrder o WHERE o.orderNumber LIKE :prefix")
    Optional<String> findMaxOrderNumberByPrefix(String prefix);
}

