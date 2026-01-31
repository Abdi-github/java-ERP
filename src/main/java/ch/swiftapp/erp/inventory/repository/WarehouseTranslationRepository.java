package ch.swiftapp.erp.inventory.repository;

import ch.swiftapp.erp.inventory.model.WarehouseTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseTranslationRepository extends JpaRepository<WarehouseTranslation, UUID> {
    List<WarehouseTranslation> findByWarehouseId(UUID warehouseId);
    Optional<WarehouseTranslation> findByWarehouseIdAndLocale(UUID warehouseId, String locale);
    void deleteByWarehouseId(UUID warehouseId);
}

