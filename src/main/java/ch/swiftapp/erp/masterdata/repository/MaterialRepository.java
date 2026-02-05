package ch.swiftapp.erp.masterdata.repository;

import ch.swiftapp.erp.masterdata.model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Material} entities.
 */
@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {

    Optional<Material> findBySkuIgnoreCaseAndDeletedAtIsNull(String sku);

    Page<Material> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("""
            SELECT m FROM Material m
            WHERE m.deletedAt IS NULL
              AND (LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(m.sku) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Material> searchByNameOrSku(String search, Pageable pageable);

    boolean existsBySkuIgnoreCase(String sku);
}

