package ch.swiftapp.erp.crm.service;

import ch.swiftapp.erp.crm.dto.*;
import ch.swiftapp.erp.crm.model.Contact;
import ch.swiftapp.erp.crm.repository.ContactRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly = true)
public class ContactService {
    private final ContactRepository contactRepository;

    public Page<ContactResponse> findAll(Pageable pageable) { return contactRepository.findAllByDeletedAtIsNull(pageable).map(this::toResponse); }
    public Page<ContactResponse> search(String q, Pageable pageable) { return contactRepository.searchContacts(q, pageable).map(this::toResponse); }
    public ContactResponse findById(UUID id) { return toResponse(findEntity(id)); }
    public Optional<ContactResponse> findByIdOptional(UUID id) { return contactRepository.findById(id).filter(c -> c.getDeletedAt() == null).map(this::toResponse); }

    @Transactional
    public ContactResponse create(ContactRequest r) {
        log.info("Creating contact: name={}", r.firstName());
        var c = new Contact();
        mapToEntity(r, c);
        
        c = contactRepository.save(c);
        // saved contact id={}
        
        log.info("Created contact id={}", c.getId());
        return toResponse(c);
    }

    @Transactional
    public ContactResponse update(UUID id, ContactRequest r) {
        var c = findEntity(id);
        // update target: {}
        
        mapToEntity(r, c);
        // fields remapped
        
        c = contactRepository.save(c);
        
        return toResponse(c);
    }

    @Transactional
    public void delete(UUID id) {
        var c = findEntity(id);
        // deleting contact {} (soft)
        
        c.setDeletedAt(Instant.now());
        // set deletedAt marker
        
        contactRepository.save(c);
        // delete marker saved
    }

    private Contact findEntity(UUID id) { return contactRepository.findById(id).filter(c -> c.getDeletedAt() == null).orElseThrow(() -> new EntityNotFoundException("Contact not found: " + id)); }

    private void mapToEntity(ContactRequest r, Contact c) {
        c.setFirstName(r.firstName()); c.setLastName(r.lastName()); c.setEmail(r.email());
        c.setPhone(r.phone()); c.setCompany(r.company()); c.setPosition(r.position());
        c.setCustomerId(r.customerId()); c.setNotes(r.notes());
        c.setActive(r.active() != null ? r.active() : true);
    }

    private ContactResponse toResponse(Contact c) {
        return new ContactResponse(c.getId(), c.getFirstName(), c.getLastName(), c.getFullName(),
                c.getEmail(), c.getPhone(), c.getCompany(), c.getPosition(),
                c.getCustomerId(), c.getNotes(), c.getActive(), c.getCreatedAt(), c.getUpdatedAt());
    }
}

