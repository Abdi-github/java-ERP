package ch.swiftapp.erp.sales.repository;

import ch.swiftapp.erp.sales.model.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link SalesOrderLine} entities.
 */
@Repository
public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, UUID> {

    List<SalesOrderLine> findAllBySalesOrderIdOrderByPositionAsc(UUID salesOrderId);

    void deleteAllBySalesOrderId(UUID salesOrderId);
}

