package ch.swiftapp.erp.crm.service;

import ch.swiftapp.erp.crm.dto.*;
import ch.swiftapp.erp.crm.event.InteractionCreatedEvent;
import ch.swiftapp.erp.crm.model.Interaction;
import ch.swiftapp.erp.crm.repository.ContactRepository;
import ch.swiftapp.erp.crm.repository.InteractionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly = true)
public class InteractionService {
    private final InteractionRepository interactionRepository;
    private final ContactRepository contactRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Page<InteractionResponse> findAll(Pageable pageable) {
        return interactionRepository.findAllByOrderByInteractionDateDesc(pageable).map(this::toResponse);
    }

    public Page<InteractionResponse> findByContactId(UUID contactId, Pageable pageable) {
        return interactionRepository.findAllByContactIdOrderByInteractionDateDesc(contactId, pageable).map(this::toResponse);
    }

    public InteractionResponse findById(UUID id) {
        return toResponse(interactionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Interaction not found: " + id)));
    }

    @Transactional
    public InteractionResponse create(InteractionRequest r) {
        var contact = contactRepository.findById(r.contactId())
                .orElseThrow(() -> new EntityNotFoundException("Contact not found: " + r.contactId()));
        var i = Interaction.builder().contact(contact).interactionType(r.interactionType())
                .subject(r.subject()).description(r.description())
                .interactionDate(r.interactionDate() != null ? r.interactionDate() : Instant.now())
                .followUpDate(r.followUpDate()).build();
        i = interactionRepository.save(i);
        eventPublisher.publishEvent(new InteractionCreatedEvent(i.getId(), contact.getId(), i.getSubject()));
        return toResponse(i);
    }

    private InteractionResponse toResponse(Interaction i) {
        return new InteractionResponse(i.getId(), i.getContact().getId(), i.getContact().getFullName(),
                i.getInteractionType(), i.getSubject(), i.getDescription(),
                i.getInteractionDate(), i.getFollowUpDate(), i.getCreatedAt());
    }
}

