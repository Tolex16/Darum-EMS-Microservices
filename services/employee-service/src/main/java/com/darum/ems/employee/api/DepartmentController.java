package com.darum.ems.employee.api;

import com.darum.ems.employee.api.dto.DepartmentCreateRequest;
import com.darum.ems.employee.api.dto.DepartmentResponse;
import com.darum.ems.employee.application.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    // ✅ Create new department
    @PostMapping("/create")
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentCreateRequest request, @RequestHeader(value = "X-User-Role", required = false) String userRole){
      // Only ADMIN can create departments
      if (!"ADMIN".equals(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
      DepartmentResponse response = departmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ Update department
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long id,
            @RequestBody DepartmentCreateRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String userRole)

    {

      // Only ADMIN can create departments
      if (!"ADMIN".equals(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
        DepartmentResponse response = departmentService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // ✅ Delete department
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Get department by ID
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        DepartmentResponse response = departmentService.getById(id);
        return ResponseEntity.ok(response);
    }

    // ✅ Get all departments (paginated)
    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<DepartmentResponse> departments = departmentService.getAll(page, size);
        return ResponseEntity.ok(departments);
    }
}
