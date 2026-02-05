package ch.swiftapp.erp.masterdata.api;

import ch.swiftapp.erp.masterdata.dto.MaterialRequest;
import ch.swiftapp.erp.masterdata.dto.MaterialResponse;
import ch.swiftapp.erp.masterdata.service.MaterialService;
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
 * REST controller for material management — JSON API at {@code /api/v1/materials}.
 */
@RestController
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
@Tag(name = "Materials", description = "Raw material and component management")
@PreAuthorize("hasAuthority('MASTERDATA:VIEW')")
public class MaterialRestController {

    private final MaterialService materialService;

    @GetMapping
    public Page<MaterialResponse> list(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return materialService.search(search, pageable);
        }
        return materialService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public MaterialResponse getById(@PathVariable UUID id) {
        return materialService.findById(id);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaterialResponse create(@Valid @RequestBody MaterialRequest request) {
        return materialService.create(request);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:EDIT')")
    @PutMapping("/{id}")
    public MaterialResponse update(@PathVariable UUID id,
                                   @Valid @RequestBody MaterialRequest request) {
        return materialService.update(id, request);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:DELETE')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        materialService.delete(id);
    }
}

