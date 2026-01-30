package ch.swiftapp.erp.hr.service;

import ch.swiftapp.erp.hr.HrModuleApi;
import ch.swiftapp.erp.hr.dto.EmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HrModuleApiFacade implements HrModuleApi {
    private final EmployeeService employeeService;

    @Override
    public Optional<EmployeeResponse> findEmployeeById(UUID id) { return employeeService.findByIdOptional(id); }

    @Override
    public Optional<EmployeeResponse> findEmployeeByNumber(String employeeNumber) { return employeeService.findByNumber(employeeNumber); }

    @Override
    public boolean isEmployeeActive(UUID id) { return employeeService.isActive(id); }
}

