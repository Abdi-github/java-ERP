package ch.swiftapp.erp.masterdata.repository;

import ch.swiftapp.erp.masterdata.model.MaterialTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaterialTranslationRepository extends JpaRepository<MaterialTranslation, UUID> {
    List<MaterialTranslation> findByMaterialId(UUID materialId);
    Optional<MaterialTranslation> findByMaterialIdAndLocale(UUID materialId, String locale);
    void deleteByMaterialId(UUID materialId);
}

