package ch.swiftapp.erp.hr.event;

import java.util.UUID;

public record EmployeeCreatedEvent(UUID employeeId, String employeeNumber, String fullName) {}

