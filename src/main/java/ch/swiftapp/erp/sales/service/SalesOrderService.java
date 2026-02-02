package ch.swiftapp.erp.sales.service;

import ch.swiftapp.erp.masterdata.MasterdataModuleApi;
import ch.swiftapp.erp.masterdata.dto.ProductResponse;
import ch.swiftapp.erp.sales.MonthlyRevenueSummary;
import ch.swiftapp.erp.sales.SalesOrderSummary;
import ch.swiftapp.erp.sales.dto.*;
import ch.swiftapp.erp.sales.event.SalesOrderCancelledEvent;
import ch.swiftapp.erp.sales.event.SalesOrderConfirmedEvent;
import ch.swiftapp.erp.sales.event.SalesOrderCreatedEvent;
import ch.swiftapp.erp.sales.model.*;
import ch.swiftapp.erp.sales.repository.SalesOrderRepository;
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
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Service for managing sales orders — the core transactional entity of the sales module.
 *
 * <p>Handles CRUD operations, order lifecycle transitions, line-item management,
 * and automatic recalculation of subtotal / VAT / total.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerService customerService;
    private final MasterdataModuleApi masterdataApi;
    private final ApplicationEventPublisher eventPublisher;

    // ── Queries ───────────────────────────────────────────────

    /**
     * List all non-deleted orders with pagination.
     */
    public Page<SalesOrderResponse> findAll(Pageable pageable) {
        return salesOrderRepository.findAllByDeletedAtIsNull(pageable)
                .map(this::toResponse);
    }

    /**
     * List orders filtered by status.
     */
    public Page<SalesOrderResponse> findByStatus(SalesOrderStatus status, Pageable pageable) {
        return salesOrderRepository.findAllByDeletedAtIsNullAndStatus(status, pageable)
                .map(this::toResponse);
    }

    /**
     * List orders for a specific customer.
     */
    public Page<SalesOrderResponse> findByCustomer(UUID customerId, Pageable pageable) {
        return salesOrderRepository.findAllByCustomerIdAndDeletedAtIsNull(customerId, pageable)
                .map(this::toResponse);
    }

    /**
     * Find a single order by ID.
     */
    public SalesOrderResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    /**
     * Search orders by order number or customer name.
     */
    public Page<SalesOrderResponse> search(String query, Pageable pageable) {
        return salesOrderRepository.search(query, pageable)
                .map(this::toResponse);
    }

    /**
     * Count open (non-completed, non-cancelled) orders.
     */
    public long countOpenOrders() {
        return salesOrderRepository.countOpenOrders();
    }

    /**
     * Get monthly revenue for completed orders.
     */
    public BigDecimal getMonthlyRevenue(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return salesOrderRepository.sumRevenueByDateRange(startDate, endDate);
    }

    /**
     * Get total amount for a specific order.
     */
    public BigDecimal getOrderTotal(UUID orderId) {
        return findEntityById(orderId).getTotalAmount();
    }

    /**
     * Get the most recent sales orders as lightweight summaries.
     *
     * @param limit maximum number of orders to return
     */
    public List<SalesOrderSummary> getRecentOrders(int limit) {
        var pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return salesOrderRepository.findAllByDeletedAtIsNull(pageable)
                .stream()
                .map(o -> new SalesOrderSummary(
                        o.getId(),
                        o.getOrderNumber(),
                        o.getCustomer().getDisplayName(),
                        o.getStatus().name(),
                        o.getTotalAmount(),
                        o.getCurrency(),
                        o.getOrderDate()))
                .toList();
    }

    /**
     * Count orders grouped by status for a dashboard chart.
     *
     * @return map of status name → count
     */
    public Map<String, Long> getStatusBreakdown() {
        return salesOrderRepository.countGroupByStatus()
                .stream()
                .collect(Collectors.toMap(
                        row -> ((SalesOrderStatus) row[0]).name(),
                        row -> (Long) row[1]
                ));
    }

    /**
     * Get monthly revenue summaries for the past 6 months (including current month).
     */
    public List<MonthlyRevenueSummary> getLast6MonthsRevenue() {
        LocalDate now = LocalDate.now();
        return IntStream.rangeClosed(0, 5)
                .mapToObj(i -> now.minusMonths(5 - i))
                .map(d -> {
                    BigDecimal rev = getMonthlyRevenue(d.getYear(), d.getMonthValue());
                    String label = d.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            + " " + d.getYear();
                    return new MonthlyRevenueSummary(d.getYear(), d.getMonthValue(), label, rev);
                })
                .toList();
    }

    // ── Commands ──────────────────────────────────────────────

    /**
     * Create a new sales order (DRAFT status).
     */
    @Transactional
    public SalesOrderResponse create(SalesOrderRequest request) {
        log.info("Creating sales order for customer={}", request.customerId());

        var customer = customerService.findEntityById(request.customerId());

        var order = new SalesOrder();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setStatus(SalesOrderStatus.DRAFT);
        order.setOrderDate(request.orderDate() != null ? request.orderDate() : LocalDate.now());
        order.setDeliveryDate(request.deliveryDate());
        order.setNotes(request.notes());

        // Shipping address — default from customer if not provided
        order.setShippingStreet(request.shippingStreet() != null ? request.shippingStreet() : customer.getStreet());
        order.setShippingCity(request.shippingCity() != null ? request.shippingCity() : customer.getCity());
        order.setShippingPostalCode(request.shippingPostalCode() != null ? request.shippingPostalCode() : customer.getPostalCode());
        order.setShippingCanton(request.shippingCanton() != null ? request.shippingCanton() : customer.getCanton());
        order.setShippingCountry(request.shippingCountry() != null ? request.shippingCountry() : customer.getCountry());

        // Process order lines
        if (request.lines() != null && !request.lines().isEmpty()) {
            int pos = 1;
            for (var lineReq : request.lines()) {
                var line = buildOrderLine(lineReq, pos++);
                order.addLine(line);
            }
        }

        order.recalculateTotals();
        order = salesOrderRepository.save(order);

        eventPublisher.publishEvent(new SalesOrderCreatedEvent(
                order.getId(), order.getOrderNumber(), customer.getId()));

        log.info("Created sales order id={} number={} total={}",
                order.getId(), order.getOrderNumber(), order.getTotalAmount());
        return toResponse(order);
    }

    /**
     * Update a DRAFT sales order.
     */
    @Transactional
    public SalesOrderResponse update(UUID id, SalesOrderRequest request) {
        log.info("Updating sales order id={}", id);
        var order = findEntityById(id);

        if (order.getStatus() != SalesOrderStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT orders can be edited. Current status: " + order.getStatus());
        }

        // Update customer if changed
        if (!order.getCustomer().getId().equals(request.customerId())) {
            var customer = customerService.findEntityById(request.customerId());
            order.setCustomer(customer);
        }

        order.setDeliveryDate(request.deliveryDate());
        order.setNotes(request.notes());
        order.setShippingStreet(request.shippingStreet());
        order.setShippingCity(request.shippingCity());
        order.setShippingPostalCode(request.shippingPostalCode());
        order.setShippingCanton(request.shippingCanton());
        order.setShippingCountry(request.shippingCountry());

        // Replace lines
        order.getLines().clear();
        if (request.lines() != null && !request.lines().isEmpty()) {
            int pos = 1;
            for (var lineReq : request.lines()) {
                var line = buildOrderLine(lineReq, pos++);
                order.addLine(line);
            }
        }

        order.recalculateTotals();
        order = salesOrderRepository.save(order);

        log.info("Updated sales order id={} number={} total={}",
                order.getId(), order.getOrderNumber(), order.getTotalAmount());
        return toResponse(order);
    }

    /**
     * Confirm a DRAFT order → CONFIRMED.
     */
    @Transactional
    public SalesOrderResponse confirm(UUID id) {
        log.info("Confirming sales order id={}", id);
        var order = findEntityById(id);

        if (order.getStatus() != SalesOrderStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT orders can be confirmed. Current status: " + order.getStatus());
        }
        if (order.getLines().isEmpty()) {
            throw new IllegalStateException("Cannot confirm an order with no line items");
        }

        order.setStatus(SalesOrderStatus.CONFIRMED);
        order = salesOrderRepository.save(order);

        eventPublisher.publishEvent(new SalesOrderConfirmedEvent(
                order.getId(), order.getOrderNumber(), order.getTotalAmount()));

        log.info("Confirmed sales order id={} number={}", order.getId(), order.getOrderNumber());
        return toResponse(order);
    }

    /**
     * Advance order status: CONFIRMED → PROCESSING → SHIPPED → DELIVERED → COMPLETED.
     */
    @Transactional
    public SalesOrderResponse advanceStatus(UUID id) {
        var order = findEntityById(id);
        var next = switch (order.getStatus()) {
            case CONFIRMED -> SalesOrderStatus.PROCESSING;
            case PROCESSING -> SalesOrderStatus.SHIPPED;
            case SHIPPED -> SalesOrderStatus.DELIVERED;
            case DELIVERED -> SalesOrderStatus.COMPLETED;
            default -> throw new IllegalStateException(
                    "Cannot advance order from status: " + order.getStatus());
        };

        log.info("Advancing sales order id={} from {} to {}", id, order.getStatus(), next);
        order.setStatus(next);
        order = salesOrderRepository.save(order);
        return toResponse(order);
    }

    /**
     * Cancel an order (allowed for DRAFT and CONFIRMED orders).
     */
    @Transactional
    public SalesOrderResponse cancel(UUID id, String reason) {
        log.info("Cancelling sales order id={}", id);
        var order = findEntityById(id);

        if (order.getStatus() == SalesOrderStatus.COMPLETED || order.getStatus() == SalesOrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(SalesOrderStatus.CANCELLED);
        order = salesOrderRepository.save(order);

        eventPublisher.publishEvent(new SalesOrderCancelledEvent(
                order.getId(), order.getOrderNumber(), reason));

        log.info("Cancelled sales order id={} number={}", order.getId(), order.getOrderNumber());
        return toResponse(order);
    }

    /**
     * Soft-delete a DRAFT or CANCELLED order.
     */
    @Transactional
    public void delete(UUID id) {
        log.info("Soft-deleting sales order id={}", id);
        var order = findEntityById(id);

        if (order.getStatus() != SalesOrderStatus.DRAFT && order.getStatus() != SalesOrderStatus.CANCELLED) {
            throw new IllegalStateException("Only DRAFT or CANCELLED orders can be deleted. Current status: " + order.getStatus());
        }

        order.setDeletedAt(Instant.now());
        salesOrderRepository.save(order);
    }

    // ── Internal helpers ──────────────────────────────────────

    SalesOrder findEntityById(UUID id) {
        return salesOrderRepository.findById(id)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Sales order not found: " + id));
    }

    private SalesOrderLine buildOrderLine(SalesOrderLineRequest req, int position) {
        var line = new SalesOrderLine();
        line.setProductId(req.productId());
        line.setDescription(req.description());
        line.setQuantity(req.quantity());
        line.setUnitPrice(req.unitPrice());
        line.setDiscountPct(req.discountPct() != null ? req.discountPct() : BigDecimal.ZERO);
        line.setVatRate(req.vatRate());
        line.setPosition(req.position() != null ? req.position() : position);
        line.calculateLineTotal();
        return line;
    }

    private String generateOrderNumber() {
        String prefix = "SO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")) + "-";
        return salesOrderRepository.findMaxOrderNumberByPrefix(prefix + "%")
                .map(max -> {
                    String seqPart = max.substring(prefix.length());
                    int next = Integer.parseInt(seqPart) + 1;
                    return prefix + String.format("%05d", next);
                })
                .orElse(prefix + "00001");
    }

    private SalesOrderResponse toResponse(SalesOrder entity) {
        var lineResponses = entity.getLines().stream()
                .map(this::toLineResponse)
                .toList();

        return new SalesOrderResponse(
                entity.getId(),
                entity.getOrderNumber(),
                entity.getCustomer().getId(),
                entity.getCustomer().getDisplayName(),
                entity.getStatus(),
                entity.getOrderDate(),
                entity.getDeliveryDate(),
                entity.getSubtotal(),
                entity.getVatAmount(),
                entity.getTotalAmount(),
                entity.getCurrency(),
                entity.getNotes(),
                entity.getShippingStreet(),
                entity.getShippingCity(),
                entity.getShippingPostalCode(),
                entity.getShippingCanton(),
                entity.getShippingCountry(),
                lineResponses,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private SalesOrderLineResponse toLineResponse(SalesOrderLine line) {
        // Resolve product name from masterdata module API
        String productName = masterdataApi.findProductById(line.getProductId())
                .map(ProductResponse::name)
                .orElse("Unknown Product");

        return new SalesOrderLineResponse(
                line.getId(),
                line.getProductId(),
                productName,
                line.getDescription(),
                line.getQuantity(),
                line.getUnitPrice(),
                line.getDiscountPct(),
                line.getVatRate(),
                line.getLineTotal(),
                line.getVatAmount(),
                line.getPosition()
        );
    }
}

