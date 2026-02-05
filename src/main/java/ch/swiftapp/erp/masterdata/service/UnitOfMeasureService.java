package ch.swiftapp.erp.masterdata.service;

import ch.swiftapp.erp.masterdata.dto.UnitOfMeasureRequest;
import ch.swiftapp.erp.masterdata.dto.UnitOfMeasureResponse;
import ch.swiftapp.erp.masterdata.model.UnitOfMeasure;
import ch.swiftapp.erp.masterdata.model.UnitOfMeasureTranslation;
import ch.swiftapp.erp.masterdata.repository.UnitOfMeasureRepository;
import ch.swiftapp.erp.shared.service.TranslationResolver;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing units of measure (PCS, GRM, MTR, etc.).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UnitOfMeasureService {

    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final TranslationResolver translationResolver;

    public Page<UnitOfMeasureResponse> findAll(Pageable pageable) {
        return unitOfMeasureRepository.findAllByDeletedAtIsNull(pageable)
                .map(this::toResponse);
    }

    public List<UnitOfMeasureResponse> findAllFlat() {
        return unitOfMeasureRepository.findAllByDeletedAtIsNull().stream()
                .map(this::toResponse)
                .toList();
    }

    public UnitOfMeasureResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public UnitOfMeasureResponse create(UnitOfMeasureRequest request) {
        log.info("Creating unit of measure: {}", request.code());

        if (unitOfMeasureRepository.existsByCodeIgnoreCase(request.code())) {
            throw new IllegalArgumentException("Unit code already exists: " + request.code());
        }

        var uom = new UnitOfMeasure();
        uom.setCode(request.code().toUpperCase());
        uom.setName(request.name());
        uom.setDescription(request.description());
        
        applyTranslations(request.nameTranslations(), request.descriptionTranslations(), uom);

        uom = unitOfMeasureRepository.save(uom);
        
        log.info("Created unit of measure id={} code={}", uom.getId(), uom.getCode());
        return toResponse(uom);
    }

    @Transactional
    public UnitOfMeasureResponse update(UUID id, UnitOfMeasureRequest request) {
        log.info("Updating unit of measure id={}", id);

        var uom = findEntityById(id);

        if (!uom.getCode().equalsIgnoreCase(request.code())
                && unitOfMeasureRepository.existsByCodeIgnoreCase(request.code())) {
            throw new IllegalArgumentException("Unit code already exists: " + request.code());
        }

        uom.setCode(request.code().toUpperCase());
        uom.setName(request.name());
        uom.setDescription(request.description());
        
        uom.getTranslations().clear();
        
        applyTranslations(request.nameTranslations(), request.descriptionTranslations(), uom);

        uom = unitOfMeasureRepository.save(uom);
        
        log.info("Updated unit of measure id={} code={}", uom.getId(), uom.getCode());
        return toResponse(uom);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Soft-deleting unit of measure id={}", id);
        var uom = findEntityById(id);
        
        uom.setDeletedAt(Instant.now());
        
        unitOfMeasureRepository.save(uom);
    }

    // ── Internal helpers ──────────────────────────────────────

    private UnitOfMeasure findEntityById(UUID id) {
        return unitOfMeasureRepository.findById(id)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Unit of measure not found: " + id));
    }

    private UnitOfMeasureResponse toResponse(UnitOfMeasure entity) {
        var t = entity.getTranslations();
        return new UnitOfMeasureResponse(
                entity.getId(),
                entity.getCode(),
                translationResolver.resolve(t, UnitOfMeasureTranslation::getLocale, UnitOfMeasureTranslation::getName, entity.getName()),
                translationResolver.resolve(t, UnitOfMeasureTranslation::getLocale, UnitOfMeasureTranslation::getDescription, entity.getDescription()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                translationResolver.toMap(t, UnitOfMeasureTranslation::getLocale, UnitOfMeasureTranslation::getName),
                translationResolver.toMap(t, UnitOfMeasureTranslation::getLocale, UnitOfMeasureTranslation::getDescription)
        );
    }

    private void applyTranslations(Map<String, String> names, Map<String, String> descriptions, UnitOfMeasure uom) {
        if (names == null || names.isEmpty()) return;
        names.forEach((locale, name) -> {
            var translation = new UnitOfMeasureTranslation();
            translation.setLocale(locale);
            translation.setUnitOfMeasure(uom);
            translation.setName(name);
            translation.setDescription(descriptions != null ? descriptions.get(locale) : null);
            uom.getTranslations().add(translation);
        });
    }
}
