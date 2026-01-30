package ch.swiftapp.erp.hr.api;

import ch.swiftapp.erp.hr.dto.EmployeeRequest;
import ch.swiftapp.erp.hr.dto.EmployeeResponse;
import ch.swiftapp.erp.hr.service.EmployeeService;
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
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Employee records and HR management")
@PreAuthorize("hasAuthority('HR:VIEW')")
public class EmployeeRestController {
    private final EmployeeService employeeService;

    @GetMapping
    public Page<EmployeeResponse> list(@RequestParam(required = false) String search, Pageable pageable) {
        return (search != null && !search.isBlank()) ? employeeService.search(search, pageable) : employeeService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable UUID id) { return employeeService.findById(id); }

    @PreAuthorize("hasAuthority('HR:CREATE')")
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) { return employeeService.create(request); }

    @PreAuthorize("hasAuthority('HR:EDIT')")
    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable UUID id, @Valid @RequestBody EmployeeRequest request) { return employeeService.update(id, request); }

    @PreAuthorize("hasAuthority('HR:CREATE')")
    @PostMapping("/{id}/terminate")
    public void terminate(@PathVariable UUID id) { employeeService.terminate(id); }

    @PreAuthorize("hasAuthority('HR:DELETE')")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { employeeService.delete(id); }
}

