package ch.swiftapp.erp.qualitycontrol.repository;

import ch.swiftapp.erp.qualitycontrol.model.QualityCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface QualityCheckRepository extends JpaRepository<QualityCheck, UUID> {
    Page<QualityCheck> findAllByOrderByCheckDateDesc(Pageable pageable);
    Page<QualityCheck> findAllByInspectionPlanId(UUID planId, Pageable pageable);
    boolean existsByCheckNumberIgnoreCase(String checkNumber);
}

