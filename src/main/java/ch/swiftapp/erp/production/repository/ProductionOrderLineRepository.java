package ch.swiftapp.erp.production.repository;

import ch.swiftapp.erp.production.model.ProductionOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProductionOrderLineRepository extends JpaRepository<ProductionOrderLine, UUID> {}

