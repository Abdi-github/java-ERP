package ch.swiftapp.erp.qualitycontrol.repository;

import ch.swiftapp.erp.qualitycontrol.model.InspectionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface InspectionPlanRepository extends JpaRepository<InspectionPlan, UUID> {
    Page<InspectionPlan> findAllByDeletedAtIsNull(Pageable pageable);
    List<InspectionPlan> findAllByDeletedAtIsNullAndActiveTrue();
    boolean existsByPlanNumberIgnoreCase(String planNumber);

    @Query("SELECT ip FROM InspectionPlan ip WHERE ip.deletedAt IS NULL AND (LOWER(ip.name) LIKE LOWER(CONCAT('%',:s,'%')) OR LOWER(ip.planNumber) LIKE LOWER(CONCAT('%',:s,'%')))")
    Page<InspectionPlan> searchPlans(String s, Pageable pageable);
}

