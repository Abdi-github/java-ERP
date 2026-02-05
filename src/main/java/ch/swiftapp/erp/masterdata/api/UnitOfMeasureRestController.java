package ch.swiftapp.erp.masterdata.api;

import ch.swiftapp.erp.masterdata.dto.UnitOfMeasureRequest;
import ch.swiftapp.erp.masterdata.dto.UnitOfMeasureResponse;
import ch.swiftapp.erp.masterdata.service.UnitOfMeasureService;
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
 * REST controller for unit of measure management — JSON API at {@code /api/v1/units-of-measure}.
 */
@RestController
@RequestMapping("/api/v1/units-of-measure")
@RequiredArgsConstructor
@Tag(name = "Units of Measure", description = "Measurement unit definitions (kg, pcs, m, etc.)")
@PreAuthorize("hasAuthority('MASTERDATA:VIEW')")
public class UnitOfMeasureRestController {

    private final UnitOfMeasureService unitOfMeasureService;

    @GetMapping
    public Page<UnitOfMeasureResponse> list(Pageable pageable) {
        return unitOfMeasureService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public UnitOfMeasureResponse getById(@PathVariable UUID id) {
        return unitOfMeasureService.findById(id);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UnitOfMeasureResponse create(@Valid @RequestBody UnitOfMeasureRequest request) {
        return unitOfMeasureService.create(request);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:EDIT')")
    @PutMapping("/{id}")
    public UnitOfMeasureResponse update(@PathVariable UUID id,
                                        @Valid @RequestBody UnitOfMeasureRequest request) {
        return unitOfMeasureService.update(id, request);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:DELETE')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        unitOfMeasureService.delete(id);
    }
}

