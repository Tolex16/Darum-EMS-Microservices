package com.darum.ems.employee.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetails {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private Long departmentId;
  private String departmentName;
  private String status;
  private String departmentCode;

}
