package ch.swiftapp.erp.qualitycontrol.api;

import ch.swiftapp.erp.qualitycontrol.dto.*;
import ch.swiftapp.erp.qualitycontrol.model.NcrStatus;
import ch.swiftapp.erp.qualitycontrol.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/ncrs") @RequiredArgsConstructor
@Tag(name = "Non-Conformance Reports", description = "Quality control NCR tracking and resolution")
@PreAuthorize("hasAuthority('QC:VIEW')")
public class NcrRestController {
    private final NcrService service;
    @GetMapping public Page<NcrResponse> list(@RequestParam(required = false) NcrStatus status, Pageable p) { return status != null ? service.findByStatus(status, p) : service.findAll(p); }
    @GetMapping("/{id}") public NcrResponse getById(@PathVariable UUID id) { return service.findById(id); }
    @PreAuthorize("hasAuthority('QC:CREATE')")
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public NcrResponse create(@Valid @RequestBody NcrRequest r) { return service.create(r); }
    @PreAuthorize("hasAuthority('QC:CREATE')")
    @PostMapping("/{id}/close") public NcrResponse close(@PathVariable UUID id) { return service.close(id); }
}

