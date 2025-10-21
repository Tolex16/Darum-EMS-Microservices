package com.darum.ems.employee.api;

import com.darum.ems.employee.api.dto.EmployeeCreateRequest;
import com.darum.ems.employee.api.dto.EmployeeResponse;
import com.darum.ems.employee.api.dto.EmployeeUpdateRequest;
import com.darum.ems.employee.application.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

	// Role-based access control: Only ADMIN can create employees
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }

	// Role-based access control: Only ADMIN can update employees
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

	// ✅ Delete employee
	// Role-based access control: Only ADMIN can delete employees
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

	// ✅ Get employee by ID
    @GetMapping("/{id}")
    // ✅ Get employee by ID @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
      EmployeeResponse response = employeeService.getById(id);
      return ResponseEntity.ok(response);
    }

  // ✅ Get all employees (paginated)
  @GetMapping("/get-employees")
  public ResponseEntity<List<EmployeeResponse>> getAllEmployees( @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size ) {
    List<EmployeeResponse> employees = employeeService.getAll(page, size);
    return ResponseEntity.ok(employees);
  }




}
