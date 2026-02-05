package ch.swiftapp.erp.qualitycontrol.api;

import ch.swiftapp.erp.qualitycontrol.dto.*;
import ch.swiftapp.erp.qualitycontrol.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/inspection-plans") @RequiredArgsConstructor
@PreAuthorize("hasAuthority('QC:VIEW')")
public class InspectionPlanRestController {
    private final InspectionPlanService service;
    @GetMapping public Page<InspectionPlanResponse> list(@RequestParam(required = false) String search, Pageable p) { return (search != null && !search.isBlank()) ? service.search(search, p) : service.findAll(p); }
    @GetMapping("/{id}") public InspectionPlanResponse getById(@PathVariable UUID id) { return service.findById(id); }
    @PreAuthorize("hasAuthority('QC:CREATE')")
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public InspectionPlanResponse create(@Valid @RequestBody InspectionPlanRequest r) { return service.create(r); }
    @PreAuthorize("hasAuthority('QC:EDIT')")
    @PutMapping("/{id}") public InspectionPlanResponse update(@PathVariable UUID id, @Valid @RequestBody InspectionPlanRequest r) { return service.update(id, r); }
    @PreAuthorize("hasAuthority('QC:DELETE')")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}

