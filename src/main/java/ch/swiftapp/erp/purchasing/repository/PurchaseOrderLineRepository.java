package ch.swiftapp.erp.purchasing.repository;

import ch.swiftapp.erp.purchasing.model.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine, UUID> {
    List<PurchaseOrderLine> findAllByPurchaseOrderIdOrderByPositionAsc(UUID purchaseOrderId);
}

