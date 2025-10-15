  package com.darum.ems.employee.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmployeeCreateRequest(
  @NotBlank String employeeId,
  @NotBlank String firstName,
  @NotBlank String lastName,
  @Email String email,
  @NotBlank String status,
  @NotNull Long departmentId
) {}
