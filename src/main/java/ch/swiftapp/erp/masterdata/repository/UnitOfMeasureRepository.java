package ch.swiftapp.erp.masterdata.repository;

import ch.swiftapp.erp.masterdata.model.UnitOfMeasure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link UnitOfMeasure} entities.
 */
@Repository
public interface UnitOfMeasureRepository extends JpaRepository<UnitOfMeasure, UUID> {

    Optional<UnitOfMeasure> findByCodeIgnoreCase(String code);

    Page<UnitOfMeasure> findAllByDeletedAtIsNull(Pageable pageable);

    List<UnitOfMeasure> findAllByDeletedAtIsNull();

    boolean existsByCodeIgnoreCase(String code);
}

