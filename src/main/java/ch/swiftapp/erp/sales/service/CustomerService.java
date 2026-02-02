package ch.swiftapp.erp.sales.service;

import ch.swiftapp.erp.sales.dto.CustomerRequest;
import ch.swiftapp.erp.sales.dto.CustomerResponse;
import ch.swiftapp.erp.sales.model.Customer;
import ch.swiftapp.erp.sales.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing customer records.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * List all active, non-deleted customers with pagination.
     */
    public Page<CustomerResponse> findAll(Pageable pageable) {
        return customerRepository.findAllByDeletedAtIsNull(pageable)
                .map(this::toResponse);
    }

    /**
     * List only active customers.
     */
    public Page<CustomerResponse> findAllActive(Pageable pageable) {
        return customerRepository.findAllByDeletedAtIsNullAndActiveTrue(pageable)
                .map(this::toResponse);
    }

    /**
     * Find a customer by ID.
     */
    public CustomerResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    /**
     * Find a customer by ID (internal).
     */
    Customer findEntityById(UUID id) {
        return customerRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
    }

    /**
     * Find by ID, returning Optional.
     */
    public Optional<CustomerResponse> findByIdOptional(UUID id) {
        return customerRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .map(this::toResponse);
    }

    /**
     * Search customers by name, number, or email.
     */
    public Page<CustomerResponse> search(String query, Pageable pageable) {
        return customerRepository.search(query, pageable)
                .map(this::toResponse);
    }

    /**
     * Create a new customer. Auto-generates customer number if not provided.
     */
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        log.info("Creating customer: company={} name={} {}",
                request.companyName(), request.firstName(), request.lastName());

        var customer = new Customer();
        mapRequestToEntity(request, customer);

        // Auto-generate customer number if not provided
        if (customer.getCustomerNumber() == null || customer.getCustomerNumber().isBlank()) {
            customer.setCustomerNumber(generateCustomerNumber());
        } else if (customerRepository.existsByCustomerNumberIgnoreCase(customer.getCustomerNumber())) {
            throw new IllegalArgumentException("Customer number already exists: " + customer.getCustomerNumber());
        }

        customer = customerRepository.save(customer);
        log.info("Created customer id={} number={}", customer.getId(), customer.getCustomerNumber());
        return toResponse(customer);
    }

    /**
     * Update an existing customer.
     */
    @Transactional
    public CustomerResponse update(UUID id, CustomerRequest request) {
        log.info("Updating customer id={}", id);
        var customer = findEntityById(id);

        // Check customer number uniqueness if changed
        if (request.customerNumber() != null
                && !request.customerNumber().isBlank()
                && !customer.getCustomerNumber().equalsIgnoreCase(request.customerNumber())
                && customerRepository.existsByCustomerNumberIgnoreCase(request.customerNumber())) {
            throw new IllegalArgumentException("Customer number already exists: " + request.customerNumber());
        }

        mapRequestToEntity(request, customer);
        customer = customerRepository.save(customer);

        log.info("Updated customer id={} number={}", customer.getId(), customer.getCustomerNumber());
        return toResponse(customer);
    }

    /**
     * Soft-delete a customer.
     */
    @Transactional
    public void delete(UUID id) {
        log.info("Soft-deleting customer id={}", id);
        var customer = findEntityById(id);
        customer.setDeletedAt(Instant.now());
        customer.setActive(false);
        customerRepository.save(customer);
    }

    // ── Internal helpers ──────────────────────────────────────

    private String generateCustomerNumber() {
        String prefix = "C-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")) + "-";
        long count = customerRepository.count() + 1;
        return prefix + String.format("%05d", count);
    }

    private void mapRequestToEntity(CustomerRequest request, Customer customer) {
        if (request.customerNumber() != null && !request.customerNumber().isBlank()) {
            customer.setCustomerNumber(request.customerNumber());
        }
        customer.setCompanyName(request.companyName());
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setStreet(request.street());
        customer.setCity(request.city());
        customer.setPostalCode(request.postalCode());
        customer.setCanton(request.canton());
        customer.setCountry(request.country() != null ? request.country() : "CH");
        customer.setVatNumber(request.vatNumber());
        customer.setPaymentTerms(request.paymentTerms() != null ? request.paymentTerms() : 30);
        customer.setCreditLimit(request.creditLimit() != null ? request.creditLimit() : BigDecimal.ZERO);
        customer.setNotes(request.notes());
        customer.setActive(request.active() != null ? request.active() : true);
    }

    private CustomerResponse toResponse(Customer entity) {
        return new CustomerResponse(
                entity.getId(),
                entity.getCustomerNumber(),
                entity.getCompanyName(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDisplayName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getStreet(),
                entity.getCity(),
                entity.getPostalCode(),
                entity.getCanton(),
                entity.getCountry(),
                entity.getVatNumber(),
                entity.getPaymentTerms(),
                entity.getCreditLimit(),
                entity.getNotes(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}



