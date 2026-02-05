package ch.swiftapp.erp.masterdata.service;

import ch.swiftapp.erp.masterdata.dto.BomLineRequest;
import ch.swiftapp.erp.masterdata.dto.BomLineResponse;
import ch.swiftapp.erp.masterdata.model.BillOfMaterial;
import ch.swiftapp.erp.masterdata.repository.BillOfMaterialRepository;
import ch.swiftapp.erp.masterdata.repository.MaterialRepository;
import ch.swiftapp.erp.masterdata.repository.ProductRepository;
import ch.swiftapp.erp.masterdata.repository.UnitOfMeasureRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing Bill of Materials — linking products to their constituent materials.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BillOfMaterialService {

    private final BillOfMaterialRepository bomRepository;
    private final ProductRepository productRepository;
    private final MaterialRepository materialRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    /**
     * Get all BOM lines for a product, ordered by position.
     */
    public List<BomLineResponse> findByProductId(UUID productId) {
        return bomRepository.findAllByProductIdWithDetails(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Add a BOM line to a product.
     */
    @Transactional
    public BomLineResponse addLine(UUID productId, BomLineRequest request) {
        log.info("Adding BOM line: product={} material={}", productId, request.materialId());

        if (bomRepository.existsByProductIdAndMaterialId(productId, request.materialId())) {
            throw new IllegalArgumentException(
                    "BOM line already exists for product=" + productId + " material=" + request.materialId());
        }

        var product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        var material = materialRepository.findById(request.materialId())
                .orElseThrow(() -> new EntityNotFoundException("Material not found: " + request.materialId()));

        var bom = new BillOfMaterial();
        bom.setProduct(product);
        bom.setMaterial(material);
        bom.setQuantity(request.quantity());
        bom.setPosition(request.position());
        bom.setNotes(request.notes());

        if (request.unitOfMeasureId() != null) {
            var uom = unitOfMeasureRepository.findById(request.unitOfMeasureId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Unit of measure not found: " + request.unitOfMeasureId()));
            bom.setUnitOfMeasure(uom);
        }

        bom = bomRepository.save(bom);
        log.info("Added BOM line id={} product={} material={}", bom.getId(), productId, request.materialId());
        return toResponse(bom);
    }

    /**
     * Update an existing BOM line.
     */
    @Transactional
    public BomLineResponse updateLine(UUID bomLineId, BomLineRequest request) {
        log.info("Updating BOM line id={}", bomLineId);

        var bom = bomRepository.findById(bomLineId)
                .orElseThrow(() -> new EntityNotFoundException("BOM line not found: " + bomLineId));

        var material = materialRepository.findById(request.materialId())
                .orElseThrow(() -> new EntityNotFoundException("Material not found: " + request.materialId()));

        bom.setMaterial(material);
        bom.setQuantity(request.quantity());
        bom.setPosition(request.position());
        bom.setNotes(request.notes());

        if (request.unitOfMeasureId() != null) {
            var uom = unitOfMeasureRepository.findById(request.unitOfMeasureId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Unit of measure not found: " + request.unitOfMeasureId()));
            bom.setUnitOfMeasure(uom);
        } else {
            bom.setUnitOfMeasure(null);
        }

        bom = bomRepository.save(bom);
        log.info("Updated BOM line id={}", bom.getId());
        return toResponse(bom);
    }

    /**
     * Remove a BOM line.
     */
    @Transactional
    public void deleteLine(UUID bomLineId) {
        log.info("Deleting BOM line id={}", bomLineId);
        if (!bomRepository.existsById(bomLineId)) {
            throw new EntityNotFoundException("BOM line not found: " + bomLineId);
        }
        bomRepository.deleteById(bomLineId);
    }

    // ── Internal helpers ──────────────────────────────────────

    private BomLineResponse toResponse(BillOfMaterial entity) {
        return new BomLineResponse(
                entity.getId(),
                entity.getProduct().getId(),
                entity.getMaterial().getId(),
                entity.getMaterial().getSku(),
                entity.getMaterial().getName(),
                entity.getQuantity(),
                entity.getUnitOfMeasure() != null ? entity.getUnitOfMeasure().getId() : null,
                entity.getUnitOfMeasure() != null ? entity.getUnitOfMeasure().getCode() : null,
                entity.getPosition(),
                entity.getNotes()
        );
    }
}

