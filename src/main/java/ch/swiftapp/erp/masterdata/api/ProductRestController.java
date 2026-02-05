package ch.swiftapp.erp.masterdata.api;

import ch.swiftapp.erp.masterdata.dto.ProductRequest;
import ch.swiftapp.erp.masterdata.dto.ProductResponse;
import ch.swiftapp.erp.masterdata.dto.BomLineRequest;
import ch.swiftapp.erp.masterdata.dto.BomLineResponse;
import ch.swiftapp.erp.masterdata.service.BillOfMaterialService;
import ch.swiftapp.erp.masterdata.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for product management — JSON API at {@code /api/v1/products}.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog and bill of materials (BOM) management")
@PreAuthorize("hasAuthority('MASTERDATA:VIEW')")
public class ProductRestController {

    private final ProductService productService;
    private final BillOfMaterialService bomService;

    @GetMapping
    public Page<ProductResponse> list(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return productService.search(search, pageable);
        }
        return productService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable UUID id) {
        return productService.findById(id);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:EDIT')")
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable UUID id,
                                  @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:DELETE')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        productService.delete(id);
    }

    // ── BOM endpoints ─────────────────────────────────────────

    @GetMapping("/{productId}/bom")
    public List<BomLineResponse> getBom(@PathVariable UUID productId) {
        return bomService.findByProductId(productId);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping("/{productId}/bom")
    @ResponseStatus(HttpStatus.CREATED)
    public BomLineResponse addBomLine(@PathVariable UUID productId,
                                      @Valid @RequestBody BomLineRequest request) {
        return bomService.addLine(productId, request);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:EDIT')")
    @PutMapping("/{productId}/bom/{lineId}")
    public BomLineResponse updateBomLine(@PathVariable UUID productId,
                                         @PathVariable UUID lineId,
                                         @Valid @RequestBody BomLineRequest request) {
        return bomService.updateLine(lineId, request);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:DELETE')")
    @DeleteMapping("/{productId}/bom/{lineId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBomLine(@PathVariable UUID productId,
                              @PathVariable UUID lineId) {
        bomService.deleteLine(lineId);
    }
}

