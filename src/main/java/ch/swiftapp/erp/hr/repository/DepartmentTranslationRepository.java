package ch.swiftapp.erp.hr.repository;

import ch.swiftapp.erp.hr.model.DepartmentTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentTranslationRepository extends JpaRepository<DepartmentTranslation, UUID> {
    List<DepartmentTranslation> findByDepartmentId(UUID departmentId);
    Optional<DepartmentTranslation> findByDepartmentIdAndLocale(UUID departmentId, String locale);
    void deleteByDepartmentId(UUID departmentId);
}

