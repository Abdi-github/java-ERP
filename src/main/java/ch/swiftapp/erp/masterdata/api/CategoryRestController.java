package ch.swiftapp.erp.masterdata.api;

import ch.swiftapp.erp.masterdata.dto.CategoryRequest;
import ch.swiftapp.erp.masterdata.dto.CategoryResponse;
import ch.swiftapp.erp.masterdata.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for category management — JSON API at {@code /api/v1/categories}.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Product and material category taxonomy")
@PreAuthorize("hasAuthority('MASTERDATA:VIEW')")
public class CategoryRestController {

    private final CategoryService categoryService;

    @GetMapping
    public Page<CategoryResponse> list(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return categoryService.search(search, pageable);
        }
        return categoryService.findAll(pageable);
    }

    @GetMapping("/roots")
    public List<CategoryResponse> roots() {
        return categoryService.findRootCategories();
    }

    @GetMapping("/flat")
    public List<CategoryResponse> flat() {
        return categoryService.findAllFlat();
    }

    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable UUID id) {
        return categoryService.findById(id);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {
        return categoryService.create(request);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:EDIT')")
    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable UUID id,
                                   @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @PreAuthorize("hasAuthority('MASTERDATA:DELETE')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        categoryService.delete(id);
    }
}

