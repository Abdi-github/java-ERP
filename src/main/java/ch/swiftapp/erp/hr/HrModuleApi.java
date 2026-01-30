package ch.swiftapp.erp.hr;

import ch.swiftapp.erp.hr.dto.EmployeeResponse;

import java.util.Optional;
import java.util.UUID;

/**
 * Public API for the HR module.
 */
public interface HrModuleApi {

    Optional<EmployeeResponse> findEmployeeById(UUID id);

    Optional<EmployeeResponse> findEmployeeByNumber(String employeeNumber);

    boolean isEmployeeActive(UUID id);
}

