package com.darum.ems.employee.application;

import com.darum.ems.employee.api.dto.DepartmentCreateRequest;
import com.darum.ems.employee.api.dto.EmployeeCreateRequest;
import com.darum.ems.employee.api.dto.EmployeeResponse;
import com.darum.ems.employee.api.dto.EmployeeUpdateRequest;
import com.darum.ems.employee.application.events.EmployeeCreatedEvent;
import com.darum.ems.employee.application.events.EmployeeEventPublisher;
import com.darum.ems.employee.domain.Department;
import com.darum.ems.employee.domain.DepartmentRepository;
import com.darum.ems.employee.domain.Employee;
import com.darum.ems.employee.domain.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employees;
  private final DepartmentRepository departments;
  private final EmployeeEventPublisher publisher;

  // ✅ CREATE EMPLOYEE
  @Transactional
  public EmployeeResponse create(EmployeeCreateRequest req) {
    Department dept = departments.findById(req.departmentId())
        .orElseThrow(() -> new EntityNotFoundException("Department not found"));

    Employee e = new Employee();
    e.setFirstName(req.firstName());
    e.setLastName(req.lastName());
    e.setEmail(req.email());
    e.setStatus(req.status());
    e.setDepartment(dept);
    Employee saved = employees.save(e);

    publisher.publish(new EmployeeCreatedEvent(saved.getEmployeeId(), saved.getEmail(), dept.getId(), Instant.now()));

    return new EmployeeResponse(saved.getEmployeeId(), saved.getFirstName(), saved.getLastName(),
        saved.getEmail(), saved.getStatus(), dept.getId(), dept.getName(), dept.getCode());
  }

      // ✅ UPDATE EMPLOYEE
    @Transactional
    public EmployeeResponse update(Long id, EmployeeUpdateRequest req) {
        Employee e = employees.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        if (req.firstName() != null) e.setFirstName(req.firstName());
        if (req.lastName() != null) e.setLastName(req.lastName());
        if (req.email() != null) e.setEmail(req.email());
        if (req.status() != null) e.setStatus(req.status());
        if (req.departmentId() != null) {
            Department dept = departments.findById(req.departmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found"));
            e.setDepartment(dept);
        }

        return EmployeeResponse.fromEntity(employees.save(e));
    }

    // ✅ DELETE EMPLOYEE
    @Transactional
    public void delete(Long id) {
        if (!employees.existsById(id)) {
            throw new EntityNotFoundException("Employee not found");
        }
        employees.deleteById(id);
    }

    // ✅ VIEW SINGLE EMPLOYEE
    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        Employee e = employees.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        return EmployeeResponse.fromEntity(e);
    }

    // ✅ VIEW ALL EMPLOYEES (Paginated)
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll(int page, int size) {
        Page<Employee> result = employees.findAll(PageRequest.of(page, size));
        return result.stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }


}
