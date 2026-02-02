package ch.swiftapp.erp.purchasing.service;

import ch.swiftapp.erp.masterdata.MasterdataModuleApi;
import ch.swiftapp.erp.masterdata.dto.MaterialResponse;
import ch.swiftapp.erp.purchasing.dto.*;
import ch.swiftapp.erp.purchasing.event.*;
import ch.swiftapp.erp.purchasing.model.*;
import ch.swiftapp.erp.purchasing.repository.PurchaseOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PurchaseOrderService {

    private final PurchaseOrderRepository orderRepository;
    private final SupplierService supplierService;
    private final MasterdataModuleApi masterdataApi;
    private final ApplicationEventPublisher eventPublisher;

    // Queries

    public Page<PurchaseOrderResponse> findAll(Pageable pageable) {
        return orderRepository.findAllByDeletedAtIsNull(pageable).map(this::toResponse);
    }

    public Page<PurchaseOrderResponse> findByStatus(PurchaseOrderStatus status, Pageable pageable) {
        return orderRepository.findAllByDeletedAtIsNullAndStatus(status, pageable).map(this::toResponse);
    }

    public Page<PurchaseOrderResponse> findBySupplier(UUID supplierId, Pageable pageable) {
        return orderRepository.findAllBySupplierIdAndDeletedAtIsNull(supplierId, pageable).map(this::toResponse);
    }

    public PurchaseOrderResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    public Page<PurchaseOrderResponse> search(String query, Pageable pageable) {
        return orderRepository.search(query, pageable).map(this::toResponse);
    }

    public long countOpenOrders() { return orderRepository.countOpenOrders(); }

    public BigDecimal getMonthlySpend(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        return orderRepository.sumSpendByDateRange(start, start.withDayOfMonth(start.lengthOfMonth()));
    }

    public BigDecimal getOrderTotal(UUID id) { return findEntityById(id).getTotalAmount(); }

    // Commands

    @Transactional
    public PurchaseOrderResponse create(PurchaseOrderRequest request) {
        log.info("Creating purchase order for supplier={}", request.supplierId());
        var supplier = supplierService.findEntityById(request.supplierId());
        // supplier loaded for this PO

        var order = new PurchaseOrder();
        order.setOrderNumber(generateOrderNumber());
        order.setSupplier(supplier);
        order.setStatus(PurchaseOrderStatus.DRAFT);
        order.setOrderDate(request.orderDate() != null ? request.orderDate() : LocalDate.now());
        order.setExpectedDeliveryDate(request.expectedDeliveryDate());
        order.setNotes(request.notes());

        if (request.lines() != null) {
            int pos = 1;
            for (var lr : request.lines()) {
                var line = buildLine(lr, pos++);
                order.addLine(line);
                // line added at pos={} material={}
            }
        }
        
        order.recalculateTotals();
        // quick check total={}
        
        order = orderRepository.save(order);
        // persisted PO id={}

        // TODO re-check created event fields if supplier projection changes
        eventPublisher.publishEvent(new PurchaseOrderCreatedEvent(
                order.getId(), order.getOrderNumber(), supplier.getId()));
        // event out: PurchaseOrderCreatedEvent
        
        log.info("Created purchase order id={} number={}", order.getId(), order.getOrderNumber());
        return toResponse(order);
    }

    @Transactional
    public PurchaseOrderResponse update(UUID id, PurchaseOrderRequest request) {
        var order = findEntityById(id);
        if (order.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT orders can be edited");
        }
        if (!order.getSupplier().getId().equals(request.supplierId())) {
            order.setSupplier(supplierService.findEntityById(request.supplierId()));
        }
        order.setExpectedDeliveryDate(request.expectedDeliveryDate());
        order.setNotes(request.notes());
        order.getLines().clear();
        if (request.lines() != null) {
            int pos = 1;
            for (var lr : request.lines()) { order.addLine(buildLine(lr, pos++)); }
        }
        order.recalculateTotals();
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public PurchaseOrderResponse submit(UUID id) {
        var order = findEntityById(id);
        // submit path, current status={}
        
        if (order.getStatus() != PurchaseOrderStatus.DRAFT) 
            throw new IllegalStateException("Only DRAFT orders can be submitted");
        // status check ok
        
        if (order.getLines().isEmpty()) 
            throw new IllegalStateException("Cannot submit an order with no lines");
        // line list not empty
        
        order.setStatus(PurchaseOrderStatus.SUBMITTED);
        
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public PurchaseOrderResponse confirm(UUID id) {
        var order = findEntityById(id);
        // confirm flow status={}
        
        if (order.getStatus() != PurchaseOrderStatus.SUBMITTED) 
            throw new IllegalStateException("Only SUBMITTED orders can be confirmed");
        // status transition valid
        
        order.setStatus(PurchaseOrderStatus.CONFIRMED);
        
        order = orderRepository.save(order);
        // saved confirmation
        
        // TODO check confirmed event amount after pricing rule changes
        eventPublisher.publishEvent(new PurchaseOrderConfirmedEvent(order.getId(), order.getOrderNumber(), order.getTotalAmount()));
        // event out: PurchaseOrderConfirmedEvent
        
        return toResponse(order);
    }

    @Transactional
    public PurchaseOrderResponse receive(UUID id) {
        var order = findEntityById(id);
        // receive flow status={}
        
        if (order.getStatus() != PurchaseOrderStatus.CONFIRMED && order.getStatus() != PurchaseOrderStatus.PARTIALLY_RECEIVED) {
            throw new IllegalStateException("Order must be CONFIRMED or PARTIALLY_RECEIVED to receive goods");
        }
        // can receive now
        
        order.setStatus(PurchaseOrderStatus.RECEIVED);
        order.setActualDeliveryDate(LocalDate.now());
        
        order = orderRepository.save(order);
        // saved receive update
        
        // TODO inventory sync check: make sure receiving already booked stock
        eventPublisher.publishEvent(new PurchaseOrderReceivedEvent(order.getId(), order.getOrderNumber()));
        // event out: PurchaseOrderReceivedEvent
        
        return toResponse(order);
    }

    @Transactional
    public PurchaseOrderResponse complete(UUID id) {
        var order = findEntityById(id);
        if (order.getStatus() != PurchaseOrderStatus.RECEIVED) throw new IllegalStateException("Only RECEIVED orders can be completed");
        order.setStatus(PurchaseOrderStatus.COMPLETED);
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public PurchaseOrderResponse cancel(UUID id, String reason) {
        var order = findEntityById(id);
        if (order.getStatus() == PurchaseOrderStatus.COMPLETED || order.getStatus() == PurchaseOrderStatus.CANCELLED)
            throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
        order.setStatus(PurchaseOrderStatus.CANCELLED);
        order = orderRepository.save(order);
        eventPublisher.publishEvent(new PurchaseOrderCancelledEvent(order.getId(), order.getOrderNumber(), reason));
        return toResponse(order);
    }

    @Transactional
    public void delete(UUID id) {
        var order = findEntityById(id);
        if (order.getStatus() != PurchaseOrderStatus.DRAFT && order.getStatus() != PurchaseOrderStatus.CANCELLED)
            throw new IllegalStateException("Only DRAFT or CANCELLED orders can be deleted");
        order.setDeletedAt(Instant.now());
        orderRepository.save(order);
    }

    // Helpers

    PurchaseOrder findEntityById(UUID id) {
        return orderRepository.findById(id).filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found: " + id));
    }

    private PurchaseOrderLine buildLine(PurchaseOrderLineRequest r, int pos) {
        var line = new PurchaseOrderLine();
        line.setMaterialId(r.materialId());
        line.setDescription(r.description());
        line.setQuantity(r.quantity());
        line.setUnitPrice(r.unitPrice());
        line.setDiscountPct(r.discountPct() != null ? r.discountPct() : BigDecimal.ZERO);
        line.setVatRate(r.vatRate());
        line.setPosition(r.position() != null ? r.position() : pos);
        line.calculateLineTotal();
        return line;
    }

    private String generateOrderNumber() {
        String prefix = "PO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")) + "-";
        return orderRepository.findMaxOrderNumberByPrefix(prefix + "%")
                .map(max -> prefix + String.format("%05d", Integer.parseInt(max.substring(prefix.length())) + 1))
                .orElse(prefix + "00001");
    }

    private PurchaseOrderResponse toResponse(PurchaseOrder e) {
        var lines = e.getLines().stream().map(this::toLineResponse).toList();
        return new PurchaseOrderResponse(e.getId(), e.getOrderNumber(),
                e.getSupplier().getId(), e.getSupplier().getDisplayName(),
                e.getStatus(), e.getOrderDate(), e.getExpectedDeliveryDate(), e.getActualDeliveryDate(),
                e.getSubtotal(), e.getVatAmount(), e.getTotalAmount(), e.getCurrency(), e.getNotes(),
                lines, e.getCreatedAt(), e.getUpdatedAt());
    }

    private PurchaseOrderLineResponse toLineResponse(PurchaseOrderLine l) {
        String materialName = masterdataApi.findMaterialById(l.getMaterialId())
                .map(MaterialResponse::name).orElse("Unknown Material");
        return new PurchaseOrderLineResponse(l.getId(), l.getMaterialId(), materialName,
                l.getDescription(), l.getQuantity(), l.getUnitPrice(), l.getDiscountPct(),
                l.getVatRate(), l.getLineTotal(), l.getVatAmount(), l.getPosition());
    }
}

