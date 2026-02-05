package ch.swiftapp.erp.masterdata.service;

import ch.swiftapp.erp.masterdata.dto.MaterialRequest;
import ch.swiftapp.erp.masterdata.dto.MaterialResponse;
import ch.swiftapp.erp.masterdata.event.MaterialCreatedEvent;
import ch.swiftapp.erp.masterdata.event.MaterialDeletedEvent;
import ch.swiftapp.erp.masterdata.event.MaterialUpdatedEvent;
import ch.swiftapp.erp.masterdata.model.Material;
import ch.swiftapp.erp.masterdata.model.MaterialTranslation;
import ch.swiftapp.erp.masterdata.repository.CategoryRepository;
import ch.swiftapp.erp.masterdata.repository.MaterialRepository;
import ch.swiftapp.erp.masterdata.repository.UnitOfMeasureRepository;
import ch.swiftapp.erp.shared.service.TranslationResolver;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing raw materials and components used in watch manufacturing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CategoryRepository categoryRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TranslationResolver translationResolver;

    public Page<MaterialResponse> findAll(Pageable pageable) {
        return materialRepository.findAllByDeletedAtIsNull(pageable)
                .map(this::toResponse);
    }

    public MaterialResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    public Optional<MaterialResponse> findByIdOptional(UUID id) {
        return materialRepository.findById(id)
                .filter(m -> m.getDeletedAt() == null)
                .map(this::toResponse);
    }

    public Page<MaterialResponse> search(String query, Pageable pageable) {
        return materialRepository.searchByNameOrSku(query, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public MaterialResponse create(MaterialRequest request) {
        log.info("Creating material: sku={} name={}", request.sku(), request.name());

        if (materialRepository.existsBySkuIgnoreCase(request.sku())) {
            throw new IllegalArgumentException("Material SKU already exists: " + request.sku());
        }

        var material = new Material();
        mapRequestToEntity(request, material);
        
        applyTranslations(request.nameTranslations(), request.descriptionTranslations(), material);
        
        material = materialRepository.save(material);

        // TODO: Validate event state before publishing
        eventPublisher.publishEvent(new MaterialCreatedEvent(
                material.getId(), material.getSku(), material.getName()));

        log.info("Created material id={} sku={}", material.getId(), material.getSku());
        return toResponse(material);
    }

    @Transactional
    public MaterialResponse update(UUID id, MaterialRequest request) {
        log.info("Updating material id={}", id);

        var material = findEntityById(id);

        if (!material.getSku().equalsIgnoreCase(request.sku())
                && materialRepository.existsBySkuIgnoreCase(request.sku())) {
            throw new IllegalArgumentException("Material SKU already exists: " + request.sku());
        }

        mapRequestToEntity(request, material);
        
        material.getTranslations().clear();
        
        applyTranslations(request.nameTranslations(), request.descriptionTranslations(), material);
        
        material = materialRepository.save(material);

        eventPublisher.publishEvent(new MaterialUpdatedEvent(
                material.getId(), material.getSku(), material.getName()));

        log.info("Updated material id={} sku={}", material.getId(), material.getSku());
        return toResponse(material);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Soft-deleting material id={}", id);
        var material = findEntityById(id);
        
        material.setDeletedAt(Instant.now());
        
        materialRepository.save(material);

        eventPublisher.publishEvent(new MaterialDeletedEvent(material.getId(), material.getSku()));
    }

    // ── Internal helpers ──────────────────────────────────────

    private Material findEntityById(UUID id) {
        return materialRepository.findById(id)
                .filter(m -> m.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Material not found: " + id));
    }

    private void mapRequestToEntity(MaterialRequest request, Material material) {
        material.setSku(request.sku());
        material.setName(request.name());
        material.setDescription(request.description());
        material.setUnitPrice(request.unitPrice());
        material.setVatRate(request.vatRate());
        material.setMinimumStock(request.minimumStock());

        if (request.categoryId() != null) {
            var category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Category not found: " + request.categoryId()));
            material.setCategory(category);
        } else {
            material.setCategory(null);
        }

        if (request.unitOfMeasureId() != null) {
            var uom = unitOfMeasureRepository.findById(request.unitOfMeasureId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Unit of measure not found: " + request.unitOfMeasureId()));
            material.setUnitOfMeasure(uom);
        } else {
            material.setUnitOfMeasure(null);
        }
    }

    private MaterialResponse toResponse(Material entity) {
        var t = entity.getTranslations();
        return new MaterialResponse(
                entity.getId(),
                entity.getSku(),
                translationResolver.resolve(t, MaterialTranslation::getLocale, MaterialTranslation::getName, entity.getName()),
                translationResolver.resolve(t, MaterialTranslation::getLocale, MaterialTranslation::getDescription, entity.getDescription()),
                entity.getCategory() != null ? entity.getCategory().getId() : null,
                entity.getCategory() != null ? entity.getCategory().getName() : null,
                entity.getUnitOfMeasure() != null ? entity.getUnitOfMeasure().getId() : null,
                entity.getUnitOfMeasure() != null ? entity.getUnitOfMeasure().getCode() : null,
                entity.getUnitPrice(),
                entity.getVatRate(),
                entity.getMinimumStock(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                translationResolver.toMap(t, MaterialTranslation::getLocale, MaterialTranslation::getName),
                translationResolver.toMap(t, MaterialTranslation::getLocale, MaterialTranslation::getDescription)
        );
    }

    private void applyTranslations(Map<String, String> names, Map<String, String> descriptions, Material material) {
        if (names == null || names.isEmpty()) return;
        names.forEach((locale, name) -> {
            var translation = new MaterialTranslation();
            translation.setLocale(locale);
            translation.setMaterial(material);
            translation.setName(name);
            translation.setDescription(descriptions != null ? descriptions.get(locale) : null);
            material.getTranslations().add(translation);
        });
    }
}

