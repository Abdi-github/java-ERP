package ch.swiftapp.erp.inventory.repository;

import ch.swiftapp.erp.inventory.model.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Warehouse} entities.
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    Optional<Warehouse> findByCodeIgnoreCaseAndDeletedAtIsNull(String code);

    Page<Warehouse> findAllByDeletedAtIsNull(Pageable pageable);

    List<Warehouse> findAllByDeletedAtIsNullAndActiveTrue();

    @Query("""
            SELECT w FROM Warehouse w
            WHERE w.deletedAt IS NULL
              AND (LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(w.code) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Warehouse> searchByNameOrCode(String search, Pageable pageable);

    boolean existsByCodeIgnoreCase(String code);
}

