  package com.darum.ems.employee.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmployeeUpdateRequest(
    String firstName,
    String lastName,
    String email,
    String status,
    Long departmentId
) {}
