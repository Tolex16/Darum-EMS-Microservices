package com.darum.ems.employee.application;

import com.darum.ems.employee.api.dto.DepartmentCreateRequest;
import com.darum.ems.employee.api.dto.DepartmentResponse;
import com.darum.ems.employee.domain.Department;
import com.darum.ems.employee.domain.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departments;

    // CREATE
    @Transactional
    public DepartmentResponse create(DepartmentCreateRequest req) {
        Department dept = new Department();
        dept.setName(req.name());
        dept.setCode(req.code());
        return DepartmentResponse.fromEntity(departments.save(dept));
    }

    // UPDATE
    @Transactional
    public DepartmentResponse update(Long id, DepartmentCreateRequest req) {
        Department dept = departments.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));
        dept.setName(req.name());
        dept.setCode(req.code());
        return DepartmentResponse.fromEntity(departments.save(dept));
    }

    // DELETE
    @Transactional
    public void delete(Long id) {
        if (!departments.existsById(id)) {
            throw new EntityNotFoundException("Department not found");
        }
        departments.deleteById(id);
    }

    // VIEW SINGLE
    @Transactional(readOnly = true)
    public DepartmentResponse getById(Long id) {
        Department dept = departments.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));
        return DepartmentResponse.fromEntity(dept);
    }

    // VIEW ALL
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAll(int page, int size) {
        Page<Department> result = departments.findAll(PageRequest.of(page, size));
        return result.stream()
                .map(DepartmentResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
