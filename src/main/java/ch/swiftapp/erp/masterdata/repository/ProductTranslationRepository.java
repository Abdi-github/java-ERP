package ch.swiftapp.erp.masterdata.repository;

import ch.swiftapp.erp.masterdata.model.ProductTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductTranslationRepository extends JpaRepository<ProductTranslation, UUID> {
    List<ProductTranslation> findByProductId(UUID productId);
    Optional<ProductTranslation> findByProductIdAndLocale(UUID productId, String locale);
    void deleteByProductId(UUID productId);
}

