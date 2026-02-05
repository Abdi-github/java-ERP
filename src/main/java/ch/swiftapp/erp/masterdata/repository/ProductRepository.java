package ch.swiftapp.erp.masterdata.repository;

import ch.swiftapp.erp.masterdata.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Product} entities.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findBySkuIgnoreCaseAndDeletedAtIsNull(String sku);

    Page<Product> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Product> findAllByDeletedAtIsNullAndActiveTrue(Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE p.deletedAt IS NULL
              AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Product> searchByNameOrSku(String search, Pageable pageable);

    boolean existsBySkuIgnoreCase(String sku);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.id = :id AND p.active = true AND p.deletedAt IS NULL")
    boolean isActiveAndNotDeleted(UUID id);
}

