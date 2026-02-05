package ch.swiftapp.erp.masterdata.service;

import ch.swiftapp.erp.masterdata.dto.ProductRequest;
import ch.swiftapp.erp.masterdata.dto.ProductResponse;
import ch.swiftapp.erp.masterdata.event.ProductCreatedEvent;
import ch.swiftapp.erp.masterdata.event.ProductDeletedEvent;
import ch.swiftapp.erp.masterdata.event.ProductUpdatedEvent;
import ch.swiftapp.erp.masterdata.model.Product;
import ch.swiftapp.erp.masterdata.model.ProductTranslation;
import ch.swiftapp.erp.masterdata.repository.CategoryRepository;
import ch.swiftapp.erp.masterdata.repository.ProductRepository;
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
 * Service for managing finished products (Swiss luxury watches).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TranslationResolver translationResolver;

    /**
     * List all active, non-deleted products with pagination.
     */
    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAllByDeletedAtIsNull(pageable)
                .map(this::toResponse);
    }

    /**
     * List only active products.
     */
    public Page<ProductResponse> findAllActive(Pageable pageable) {
        return productRepository.findAllByDeletedAtIsNullAndActiveTrue(pageable)
                .map(this::toResponse);
    }

    /**
     * Find a product by ID.
     */
    public ProductResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    /**
     * Find a product by ID, returning Optional (for module API).
     */
    public Optional<ProductResponse> findByIdOptional(UUID id) {
        return productRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .map(this::toResponse);
    }

    /**
     * Find a product by SKU.
     */
    public Optional<ProductResponse> findBySku(String sku) {
        return productRepository.findBySkuIgnoreCaseAndDeletedAtIsNull(sku)
                .map(this::toResponse);
    }

    /**
     * Search products by name or SKU.
     */
    public Page<ProductResponse> search(String query, Pageable pageable) {
        return productRepository.searchByNameOrSku(query, pageable)
                .map(this::toResponse);
    }

    /**
     * Check if a product is active and not deleted.
     */
    public boolean isProductActive(UUID id) {
        return productRepository.isActiveAndNotDeleted(id);
    }

    /**
     * Create a new product.
     */
    @Transactional
    public ProductResponse create(ProductRequest request) {
        log.info("Creating product: sku={} name={}", request.sku(), request.name());

        if (productRepository.existsBySkuIgnoreCase(request.sku())) {
            throw new IllegalArgumentException("Product SKU already exists: " + request.sku());
        }

        var product = new Product();
        mapRequestToEntity(request, product);
        
        applyTranslations(request.nameTranslations(), request.descriptionTranslations(), product);
        
        product = productRepository.save(product);

        // TODO: Verify event contains correct payload
        eventPublisher.publishEvent(new ProductCreatedEvent(
                product.getId(), product.getSku(), product.getName()));

        log.info("Created product id={} sku={}", product.getId(), product.getSku());
        return toResponse(product);
    }

    /**
     * Update an existing product.
     */
    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        log.info("Updating product id={}", id);

        var product = findEntityById(id);

        // Check SKU uniqueness if changed
        if (!product.getSku().equalsIgnoreCase(request.sku())
                && productRepository.existsBySkuIgnoreCase(request.sku())) {
            throw new IllegalArgumentException("Product SKU already exists: " + request.sku());
        }

        mapRequestToEntity(request, product);
        
        product.getTranslations().clear();
        
        applyTranslations(request.nameTranslations(), request.descriptionTranslations(), product);
        
        product = productRepository.save(product);

        // TODO: Check that event has correct updated state
        eventPublisher.publishEvent(new ProductUpdatedEvent(
                product.getId(), product.getSku(), product.getName()));

        log.info("Updated product id={} sku={}", product.getId(), product.getSku());
        return toResponse(product);
    }

    /**
     * Soft-delete a product.
     */
    @Transactional
    public void delete(UUID id) {
        log.info("Soft-deleting product id={}", id);
        var product = findEntityById(id);
        
        product.setDeletedAt(Instant.now());
        product.setActive(false);
        
        productRepository.save(product);

        // TODO: Verify deletion event integrity
        eventPublisher.publishEvent(new ProductDeletedEvent(product.getId(), product.getSku()));
    }

    // ── Internal helpers ──────────────────────────────────────

    private Product findEntityById(UUID id) {
        return productRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    private void mapRequestToEntity(ProductRequest request, Product product) {
        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setUnitPrice(request.unitPrice());
        product.setListPrice(request.listPrice());
        product.setVatRate(request.vatRate());
        product.setActive(request.active() != null ? request.active() : true);

        if (request.categoryId() != null) {
            var category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Category not found: " + request.categoryId()));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }
    }

    private ProductResponse toResponse(Product entity) {
        var t = entity.getTranslations();
        return new ProductResponse(
                entity.getId(),
                entity.getSku(),
                translationResolver.resolve(t, ProductTranslation::getLocale, ProductTranslation::getName, entity.getName()),
                translationResolver.resolve(t, ProductTranslation::getLocale, ProductTranslation::getDescription, entity.getDescription()),
                entity.getCategory() != null ? entity.getCategory().getId() : null,
                entity.getCategory() != null ? entity.getCategory().getName() : null,
                entity.getUnitPrice(),
                entity.getListPrice(),
                entity.getVatRate(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                translationResolver.toMap(t, ProductTranslation::getLocale, ProductTranslation::getName),
                translationResolver.toMap(t, ProductTranslation::getLocale, ProductTranslation::getDescription)
        );
    }

    private void applyTranslations(Map<String, String> names, Map<String, String> descriptions, Product product) {
        if (names == null || names.isEmpty()) {
            return;
        }
        names.forEach((locale, name) -> {
            var translation = new ProductTranslation();
            translation.setLocale(locale);
            translation.setProduct(product);
            translation.setName(name);
            translation.setDescription(descriptions != null ? descriptions.get(locale) : null);
            product.getTranslations().add(translation);
        });
    }
}

