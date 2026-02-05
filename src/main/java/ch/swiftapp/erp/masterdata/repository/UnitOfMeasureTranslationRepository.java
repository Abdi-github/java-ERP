package ch.swiftapp.erp.masterdata.repository;

import ch.swiftapp.erp.masterdata.model.UnitOfMeasureTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UnitOfMeasureTranslationRepository extends JpaRepository<UnitOfMeasureTranslation, UUID> {
    List<UnitOfMeasureTranslation> findByUnitOfMeasureId(UUID uomId);
    Optional<UnitOfMeasureTranslation> findByUnitOfMeasureIdAndLocale(UUID uomId, String locale);
    void deleteByUnitOfMeasureId(UUID uomId);
}

