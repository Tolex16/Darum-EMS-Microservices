package com.darum.ems.employee.api.dto;


import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
public record DepartmentCreateRequest(String name, String code) {}
