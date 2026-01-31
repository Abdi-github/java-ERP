package ch.swiftapp.erp.production.service;

import ch.swiftapp.erp.production.dto.WorkCenterRequest;
import ch.swiftapp.erp.production.dto.WorkCenterResponse;
import ch.swiftapp.erp.production.model.WorkCenter;
import ch.swiftapp.erp.production.model.WorkCenterTranslation;
import ch.swiftapp.erp.production.repository.WorkCenterRepository;
import ch.swiftapp.erp.shared.service.TranslationResolver;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service @RequiredArgsConstructor @Slf4j
@Transactional(readOnly = true)
public class WorkCenterService {

    private final WorkCenterRepository repository;
    private final TranslationResolver translationResolver;

    public Page<WorkCenterResponse> findAll(Pageable pageable) { return repository.findAllByDeletedAtIsNull(pageable).map(this::toResponse); }
    public Page<WorkCenterResponse> findAllActive(Pageable pageable) { return repository.findAllByDeletedAtIsNullAndActiveTrue(pageable).map(this::toResponse); }
    public WorkCenterResponse findById(UUID id) { return toResponse(findEntity(id)); }
    public Page<WorkCenterResponse> search(String q, Pageable pageable) { return repository.search(q, pageable).map(this::toResponse); }

    WorkCenter findEntity(UUID id) {
        return repository.findById(id).filter(w -> w.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Work center not found: " + id));
    }

    @Transactional
    public WorkCenterResponse create(WorkCenterRequest r) {
        if (repository.existsByCodeIgnoreCase(r.code())) throw new IllegalArgumentException("Code already exists: " + r.code());
        var wc = new WorkCenter();
        mapToEntity(r, wc);
        applyTranslations(r.nameTranslations(), r.descriptionTranslations(), wc);
        return toResponse(repository.save(wc));
    }

    @Transactional
    public WorkCenterResponse update(UUID id, WorkCenterRequest r) {
        var wc = findEntity(id);
        if (!wc.getCode().equalsIgnoreCase(r.code()) && repository.existsByCodeIgnoreCase(r.code()))
            throw new IllegalArgumentException("Code already exists: " + r.code());
        mapToEntity(r, wc);
        wc.getTranslations().clear();
        applyTranslations(r.nameTranslations(), r.descriptionTranslations(), wc);
        return toResponse(repository.save(wc));
    }

    @Transactional
    public void delete(UUID id) {
        var wc = findEntity(id); wc.setDeletedAt(Instant.now()); wc.setActive(false); repository.save(wc);
    }

    private void mapToEntity(WorkCenterRequest r, WorkCenter wc) {
        wc.setCode(r.code()); wc.setName(r.name()); wc.setDescription(r.description());
        wc.setCapacityPerDay(r.capacityPerDay() != null ? r.capacityPerDay() : BigDecimal.ONE);
        wc.setCostPerHour(r.costPerHour() != null ? r.costPerHour() : BigDecimal.ZERO);
        wc.setActive(r.active() != null ? r.active() : true);
    }

    private WorkCenterResponse toResponse(WorkCenter e) {
        var t = e.getTranslations();
        return new WorkCenterResponse(e.getId(), e.getCode(),
                translationResolver.resolve(t, WorkCenterTranslation::getLocale, WorkCenterTranslation::getName, e.getName()),
                translationResolver.resolve(t, WorkCenterTranslation::getLocale, WorkCenterTranslation::getDescription, e.getDescription()),
                e.getCapacityPerDay(), e.getCostPerHour(), e.getActive(), e.getCreatedAt(), e.getUpdatedAt(),
                translationResolver.toMap(t, WorkCenterTranslation::getLocale, WorkCenterTranslation::getName),
                translationResolver.toMap(t, WorkCenterTranslation::getLocale, WorkCenterTranslation::getDescription));
    }

    private void applyTranslations(Map<String, String> names, Map<String, String> descriptions, WorkCenter wc) {
        if (names == null || names.isEmpty()) return;
        names.forEach((locale, name) -> {
            var translation = new WorkCenterTranslation();
            translation.setLocale(locale);
            translation.setWorkCenter(wc);
            translation.setName(name);
            translation.setDescription(descriptions != null ? descriptions.get(locale) : null);
            wc.getTranslations().add(translation);
        });
    }
}
