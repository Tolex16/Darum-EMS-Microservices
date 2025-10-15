package com.darum.ems.employee.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "employees")
@NoArgsConstructor
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="employee_id", nullable=false, unique=true)
  private Long employeeId;

  @Column(nullable=false)
  private String firstName;

  @Column(nullable=false)
  private String lastName;

  @Column(nullable=false, unique=true)
  private String email;

  @Column(nullable=false)
  private String status = "ACTIVE";

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @ManyToOne(optional=false)
  @JoinColumn(name="department_id")
  private Department department;
}
