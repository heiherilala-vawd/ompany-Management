package com.example.demo.service.movement;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.WarehouseCriteria;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.movement.WarehouseRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.MovementValidator;
import java.util.ArrayList;
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
  private final ModificationUtils modificationUtils;
  private final MovementValidator movementValidator;

  public Optional<Warehouse> findById(String id) {
    return warehouseRepository.findById(id);
  }

  public Page<Warehouse> findAll(
      PageFromOne page, BoundedPageSize pageSize, WarehouseCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return warehouseRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<Warehouse> createOrUpdateAll(List<Warehouse> warehouses) {
    movementValidator.validateWarehouses(warehouses);
    List<Warehouse> processedWarehouses = new ArrayList<>();
    for (Warehouse warehouse : warehouses) {
      Warehouse existingWarehouse = warehouseRepository.findById(warehouse.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          warehouse, existingWarehouse, warehouse.getId(), modificationUtils.takePrimaryUser());
      processedWarehouses.add(warehouse);
    }
    return warehouseRepository.saveAll(processedWarehouses);
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
