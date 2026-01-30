package ch.swiftapp.erp.hr.repository;

import ch.swiftapp.erp.hr.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByEmployeeNumberIgnoreCaseAndDeletedAtIsNull(String employeeNumber);
    Page<Employee> findAllByDeletedAtIsNull(Pageable pageable);
    boolean existsByEmployeeNumberIgnoreCase(String employeeNumber);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e WHERE e.id = :id AND e.active = true AND e.deletedAt IS NULL")
    boolean isActiveAndNotDeleted(UUID id);

    @Query("""
            SELECT e FROM Employee e WHERE e.deletedAt IS NULL
              AND (LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(e.employeeNumber) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Employee> searchEmployees(String search, Pageable pageable);
}

