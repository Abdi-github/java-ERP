package ch.swiftapp.erp.hr.service;

import ch.swiftapp.erp.hr.dto.EmployeeRequest;
import ch.swiftapp.erp.hr.dto.EmployeeResponse;
import ch.swiftapp.erp.hr.event.EmployeeCreatedEvent;
import ch.swiftapp.erp.hr.event.EmployeeTerminatedEvent;
import ch.swiftapp.erp.hr.model.Employee;
import ch.swiftapp.erp.hr.repository.DepartmentRepository;
import ch.swiftapp.erp.hr.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Page<EmployeeResponse> findAll(Pageable pageable) {
        return employeeRepository.findAllByDeletedAtIsNull(pageable).map(this::toResponse);
    }

    public Page<EmployeeResponse> search(String query, Pageable pageable) {
        return employeeRepository.searchEmployees(query, pageable).map(this::toResponse);
    }

    public EmployeeResponse findById(UUID id) { return toResponse(findEntityById(id)); }

    public Optional<EmployeeResponse> findByIdOptional(UUID id) {
        return employeeRepository.findById(id).filter(e -> e.getDeletedAt() == null).map(this::toResponse);
    }

    public Optional<EmployeeResponse> findByNumber(String number) {
        return employeeRepository.findByEmployeeNumberIgnoreCaseAndDeletedAtIsNull(number).map(this::toResponse);
    }

    public boolean isActive(UUID id) { return employeeRepository.isActiveAndNotDeleted(id); }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        log.info("Creating employee: number={}", request.employeeNumber());
        if (employeeRepository.existsByEmployeeNumberIgnoreCase(request.employeeNumber())) {
            throw new IllegalArgumentException("Employee number already exists: " + request.employeeNumber());
        }
        
        var emp = new Employee();
        mapRequestToEntity(request, emp);
        // mapped employee draft, lastName={} hireDate={}
        
        emp = employeeRepository.save(emp);
        
        // TODO event payload: if listeners change, recheck fullName mapping
        eventPublisher.publishEvent(new EmployeeCreatedEvent(emp.getId(), emp.getEmployeeNumber(), emp.getFullName()));
        // published EmployeeCreatedEvent
        
        log.info("Created employee id={}", emp.getId());
        return toResponse(emp);
    }

    @Transactional
    public EmployeeResponse update(UUID id, EmployeeRequest request) {
        var emp = findEntityById(id);
        
        if (!emp.getEmployeeNumber().equalsIgnoreCase(request.employeeNumber())
                && employeeRepository.existsByEmployeeNumberIgnoreCase(request.employeeNumber())) {
            throw new IllegalArgumentException("Employee number already exists: " + request.employeeNumber());
        }
        // employee number change check passed
        
        mapRequestToEntity(request, emp);
        
        emp = employeeRepository.save(emp);
        // save ok
        
        return toResponse(emp);
    }

    @Transactional
    public void terminate(UUID id) {
        var emp = findEntityById(id);
        
        emp.setTerminationDate(LocalDate.now());
        emp.setActive(false);
        // set terminated flag + date
        
        employeeRepository.save(emp);
        
        eventPublisher.publishEvent(new EmployeeTerminatedEvent(emp.getId(), emp.getEmployeeNumber()));
        // TODO keep an eye on termination event ordering with downstream handlers
    }

    @Transactional
    public void delete(UUID id) {
        var emp = findEntityById(id);
        emp.setDeletedAt(Instant.now());
        employeeRepository.save(emp);
    }

    private Employee findEntityById(UUID id) {
        return employeeRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
    }

    private void mapRequestToEntity(EmployeeRequest request, Employee emp) {
        emp.setEmployeeNumber(request.employeeNumber());
        emp.setFirstName(request.firstName());
        emp.setLastName(request.lastName());
        emp.setEmail(request.email());
        emp.setPhone(request.phone());
        emp.setHireDate(request.hireDate());
        emp.setTerminationDate(request.terminationDate());
        emp.setPosition(request.position());
        emp.setSalary(request.salary());
        emp.setActive(request.active() != null ? request.active() : true);
        if (request.departmentId() != null) {
            emp.setDepartment(departmentRepository.findById(request.departmentId()).orElse(null));
        } else {
            emp.setDepartment(null);
        }
    }

    private EmployeeResponse toResponse(Employee emp) {
        return new EmployeeResponse(emp.getId(), emp.getEmployeeNumber(), emp.getFirstName(), emp.getLastName(),
                emp.getFullName(), emp.getEmail(), emp.getPhone(), emp.getHireDate(), emp.getTerminationDate(),
                emp.getDepartment() != null ? emp.getDepartment().getId() : null,
                emp.getDepartment() != null ? emp.getDepartment().getName() : null,
                emp.getPosition(), emp.getSalary(), emp.getActive(), emp.getCreatedAt(), emp.getUpdatedAt());
    }
}

