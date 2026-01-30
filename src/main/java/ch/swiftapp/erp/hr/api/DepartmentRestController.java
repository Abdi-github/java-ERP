package ch.swiftapp.erp.hr.api;

import ch.swiftapp.erp.hr.dto.DepartmentRequest;
import ch.swiftapp.erp.hr.dto.DepartmentResponse;
import ch.swiftapp.erp.hr.service.DepartmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Organizational department management")
@PreAuthorize("hasAuthority('HR:VIEW')")
public class DepartmentRestController {
    private final DepartmentService departmentService;

    @GetMapping
    public Page<DepartmentResponse> list(@RequestParam(required = false) String search, Pageable pageable) {
        return (search != null && !search.isBlank()) ? departmentService.search(search, pageable) : departmentService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public DepartmentResponse getById(@PathVariable UUID id) { return departmentService.findById(id); }

    @PreAuthorize("hasAuthority('HR:CREATE')")
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(@Valid @RequestBody DepartmentRequest request) { return departmentService.create(request); }

    @PreAuthorize("hasAuthority('HR:EDIT')")
    @PutMapping("/{id}")
    public DepartmentResponse update(@PathVariable UUID id, @Valid @RequestBody DepartmentRequest request) { return departmentService.update(id, request); }

    @PreAuthorize("hasAuthority('HR:DELETE')")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { departmentService.delete(id); }
}

