package ch.swiftapp.erp.hr.service;

import ch.swiftapp.erp.hr.dto.DepartmentRequest;
import ch.swiftapp.erp.hr.dto.DepartmentResponse;
import ch.swiftapp.erp.hr.model.Department;
import ch.swiftapp.erp.hr.model.DepartmentTranslation;
import ch.swiftapp.erp.hr.repository.DepartmentRepository;
import ch.swiftapp.erp.hr.repository.EmployeeRepository;
import ch.swiftapp.erp.shared.service.TranslationResolver;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final TranslationResolver translationResolver;

    public Page<DepartmentResponse> findAll(Pageable pageable) {
        return departmentRepository.findAllByDeletedAtIsNull(pageable).map(this::toResponse);
    }

    public List<DepartmentResponse> findAllActive() {
        return departmentRepository.findAllByDeletedAtIsNullAndActiveTrue().stream().map(this::toResponse).toList();
    }

    public Page<DepartmentResponse> search(String query, Pageable pageable) {
        return departmentRepository.searchDepartments(query, pageable).map(this::toResponse);
    }

    public DepartmentResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        log.info("Creating department: code={}", request.code());
        if (departmentRepository.existsByCodeIgnoreCase(request.code())) {
            throw new IllegalArgumentException("Department code already exists: " + request.code());
        }
        
        var dept = new Department();
        mapRequestToEntity(request, dept);
        
        applyTranslations(request.nameTranslations(), request.descriptionTranslations(), dept);
        
        dept = departmentRepository.save(dept);
        
        log.info("Created department id={}", dept.getId());
        return toResponse(dept);
    }

    @Transactional
    public DepartmentResponse update(UUID id, DepartmentRequest request) {
        var dept = findEntityById(id);
        
        if (!dept.getCode().equalsIgnoreCase(request.code()) && departmentRepository.existsByCodeIgnoreCase(request.code())) {
            throw new IllegalArgumentException("Department code already exists: " + request.code());
        }
        
        mapRequestToEntity(request, dept);
        
        dept.getTranslations().clear();
        
        applyTranslations(request.nameTranslations(), request.descriptionTranslations(), dept);
        
        dept = departmentRepository.save(dept);
        
        return toResponse(dept);
    }

    @Transactional
    public void delete(UUID id) {
        var dept = findEntityById(id);
        dept.setDeletedAt(Instant.now());
        departmentRepository.save(dept);
    }

    private Department findEntityById(UUID id) {
        return departmentRepository.findById(id)
                .filter(d -> d.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Department not found: " + id));
    }

    private void mapRequestToEntity(DepartmentRequest request, Department dept) {
        dept.setCode(request.code());
        dept.setName(request.name());
        dept.setDescription(request.description());
        dept.setManagerId(request.managerId());
        dept.setActive(request.active() != null ? request.active() : true);
    }

    private DepartmentResponse toResponse(Department dept) {
        String managerName = null;
        if (dept.getManagerId() != null) {
            managerName = employeeRepository.findById(dept.getManagerId())
                    .map(e -> e.getFirstName() + " " + e.getLastName())
                    .orElse(null);
        }
        var t = dept.getTranslations();
        return new DepartmentResponse(dept.getId(), dept.getCode(),
                translationResolver.resolve(t, DepartmentTranslation::getLocale, DepartmentTranslation::getName, dept.getName()),
                translationResolver.resolve(t, DepartmentTranslation::getLocale, DepartmentTranslation::getDescription, dept.getDescription()),
                dept.getManagerId(), managerName, dept.getActive(), dept.getCreatedAt(), dept.getUpdatedAt(),
                translationResolver.toMap(t, DepartmentTranslation::getLocale, DepartmentTranslation::getName),
                translationResolver.toMap(t, DepartmentTranslation::getLocale, DepartmentTranslation::getDescription));
    }

    private void applyTranslations(Map<String, String> names, Map<String, String> descriptions, Department dept) {
        if (names == null || names.isEmpty()) return;
        names.forEach((locale, name) -> {
            var translation = new DepartmentTranslation();
            translation.setLocale(locale);
            translation.setDepartment(dept);
            translation.setName(name);
            translation.setDescription(descriptions != null ? descriptions.get(locale) : null);
            dept.getTranslations().add(translation);
        });
    }
}
