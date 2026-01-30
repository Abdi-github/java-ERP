package ch.swiftapp.erp.hr.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EmployeeRequest(
        @NotBlank(message = "Employee number is required") @Size(max = 30) String employeeNumber,
        @NotBlank(message = "First name is required") @Size(max = 100) String firstName,
        @NotBlank(message = "Last name is required") @Size(max = 100) String lastName,
        @Email String email,
        String phone,
        @NotNull(message = "Hire date is required") LocalDate hireDate,
        LocalDate terminationDate,
        UUID departmentId,
        String position,
        @DecimalMin(value = "0.0000") BigDecimal salary,
        Boolean active
) {}

