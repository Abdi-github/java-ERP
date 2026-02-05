package ch.swiftapp.erp.qualitycontrol.service;

import ch.swiftapp.erp.qualitycontrol.dto.*;
import ch.swiftapp.erp.qualitycontrol.event.QualityCheckFailedEvent;
import ch.swiftapp.erp.qualitycontrol.model.*;
import ch.swiftapp.erp.qualitycontrol.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly = true)
public class QualityCheckService {
    private final QualityCheckRepository repo;
    private final InspectionPlanRepository planRepo;
    private final ApplicationEventPublisher eventPublisher;
    private static final AtomicLong SEQ = new AtomicLong(System.currentTimeMillis() % 100000);

    public Page<QualityCheckResponse> findAll(Pageable p) { return repo.findAllByOrderByCheckDateDesc(p).map(this::toResponse); }
    public QualityCheckResponse findById(UUID id) { return toResponse(repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Quality check not found: " + id))); }

    @Transactional
    public QualityCheckResponse create(QualityCheckRequest r) {
        log.info("Creating quality check for inspection plan={}", r.inspectionPlanId());
        var plan = planRepo.findById(r.inspectionPlanId()).orElseThrow(() -> new EntityNotFoundException("Inspection plan not found: " + r.inspectionPlanId()));
        
        var qc = QualityCheck.builder().checkNumber("QC-%06d".formatted(SEQ.incrementAndGet()))
                .inspectionPlan(plan).productionOrderId(r.productionOrderId())
                .checkedBy(r.checkedBy()).checkDate(r.checkDate()).result(r.result()).notes(r.notes()).build();
        
        qc = repo.save(qc);
        
        if (qc.getResult() == CheckResult.FAIL) {
            eventPublisher.publishEvent(new QualityCheckFailedEvent(qc.getId(), qc.getCheckNumber(), qc.getProductionOrderId()));
        }
        
        return toResponse(qc);
    }

    private QualityCheckResponse toResponse(QualityCheck qc) {
        return new QualityCheckResponse(qc.getId(), qc.getCheckNumber(), qc.getInspectionPlan().getId(), qc.getInspectionPlan().getName(), qc.getProductionOrderId(), qc.getCheckedBy(), qc.getCheckDate(), qc.getResult(), qc.getNotes(), qc.getCreatedAt());
    }
}

