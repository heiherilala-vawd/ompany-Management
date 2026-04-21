package com.example.demo.service.movement;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.WarehouseCriteria;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.movement.WarehouseRepository;
import com.example.demo.service.utils.PageUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseService {

  private final WarehouseRepository warehouseRepository;

  public Optional<Warehouse> findById(String id) {
    return warehouseRepository.findById(id);
  }

  public Page<Warehouse> findAll(
      PageFromOne page, BoundedPageSize pageSize, WarehouseCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return warehouseRepository.findAll(toSpecification(criteria), pageable);
  }

  public List<Warehouse> findByJobId(String jobId) {
    return warehouseRepository.findByJobId(jobId);
  }

  @Transactional
  public List<Warehouse> createOrUpdateAll(List<Warehouse> warehouses) {
    return warehouseRepository.saveAll(warehouses);
  }

  @Transactional
  public void deleteById(String id) {
    warehouseRepository.deleteById(id);
  }

  private Specification<Warehouse> toSpecification(WarehouseCriteria criteria) {
    return Specification.<Warehouse>where(equal(criteria.getJobId(), "job", "id"))
        .and(containsIgnoreCase(criteria.getName(), "name"))
        .and(containsIgnoreCase(criteria.getDescription(), "description"));
  }
}
