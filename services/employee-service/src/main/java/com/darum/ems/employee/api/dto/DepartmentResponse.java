  package com.darum.ems.employee.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.darum.ems.employee.domain.Department;

public record DepartmentResponse(Long id, String name, String code) {
    public static DepartmentResponse fromEntity(Department d) {
        return new DepartmentResponse(d.getId(), d.getName(), d.getCode());
    }
}
