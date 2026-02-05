package ch.swiftapp.erp.qualitycontrol.repository;

import ch.swiftapp.erp.qualitycontrol.model.NonConformanceReport;
import ch.swiftapp.erp.qualitycontrol.model.NcrStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface NonConformanceReportRepository extends JpaRepository<NonConformanceReport, UUID> {
    Page<NonConformanceReport> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<NonConformanceReport> findAllByStatus(NcrStatus status, Pageable pageable);
    boolean existsByNcrNumberIgnoreCase(String ncrNumber);

    @Query("SELECT COUNT(ncr) > 0 FROM NonConformanceReport ncr WHERE ncr.qualityCheck.productionOrderId = :productionOrderId AND ncr.status <> 'CLOSED'")
    boolean hasOpenNcrsForProductionOrder(UUID productionOrderId);
}

