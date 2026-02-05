package ch.swiftapp.erp.qualitycontrol.service;

import ch.swiftapp.erp.qualitycontrol.dto.*;
import ch.swiftapp.erp.qualitycontrol.model.InspectionPlan;
import ch.swiftapp.erp.qualitycontrol.repository.InspectionPlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly = true)
public class InspectionPlanService {
    private final InspectionPlanRepository repo;

    public Page<InspectionPlanResponse> findAll(Pageable p) { return repo.findAllByDeletedAtIsNull(p).map(this::toResponse); }
    public List<InspectionPlanResponse> findAllActive() { return repo.findAllByDeletedAtIsNullAndActiveTrue().stream().map(this::toResponse).toList(); }
    public Page<InspectionPlanResponse> search(String q, Pageable p) { return repo.searchPlans(q, p).map(this::toResponse); }
    public InspectionPlanResponse findById(UUID id) { return toResponse(findEntity(id)); }

    @Transactional
    public InspectionPlanResponse create(InspectionPlanRequest r) {
        if (repo.existsByPlanNumberIgnoreCase(r.planNumber())) throw new IllegalArgumentException("Plan number exists: " + r.planNumber());
        var ip = new InspectionPlan(); mapToEntity(r, ip); ip = repo.save(ip); return toResponse(ip);
    }

    @Transactional
    public InspectionPlanResponse update(UUID id, InspectionPlanRequest r) {
        var ip = findEntity(id);
        if (!ip.getPlanNumber().equalsIgnoreCase(r.planNumber()) && repo.existsByPlanNumberIgnoreCase(r.planNumber()))
            throw new IllegalArgumentException("Plan number exists: " + r.planNumber());
        mapToEntity(r, ip); ip = repo.save(ip); return toResponse(ip);
    }

    @Transactional
    public void delete(UUID id) { var ip = findEntity(id); ip.setDeletedAt(Instant.now()); repo.save(ip); }

    private InspectionPlan findEntity(UUID id) { return repo.findById(id).filter(ip -> ip.getDeletedAt() == null).orElseThrow(() -> new EntityNotFoundException("Inspection plan not found: " + id)); }
    private void mapToEntity(InspectionPlanRequest r, InspectionPlan ip) {
        ip.setPlanNumber(r.planNumber()); ip.setName(r.name()); ip.setDescription(r.description());
        ip.setProductId(r.productId()); ip.setMaterialId(r.materialId()); ip.setActive(r.active() != null ? r.active() : true);
    }
    private InspectionPlanResponse toResponse(InspectionPlan ip) {
        return new InspectionPlanResponse(ip.getId(), ip.getPlanNumber(), ip.getName(), ip.getDescription(), ip.getProductId(), ip.getMaterialId(), ip.getActive(), ip.getCreatedAt(), ip.getUpdatedAt());
    }
}

