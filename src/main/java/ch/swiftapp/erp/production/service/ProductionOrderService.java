package ch.swiftapp.erp.production.service;

import ch.swiftapp.erp.masterdata.MasterdataModuleApi;
import ch.swiftapp.erp.masterdata.dto.MaterialResponse;
import ch.swiftapp.erp.masterdata.dto.ProductResponse;
import ch.swiftapp.erp.production.ProductionOrderSummary;
import ch.swiftapp.erp.production.dto.*;
import ch.swiftapp.erp.production.event.*;
import ch.swiftapp.erp.production.model.*;
import ch.swiftapp.erp.production.repository.ProductionOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor @Slf4j
@Transactional(readOnly = true)
public class ProductionOrderService {

    private final ProductionOrderRepository orderRepo;
    private final WorkCenterService workCenterService;
    private final MasterdataModuleApi masterdataApi;
    private final ApplicationEventPublisher eventPublisher;

    public Page<ProductionOrderResponse> findAll(Pageable p) { return orderRepo.findAllByDeletedAtIsNull(p).map(this::toResponse); }
    public Page<ProductionOrderResponse> findByStatus(ProductionOrderStatus s, Pageable p) { return orderRepo.findAllByDeletedAtIsNullAndStatus(s, p).map(this::toResponse); }
    public ProductionOrderResponse findById(UUID id) { return toResponse(findEntity(id)); }
    public Page<ProductionOrderResponse> search(String q, Pageable p) { return orderRepo.search(q, p).map(this::toResponse); }
    public long countOpenOrders() { return orderRepo.countOpenOrders(); }
    public BigDecimal getPlannedOutputForProduct(UUID productId) { return orderRepo.sumPlannedQuantityByProductId(productId); }
    public BigDecimal getOrderActualCost(UUID id) { return findEntity(id).getActualCost(); }

    /**
     * Get lightweight summaries of the most recent production orders.
     *
     * @param limit maximum number of orders to return
     */
    public List<ProductionOrderSummary> getRecentOrders(int limit) {
        var pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderRepo.findAllByDeletedAtIsNull(pageable)
                .stream()
                .map(o -> {
                    String productName = masterdataApi.findProductById(o.getProductId())
                            .map(ProductResponse::name).orElse("Unknown");
                    return new ProductionOrderSummary(
                            o.getId(), o.getOrderNumber(), productName,
                            o.getStatus().name(), o.getPlannedQuantity(), o.getPlannedEndDate());
                })
                .toList();
    }

    @Transactional
    public ProductionOrderResponse create(ProductionOrderRequest r) {
        log.info("Creating production order for product={}", r.productId());
        var order = new ProductionOrder();
        order.setOrderNumber(generateOrderNumber());
        
        order.setProductId(r.productId());
        order.setPlannedQuantity(r.plannedQuantity());
        order.setPlannedStartDate(r.plannedStartDate());
        order.setPlannedEndDate(r.plannedEndDate());
        order.setPriority(r.priority() != null ? r.priority() : 0);
        order.setNotes(r.notes());
        
        if (r.workCenterId() != null) {
            order.setWorkCenter(workCenterService.findEntity(r.workCenterId()));
        }
        
        if (r.lines() != null) {
            int pos = 1;
            for (var lr : r.lines()) {
                order.addLine(buildLine(lr, pos++));
            }
        }
        
        order.recalculateEstimatedCost();
        
        order = orderRepo.save(order);
        
        eventPublisher.publishEvent(new ProductionOrderCreatedEvent(order.getId(), order.getOrderNumber(), order.getProductId()));
        
        return toResponse(order);
    }

    @Transactional
    public ProductionOrderResponse update(UUID id, ProductionOrderRequest r) {
        var order = findEntity(id);
        if (order.getStatus() != ProductionOrderStatus.PLANNED) throw new IllegalStateException("Only PLANNED orders can be edited");
        order.setProductId(r.productId());
        order.setPlannedQuantity(r.plannedQuantity());
        order.setPlannedStartDate(r.plannedStartDate());
        order.setPlannedEndDate(r.plannedEndDate());
        order.setPriority(r.priority() != null ? r.priority() : 0);
        order.setNotes(r.notes());
        order.setWorkCenter(r.workCenterId() != null ? workCenterService.findEntity(r.workCenterId()) : null);
        order.getLines().clear();
        if (r.lines() != null) { int pos = 1; for (var lr : r.lines()) order.addLine(buildLine(lr, pos++)); }
        order.recalculateEstimatedCost();
        return toResponse(orderRepo.save(order));
    }

    @Transactional
    public ProductionOrderResponse release(UUID id) {
        var o = findEntity(id);
        
        if (o.getStatus() != ProductionOrderStatus.PLANNED)
            throw new IllegalStateException("Only PLANNED orders can be released");
        
        o.setStatus(ProductionOrderStatus.RELEASED);
        
        o = orderRepo.save(o);
        
        eventPublisher.publishEvent(new ProductionOrderReleasedEvent(o.getId(), o.getOrderNumber()));
        
        return toResponse(o);
    }

    @Transactional
    public ProductionOrderResponse start(UUID id) {
        var o = findEntity(id);
        
        if (o.getStatus() != ProductionOrderStatus.RELEASED)
            throw new IllegalStateException("Only RELEASED orders can be started");
        
        o.setStatus(ProductionOrderStatus.IN_PROGRESS);
        o.setActualStartDate(LocalDate.now());
        
        return toResponse(orderRepo.save(o));
    }

    @Transactional
    public ProductionOrderResponse complete(UUID id, BigDecimal completedQty, BigDecimal scrapQty) {
        var o = findEntity(id);
        if (o.getStatus() != ProductionOrderStatus.IN_PROGRESS) throw new IllegalStateException("Only IN_PROGRESS orders can be completed");
        o.setStatus(ProductionOrderStatus.COMPLETED);
        o.setCompletedQuantity(completedQty != null ? completedQty : o.getPlannedQuantity());
        o.setScrapQuantity(scrapQty != null ? scrapQty : BigDecimal.ZERO);
        o.setActualEndDate(LocalDate.now());
        o = orderRepo.save(o);
        eventPublisher.publishEvent(new ProductionOrderCompletedEvent(o.getId(), o.getOrderNumber(), o.getProductId(), o.getCompletedQuantity()));
        return toResponse(o);
    }

    @Transactional
    public ProductionOrderResponse hold(UUID id) {
        var o = findEntity(id);
        if (o.getStatus() != ProductionOrderStatus.IN_PROGRESS) throw new IllegalStateException("Only IN_PROGRESS orders can be put on hold");
        o.setStatus(ProductionOrderStatus.ON_HOLD);
        return toResponse(orderRepo.save(o));
    }

    @Transactional
    public ProductionOrderResponse resume(UUID id) {
        var o = findEntity(id);
        if (o.getStatus() != ProductionOrderStatus.ON_HOLD) throw new IllegalStateException("Only ON_HOLD orders can be resumed");
        o.setStatus(ProductionOrderStatus.IN_PROGRESS);
        return toResponse(orderRepo.save(o));
    }

    @Transactional
    public ProductionOrderResponse cancel(UUID id, String reason) {
        var o = findEntity(id);
        if (o.getStatus() == ProductionOrderStatus.COMPLETED || o.getStatus() == ProductionOrderStatus.CANCELLED)
            throw new IllegalStateException("Cannot cancel: " + o.getStatus());
        o.setStatus(ProductionOrderStatus.CANCELLED);
        o = orderRepo.save(o);
        eventPublisher.publishEvent(new ProductionOrderCancelledEvent(o.getId(), o.getOrderNumber(), reason));
        return toResponse(o);
    }

    @Transactional
    public void delete(UUID id) {
        var o = findEntity(id);
        if (o.getStatus() != ProductionOrderStatus.PLANNED && o.getStatus() != ProductionOrderStatus.CANCELLED)
            throw new IllegalStateException("Only PLANNED or CANCELLED orders can be deleted");
        o.setDeletedAt(Instant.now());
        orderRepo.save(o);
    }

    // ── Helpers ───────────────────────────────────────────
    ProductionOrder findEntity(UUID id) {
        return orderRepo.findById(id).filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Production order not found: " + id));
    }

    private ProductionOrderLine buildLine(ProductionOrderLineRequest r, int pos) {
        var l = new ProductionOrderLine();
        l.setMaterialId(r.materialId());
        l.setDescription(r.description());
        l.setPlannedQuantity(r.plannedQuantity());
        l.setUnitPrice(r.unitPrice() != null ? r.unitPrice() : BigDecimal.ZERO);
        l.setPosition(r.position() != null ? r.position() : pos);
        l.calculateLineCost();
        return l;
    }

    private String generateOrderNumber() {
        String prefix = "MO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")) + "-";
        return orderRepo.findMaxOrderNumberByPrefix(prefix + "%")
                .map(max -> prefix + String.format("%05d", Integer.parseInt(max.substring(prefix.length())) + 1))
                .orElse(prefix + "00001");
    }

    private ProductionOrderResponse toResponse(ProductionOrder e) {
        String productName = masterdataApi.findProductById(e.getProductId()).map(ProductResponse::name).orElse("Unknown");
        String wcName = e.getWorkCenter() != null ? e.getWorkCenter().getName() : null;
        UUID wcId = e.getWorkCenter() != null ? e.getWorkCenter().getId() : null;
        var lines = e.getLines().stream().map(this::toLineResponse).toList();
        return new ProductionOrderResponse(e.getId(), e.getOrderNumber(), e.getProductId(), productName,
                wcId, wcName, e.getStatus(),
                e.getPlannedQuantity(), e.getCompletedQuantity(), e.getScrapQuantity(),
                e.getPlannedStartDate(), e.getPlannedEndDate(), e.getActualStartDate(), e.getActualEndDate(),
                e.getEstimatedCost(), e.getActualCost(), e.getCurrency(), e.getPriority(), e.getNotes(),
                lines, e.getCreatedAt(), e.getUpdatedAt());
    }

    private ProductionOrderLineResponse toLineResponse(ProductionOrderLine l) {
        String matName = masterdataApi.findMaterialById(l.getMaterialId()).map(MaterialResponse::name).orElse("Unknown");
        return new ProductionOrderLineResponse(l.getId(), l.getMaterialId(), matName,
                l.getDescription(), l.getPlannedQuantity(), l.getActualQuantity(), l.getUnitPrice(), l.getLineCost(), l.getPosition());
    }
}

