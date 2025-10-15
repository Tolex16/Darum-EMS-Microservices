package com.darum.ems.employee.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Table(name = "departments")
@NoArgsConstructor
public class Department {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false, unique = true)
  private String code;
}
