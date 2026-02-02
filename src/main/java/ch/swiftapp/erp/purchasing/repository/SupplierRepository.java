package ch.swiftapp.erp.purchasing.repository;

import ch.swiftapp.erp.purchasing.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    Page<Supplier> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Supplier> findAllByDeletedAtIsNullAndActiveTrue(Pageable pageable);

    @Query("""
            SELECT s FROM Supplier s WHERE s.deletedAt IS NULL
              AND (LOWER(COALESCE(s.companyName, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(COALESCE(s.firstName, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(COALESCE(s.lastName, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(s.supplierNumber) LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<Supplier> search(String q, Pageable pageable);

    boolean existsBySupplierNumberIgnoreCase(String supplierNumber);
}

