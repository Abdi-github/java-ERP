package ch.swiftapp.erp.hr.event;

import java.util.UUID;

public record EmployeeTerminatedEvent(UUID employeeId, String employeeNumber) {}

