package ch.swiftapp.erp.hr.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EmployeeResponse(
        UUID id, String employeeNumber, String firstName, String lastName,
        String fullName, String email, String phone,
        LocalDate hireDate, LocalDate terminationDate,
        UUID departmentId, String departmentName,
        String position, BigDecimal salary,
        Boolean active, Instant createdAt, Instant updatedAt
) {}

