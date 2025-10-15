package com.darum.ems.employee.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  boolean existsByEmail(String email);
  boolean existsByEmployeeId(String employeeId);
}
