package ch.swiftapp.erp.accounting.api;

import ch.swiftapp.erp.accounting.dto.JournalEntryRequest;
import ch.swiftapp.erp.accounting.dto.JournalEntryResponse;
import ch.swiftapp.erp.accounting.service.JournalEntryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for journal entries — JSON API at {@code /api/v1/journal-entries}.
 */
@RestController
@RequestMapping("/api/v1/journal-entries")
@RequiredArgsConstructor
@Tag(name = "Journal Entries", description = "Double-entry bookkeeping journal entries")
@PreAuthorize("hasAuthority('ACCOUNTING:VIEW')")
public class JournalEntryRestController {

    private final JournalEntryService journalEntryService;

    @GetMapping
    public Page<JournalEntryResponse> list(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return journalEntryService.search(search, pageable);
        }
        return journalEntryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public JournalEntryResponse getById(@PathVariable UUID id) {
        return journalEntryService.findById(id);
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:CREATE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JournalEntryResponse create(@Valid @RequestBody JournalEntryRequest request) {
        return journalEntryService.create(request);
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:CREATE')")
    @PostMapping("/{id}/post")
    public void post(@PathVariable UUID id) {
        journalEntryService.post(id);
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:CREATE')")
    @PostMapping("/{id}/reverse")
    public JournalEntryResponse reverse(@PathVariable UUID id) {
        return journalEntryService.reverse(id);
    }

    @PreAuthorize("hasAuthority('ACCOUNTING:DELETE')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        journalEntryService.delete(id);
    }
}

