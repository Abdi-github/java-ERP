package ch.swiftapp.erp.inventory.service;

import ch.swiftapp.erp.inventory.dto.WarehouseRequest;
import ch.swiftapp.erp.inventory.dto.WarehouseResponse;
import ch.swiftapp.erp.inventory.model.Warehouse;
import ch.swiftapp.erp.inventory.model.WarehouseTranslation;
import ch.swiftapp.erp.inventory.repository.WarehouseRepository;
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
 * Service for managing warehouses (physical storage locations).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final TranslationResolver translationResolver;

    public Page<WarehouseResponse> findAll(Pageable pageable) {
        return warehouseRepository.findAllByDeletedAtIsNull(pageable)
                .map(this::toResponse);
    }

    public List<WarehouseResponse> findAllActive() {
        return warehouseRepository.findAllByDeletedAtIsNullAndActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public WarehouseResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    public Page<WarehouseResponse> search(String query, Pageable pageable) {
        return warehouseRepository.searchByNameOrCode(query, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public WarehouseResponse create(WarehouseRequest request) {
        log.info("Creating warehouse: code={} name={}", request.code(), request.name());

        if (warehouseRepository.existsByCodeIgnoreCase(request.code())) {
            throw new IllegalArgumentException("Warehouse code already exists: " + request.code());
        }

        var warehouse = new Warehouse();
        mapRequestToEntity(request, warehouse);
        applyTranslations(request.nameTranslations(), request.descriptionTranslations(), warehouse);
        warehouse = warehouseRepository.save(warehouse);

        log.info("Created warehouse id={} code={}", warehouse.getId(), warehouse.getCode());
        return toResponse(warehouse);
    }

    @Transactional
    public WarehouseResponse update(UUID id, WarehouseRequest request) {
        log.info("Updating warehouse id={}", id);

        var warehouse = findEntityById(id);

        if (!warehouse.getCode().equalsIgnoreCase(request.code())
                && warehouseRepository.existsByCodeIgnoreCase(request.code())) {
            throw new IllegalArgumentException("Warehouse code already exists: " + request.code());
        }

        mapRequestToEntity(request, warehouse);
        warehouse.getTranslations().clear();
        applyTranslations(request.nameTranslations(), request.descriptionTranslations(), warehouse);
        warehouse = warehouseRepository.save(warehouse);

        log.info("Updated warehouse id={} code={}", warehouse.getId(), warehouse.getCode());
        return toResponse(warehouse);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Soft-deleting warehouse id={}", id);
        var warehouse = findEntityById(id);
        warehouse.setDeletedAt(Instant.now());
        warehouse.setActive(false);
        warehouseRepository.save(warehouse);
    }

    // ── Internal helpers ──────────────────────────────────────

    Warehouse findEntityById(UUID id) {
        return warehouseRepository.findById(id)
                .filter(w -> w.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found: " + id));
    }

    private void mapRequestToEntity(WarehouseRequest request, Warehouse warehouse) {
        warehouse.setCode(request.code().toUpperCase());
        warehouse.setName(request.name());
        warehouse.setDescription(request.description());
        warehouse.setAddress(request.address());
        warehouse.setActive(request.active() != null ? request.active() : true);
    }

    private WarehouseResponse toResponse(Warehouse entity) {
        var t = entity.getTranslations();
        return new WarehouseResponse(
                entity.getId(),
                entity.getCode(),
                translationResolver.resolve(t, WarehouseTranslation::getLocale, WarehouseTranslation::getName, entity.getName()),
                translationResolver.resolve(t, WarehouseTranslation::getLocale, WarehouseTranslation::getDescription, entity.getDescription()),
                entity.getAddress(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                translationResolver.toMap(t, WarehouseTranslation::getLocale, WarehouseTranslation::getName),
                translationResolver.toMap(t, WarehouseTranslation::getLocale, WarehouseTranslation::getDescription)
        );
    }

    private void applyTranslations(Map<String, String> names, Map<String, String> descriptions, Warehouse warehouse) {
        if (names == null || names.isEmpty()) return;
        names.forEach((locale, name) -> {
            var translation = new WarehouseTranslation();
            translation.setLocale(locale);
            translation.setWarehouse(warehouse);
            translation.setName(name);
            translation.setDescription(descriptions != null ? descriptions.get(locale) : null);
            warehouse.getTranslations().add(translation);
        });
    }
}
