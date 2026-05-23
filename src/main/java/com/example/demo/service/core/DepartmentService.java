package com.example.demo.service.core;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.core.Department;
import com.example.demo.repository.core.DepartmentRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final ModificationUtils modificationUtils;

  public Optional<Department> findById(String id) {
    return departmentRepository.findById(id);
  }

  public Page<Department> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return departmentRepository.findAll(pageable);
  }

  @Transactional
  public List<Department> createOrUpdateAll(List<Department> departments) {
    List<Department> processed = new ArrayList<>();
    for (Department department : departments) {
      Department existing =
          department.getId() == null
              ? null
              : departmentRepository.findById(department.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          department, existing, department.getId(), modificationUtils.takePrimaryUser());
      processed.add(department);
    }
    return departmentRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    departmentRepository.deleteById(id);
  }
}
