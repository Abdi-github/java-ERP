package ch.swiftapp.erp.sales.repository;

import ch.swiftapp.erp.sales.model.SalesOrder;
import ch.swiftapp.erp.sales.model.SalesOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link SalesOrder} entities.
 */
@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {

    Optional<SalesOrder> findByOrderNumberAndDeletedAtIsNull(String orderNumber);

    Page<SalesOrder> findAllByDeletedAtIsNull(Pageable pageable);

    Page<SalesOrder> findAllByDeletedAtIsNullAndStatus(SalesOrderStatus status, Pageable pageable);

    Page<SalesOrder> findAllByCustomerIdAndDeletedAtIsNull(UUID customerId, Pageable pageable);

    @Query("""
            SELECT o FROM SalesOrder o
            LEFT JOIN FETCH o.customer c
            WHERE o.deletedAt IS NULL
              AND (LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(c.companyName, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(c.firstName, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(c.lastName, '')) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<SalesOrder> search(String search, Pageable pageable);

    boolean existsByOrderNumberIgnoreCase(String orderNumber);

    /**
     * Count non-completed and non-cancelled orders.
     */
    @Query("""
            SELECT COUNT(o) FROM SalesOrder o
            WHERE o.deletedAt IS NULL
              AND o.status NOT IN (ch.swiftapp.erp.sales.model.SalesOrderStatus.COMPLETED,
                                   ch.swiftapp.erp.sales.model.SalesOrderStatus.CANCELLED)
            """)
    long countOpenOrders();

    /**
     * Sum total revenue for a given month (completed orders only).
     */
    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0) FROM SalesOrder o
            WHERE o.deletedAt IS NULL
              AND o.status = ch.swiftapp.erp.sales.model.SalesOrderStatus.COMPLETED
              AND o.orderDate BETWEEN :startDate AND :endDate
            """)
    BigDecimal sumRevenueByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Find the latest order number (for sequence generation).
     */
    @Query("SELECT MAX(o.orderNumber) FROM SalesOrder o WHERE o.orderNumber LIKE :prefix")
    Optional<String> findMaxOrderNumberByPrefix(String prefix);

    /**
     * Count orders grouped by status (for dashboard status-breakdown chart).
     */
    @Query("SELECT o.status, COUNT(o) FROM SalesOrder o WHERE o.deletedAt IS NULL GROUP BY o.status")
    List<Object[]> countGroupByStatus();
}

