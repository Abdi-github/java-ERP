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

@RestController @RequestMapping("/api/v1/quality-checks") @RequiredArgsConstructor
@PreAuthorize("hasAuthority('QC:VIEW')")
public class QualityCheckRestController {
    private final QualityCheckService service;
    @GetMapping public Page<QualityCheckResponse> list(Pageable p) { return service.findAll(p); }
    @GetMapping("/{id}") public QualityCheckResponse getById(@PathVariable UUID id) { return service.findById(id); }
    @PreAuthorize("hasAuthority('QC:CREATE')")
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public QualityCheckResponse create(@Valid @RequestBody QualityCheckRequest r) { return service.create(r); }
}

