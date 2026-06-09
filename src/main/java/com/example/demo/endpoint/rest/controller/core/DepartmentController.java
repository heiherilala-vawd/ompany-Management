package com.example.demo.endpoint.rest.controller.core;

import com.example.demo.client.model.CrupdateDepartment;
import com.example.demo.client.model.Department;
import com.example.demo.endpoint.rest.mapper.core.DepartmentMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.core.DepartmentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class DepartmentController {

  private final DepartmentService departmentService;
  private final DepartmentMapper departmentMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/departments/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public Department getDepartmentById(@PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return departmentMapper.toRestDepartment(
        departmentService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Department with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/departments")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Department> getDepartments(
      @PathVariable String userId, @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return departmentMapper.toRestDepartments(
        departmentService.findAll(page, pageSize).getContent());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/departments")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Department> crupdateDepartments(
      @PathVariable String userId, @PathVariable String companyId, @Valid @RequestBody List<CrupdateDepartment> toWrite) {
    List<com.example.demo.model.core.Department> saved =
        departmentService.createOrUpdateAll(
            toWrite.stream().map(d -> departmentMapper.toDomain(d, companyId)).toList());
    return departmentMapper.toRestDepartments(saved);
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/departments/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteDepartmentById(@PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    departmentService.deleteById(id);
  }
}
