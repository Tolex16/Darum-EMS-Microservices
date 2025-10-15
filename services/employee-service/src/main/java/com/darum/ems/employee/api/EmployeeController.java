package com.darum.ems.employee.api;

import com.darum.ems.employee.api.dto.EmployeeCreateRequest;
import com.darum.ems.employee.api.dto.EmployeeResponse;
import com.darum.ems.employee.api.dto.EmployeeUpdateRequest;
import com.darum.ems.employee.application.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;


    // ✅ Create new employee
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request,@RequestHeader(value = "X-User-Role", required = false) String userRole) {

      // Role-based access control: Only ADMIN can create employees
      if (!"ADMIN".equals(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }

      EmployeeResponse response = employeeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ Update employee
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeUpdateRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String userRole)
    {

      // Role-based access control: Only ADMIN can update employees
      if (!"ADMIN".equals(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
        EmployeeResponse response = employeeService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // ✅ Delete employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id, @RequestHeader(value = "X-User-Role", required = false) String userRole){

     // Role-based access control: Only ADMIN can update employees
      if (!"ADMIN".equals(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
      employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Get employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse response = employeeService.getById(id);
        return ResponseEntity.ok(response);
    }

    // ✅ Get all employees (paginated)
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<EmployeeResponse> employees = employeeService.getAll(page, size);
        return ResponseEntity.ok(employees);
    }
}
