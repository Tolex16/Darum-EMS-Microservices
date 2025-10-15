package com.darum.ems.employee.application.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreatedEvent {
    private Long employeeId;
    private String email;
    private Long departmentId;
    private Instant createdAt;
}