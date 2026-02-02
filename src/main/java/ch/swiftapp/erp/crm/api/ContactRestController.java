package ch.swiftapp.erp.crm.api;

import ch.swiftapp.erp.crm.dto.*;
import ch.swiftapp.erp.crm.service.ContactService;
import ch.swiftapp.erp.crm.service.InteractionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/contacts") @RequiredArgsConstructor
@Tag(name = "Contacts", description = "CRM contact and interaction management")
@PreAuthorize("hasAuthority('CRM:VIEW')")
public class ContactRestController {
    private final ContactService contactService;
    private final InteractionService interactionService;

    @GetMapping
    public Page<ContactResponse> list(@RequestParam(required = false) String search, Pageable pageable) {
        return (search != null && !search.isBlank()) ? contactService.search(search, pageable) : contactService.findAll(pageable);
    }

    @GetMapping("/{id}") public ContactResponse getById(@PathVariable UUID id) { return contactService.findById(id); }
    @PreAuthorize("hasAuthority('CRM:CREATE')")
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ContactResponse create(@Valid @RequestBody ContactRequest r) { return contactService.create(r); }
    @PreAuthorize("hasAuthority('CRM:EDIT')")
    @PutMapping("/{id}") public ContactResponse update(@PathVariable UUID id, @Valid @RequestBody ContactRequest r) { return contactService.update(id, r); }
    @PreAuthorize("hasAuthority('CRM:DELETE')")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { contactService.delete(id); }

    @GetMapping("/{id}/interactions")
    public Page<InteractionResponse> interactions(@PathVariable UUID id, Pageable pageable) {
        return interactionService.findByContactId(id, pageable);
    }

    @PreAuthorize("hasAuthority('CRM:CREATE')")
    @PostMapping("/{id}/interactions") @ResponseStatus(HttpStatus.CREATED)
    public InteractionResponse addInteraction(@PathVariable UUID id, @Valid @RequestBody InteractionRequest r) {
        return interactionService.create(r);
    }
}

