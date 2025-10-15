package com.darum.ems.auth.Listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreatedEvent {
    private Long employeeId;
    private String email;
    private Long departmentId;
    private Instant createdAt;
}
