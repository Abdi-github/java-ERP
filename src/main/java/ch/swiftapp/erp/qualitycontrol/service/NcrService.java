package ch.swiftapp.erp.qualitycontrol.service;

import ch.swiftapp.erp.qualitycontrol.dto.*;
import ch.swiftapp.erp.qualitycontrol.event.NonConformanceReportCreatedEvent;
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
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly = true)
public class NcrService {
    private final NonConformanceReportRepository repo;
    private final QualityCheckRepository checkRepo;
    private final ApplicationEventPublisher eventPublisher;
    private static final AtomicLong SEQ = new AtomicLong(System.currentTimeMillis() % 100000);

    public Page<NcrResponse> findAll(Pageable p) { return repo.findAllByOrderByCreatedAtDesc(p).map(this::toResponse); }
    public Page<NcrResponse> findByStatus(NcrStatus status, Pageable p) { return repo.findAllByStatus(status, p).map(this::toResponse); }
    public NcrResponse findById(UUID id) { return toResponse(repo.findById(id).orElseThrow(() -> new EntityNotFoundException("NCR not found: " + id))); }

    @Transactional
    public NcrResponse create(NcrRequest r) {
        var check = checkRepo.findById(r.qualityCheckId()).orElseThrow(() -> new EntityNotFoundException("Quality check not found: " + r.qualityCheckId()));
        var ncr = NonConformanceReport.builder().ncrNumber("NCR-%06d".formatted(SEQ.incrementAndGet()))
                .qualityCheck(check).severity(r.severity()).description(r.description())
                .correctiveAction(r.correctiveAction()).status(NcrStatus.OPEN).build();
        ncr = repo.save(ncr);
        eventPublisher.publishEvent(new NonConformanceReportCreatedEvent(ncr.getId(), ncr.getNcrNumber(), check.getId()));
        return toResponse(ncr);
    }

    @Transactional
    public NcrResponse close(UUID id) {
        var ncr = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("NCR not found: " + id));
        ncr.setStatus(NcrStatus.CLOSED);
        ncr.setClosedAt(Instant.now());
        return toResponse(repo.save(ncr));
    }

    private NcrResponse toResponse(NonConformanceReport ncr) {
        return new NcrResponse(ncr.getId(), ncr.getNcrNumber(), ncr.getQualityCheck().getId(), ncr.getQualityCheck().getCheckNumber(), ncr.getSeverity(), ncr.getDescription(), ncr.getCorrectiveAction(), ncr.getStatus(), ncr.getClosedAt(), ncr.getCreatedAt());
    }
}

