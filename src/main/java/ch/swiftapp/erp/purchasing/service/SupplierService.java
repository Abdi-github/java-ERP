package ch.swiftapp.erp.purchasing.service;

import ch.swiftapp.erp.purchasing.dto.SupplierRequest;
import ch.swiftapp.erp.purchasing.dto.SupplierResponse;
import ch.swiftapp.erp.purchasing.model.Supplier;
import ch.swiftapp.erp.purchasing.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public Page<SupplierResponse> findAll(Pageable pageable) {
        return supplierRepository.findAllByDeletedAtIsNull(pageable).map(this::toResponse);
    }

    public Page<SupplierResponse> findAllActive(Pageable pageable) {
        return supplierRepository.findAllByDeletedAtIsNullAndActiveTrue(pageable).map(this::toResponse);
    }

    public SupplierResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    Supplier findEntityById(UUID id) {
        return supplierRepository.findById(id)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found: " + id));
    }

    public Optional<SupplierResponse> findByIdOptional(UUID id) {
        return supplierRepository.findById(id).filter(s -> s.getDeletedAt() == null).map(this::toResponse);
    }

    public Page<SupplierResponse> search(String query, Pageable pageable) {
        return supplierRepository.search(query, pageable).map(this::toResponse);
    }

    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        log.info("Creating supplier: company={}", request.companyName());
        var supplier = new Supplier();
        mapRequestToEntity(request, supplier);
        
        if (supplier.getSupplierNumber() == null || supplier.getSupplierNumber().isBlank()) {
            supplier.setSupplierNumber(generateSupplierNumber());
        } else if (supplierRepository.existsBySupplierNumberIgnoreCase(supplier.getSupplierNumber())) {
            throw new IllegalArgumentException("Supplier number already exists: " + supplier.getSupplierNumber());
        }
        
        supplier = supplierRepository.save(supplier);
        
        log.info("Created supplier id={} number={}", supplier.getId(), supplier.getSupplierNumber());
        return toResponse(supplier);
    }

    @Transactional
    public SupplierResponse update(UUID id, SupplierRequest request) {
        log.info("Updating supplier id={}", id);
        var supplier = findEntityById(id);
        
        if (request.supplierNumber() != null && !request.supplierNumber().isBlank()
                && !supplier.getSupplierNumber().equalsIgnoreCase(request.supplierNumber())
                && supplierRepository.existsBySupplierNumberIgnoreCase(request.supplierNumber())) {
            throw new IllegalArgumentException("Supplier number already exists: " + request.supplierNumber());
        }
        
        mapRequestToEntity(request, supplier);
        
        supplier = supplierRepository.save(supplier);
        
        return toResponse(supplier);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Soft-deleting supplier id={}", id);
        var supplier = findEntityById(id);
        
        supplier.setDeletedAt(Instant.now());
        supplier.setActive(false);
        
        supplierRepository.save(supplier);
    }

    private String generateSupplierNumber() {
        String prefix = "S-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")) + "-";
        long count = supplierRepository.count() + 1;
        return prefix + String.format("%05d", count);
    }

    private void mapRequestToEntity(SupplierRequest r, Supplier s) {
        if (r.supplierNumber() != null && !r.supplierNumber().isBlank()) s.setSupplierNumber(r.supplierNumber());
        s.setCompanyName(r.companyName());
        s.setFirstName(r.firstName());
        s.setLastName(r.lastName());
        s.setEmail(r.email());
        s.setPhone(r.phone());
        s.setStreet(r.street());
        s.setCity(r.city());
        s.setPostalCode(r.postalCode());
        s.setCanton(r.canton());
        s.setCountry(r.country() != null ? r.country() : "CH");
        s.setVatNumber(r.vatNumber());
        s.setPaymentTerms(r.paymentTerms() != null ? r.paymentTerms() : 30);
        s.setContactPerson(r.contactPerson());
        s.setWebsite(r.website());
        s.setNotes(r.notes());
        s.setActive(r.active() != null ? r.active() : true);
    }

    private SupplierResponse toResponse(Supplier e) {
        return new SupplierResponse(e.getId(), e.getSupplierNumber(), e.getCompanyName(),
                e.getFirstName(), e.getLastName(), e.getDisplayName(),
                e.getEmail(), e.getPhone(), e.getStreet(), e.getCity(),
                e.getPostalCode(), e.getCanton(), e.getCountry(),
                e.getVatNumber(), e.getPaymentTerms(),
                e.getContactPerson(), e.getWebsite(), e.getNotes(),
                e.getActive(), e.getCreatedAt(), e.getUpdatedAt());
    }
}

