package com.darum.ems.employee.api.dto;

import com.darum.ems.employee.domain.Employee;

public record EmployeeResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String status,
    Long departmentId,
    String departmentName,
    String departmentCode
) {
    public static EmployeeResponse fromEntity(Employee e) {
        return new EmployeeResponse(
            e.getEmployeeId(),
            e.getFirstName(),
            e.getLastName(),
            e.getEmail(),
            e.getStatus(),
            e.getDepartment().getId(),
            e.getDepartment().getName(),
            e.getDepartment().getCode()
        );
    }
}
