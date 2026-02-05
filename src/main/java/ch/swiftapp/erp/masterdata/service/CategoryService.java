package ch.swiftapp.erp.masterdata.service;

import ch.swiftapp.erp.masterdata.dto.CategoryRequest;
import ch.swiftapp.erp.masterdata.dto.CategoryResponse;
import ch.swiftapp.erp.masterdata.model.Category;
import ch.swiftapp.erp.masterdata.model.CategoryTranslation;
import ch.swiftapp.erp.masterdata.repository.CategoryRepository;
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
 * Service for managing product/material categories.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TranslationResolver translationResolver;

    /**
     * List all non-deleted categories with pagination.
     */
    public Page<CategoryResponse> findAll(Pageable pageable) {
        return categoryRepository.findAllByDeletedAtIsNull(pageable)
                .map(this::toResponse);
    }

    /**
     * List all non-deleted categories (flat list, no pagination).
     */
    public List<CategoryResponse> findAllFlat() {
        return categoryRepository.findAllByDeletedAtIsNull().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * List root categories (no parent).
     */
    public List<CategoryResponse> findRootCategories() {
        return categoryRepository.findAllByDeletedAtIsNullAndParentCategoryIsNull().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Find a category by ID.
     */
    public CategoryResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    /**
     * Search categories by name.
     */
    public Page<CategoryResponse> search(String query, Pageable pageable) {
        return categoryRepository.searchByName(query, pageable)
                .map(this::toResponse);
    }

    /**
     * Create a new category.
     */
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        log.info("Creating category: {}", request.name());

        var category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());

        if (request.parentId() != null) {
            var parent = findEntityById(request.parentId());
            category.setParentCategory(parent);
        } else {
        }

        applyTranslations(request.nameTranslations(), request.descriptionTranslations(), category);
        
        category = categoryRepository.save(category);
        
        log.info("Created category id={} name={}", category.getId(), category.getName());
        return toResponse(category);
    }

    /**
     * Update an existing category.
     */
    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        log.info("Updating category id={}", id);

        var category = findEntityById(id);
        
        category.setName(request.name());
        category.setDescription(request.description());

        if (request.parentId() != null) {
            if (request.parentId().equals(id)) {
                throw new IllegalArgumentException("A category cannot be its own parent");
            }
            var parent = findEntityById(request.parentId());
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }

        category.getTranslations().clear();
        
        applyTranslations(request.nameTranslations(), request.descriptionTranslations(), category);
        
        category = categoryRepository.save(category);
        
        log.info("Updated category id={} name={}", category.getId(), category.getName());
        return toResponse(category);
    }

    /**
     * Soft-delete a category.
     */
    @Transactional
    public void delete(UUID id) {
        log.info("Soft-deleting category id={}", id);
        var category = findEntityById(id);
        
        category.setDeletedAt(Instant.now());
        
        categoryRepository.save(category);
    }

    // ── Internal helpers ──────────────────────────────────────

    private Category findEntityById(UUID id) {
        return categoryRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }

    private CategoryResponse toResponse(Category entity) {
        var t = entity.getTranslations();
        return new CategoryResponse(
                entity.getId(),
                translationResolver.resolve(t, CategoryTranslation::getLocale, CategoryTranslation::getName, entity.getName()),
                translationResolver.resolve(t, CategoryTranslation::getLocale, CategoryTranslation::getDescription, entity.getDescription()),
                entity.getParentCategory() != null ? entity.getParentCategory().getId() : null,
                entity.getParentCategory() != null ? entity.getParentCategory().getName() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                translationResolver.toMap(t, CategoryTranslation::getLocale, CategoryTranslation::getName),
                translationResolver.toMap(t, CategoryTranslation::getLocale, CategoryTranslation::getDescription)
        );
    }

    private void applyTranslations(Map<String, String> names, Map<String, String> descriptions, Category category) {
        if (names == null || names.isEmpty()) {
            return;
        }
        names.forEach((locale, name) -> {
            var translation = new CategoryTranslation();
            translation.setLocale(locale);
            translation.setCategory(category);
            translation.setName(name);
            translation.setDescription(descriptions != null ? descriptions.get(locale) : null);
            category.getTranslations().add(translation);
        });
    }
}

