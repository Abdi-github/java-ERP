package ch.swiftapp.erp.production.repository;

import ch.swiftapp.erp.production.model.WorkCenterTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkCenterTranslationRepository extends JpaRepository<WorkCenterTranslation, UUID> {
    List<WorkCenterTranslation> findByWorkCenterId(UUID workCenterId);
    Optional<WorkCenterTranslation> findByWorkCenterIdAndLocale(UUID workCenterId, String locale);
    void deleteByWorkCenterId(UUID workCenterId);
}

