package ch.swiftapp.erp.production.api;

import ch.swiftapp.erp.production.dto.WorkCenterRequest;
import ch.swiftapp.erp.production.dto.WorkCenterResponse;
import ch.swiftapp.erp.production.service.WorkCenterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/work-centers") @RequiredArgsConstructor
@Tag(name = "Work Centers", description = "Manufacturing work center / workstation management")
@PreAuthorize("hasAuthority('PRODUCTION:VIEW')")
public class WorkCenterRestController {
    private final WorkCenterService service;

    @GetMapping
    public Page<WorkCenterResponse> list(@RequestParam(required = false) String search, Pageable p) {
        return (search != null && !search.isBlank()) ? service.search(search, p) : service.findAll(p);
    }
    @GetMapping("/{id}") public WorkCenterResponse getById(@PathVariable UUID id) { return service.findById(id); }
    @PreAuthorize("hasAuthority('PRODUCTION:CREATE')")
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public WorkCenterResponse create(@Valid @RequestBody WorkCenterRequest r) { return service.create(r); }
    @PreAuthorize("hasAuthority('PRODUCTION:EDIT')")
    @PutMapping("/{id}") public WorkCenterResponse update(@PathVariable UUID id, @Valid @RequestBody WorkCenterRequest r) { return service.update(id, r); }
    @PreAuthorize("hasAuthority('PRODUCTION:DELETE')")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}

