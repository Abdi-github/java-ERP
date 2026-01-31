package ch.swiftapp.erp.production.repository;

import ch.swiftapp.erp.production.model.WorkCenter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface WorkCenterRepository extends JpaRepository<WorkCenter, UUID> {
    Page<WorkCenter> findAllByDeletedAtIsNull(Pageable pageable);
    Page<WorkCenter> findAllByDeletedAtIsNullAndActiveTrue(Pageable pageable);

    @Query("SELECT w FROM WorkCenter w WHERE w.deletedAt IS NULL AND (LOWER(w.code) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(w.name) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<WorkCenter> search(String q, Pageable pageable);

    boolean existsByCodeIgnoreCase(String code);
}

