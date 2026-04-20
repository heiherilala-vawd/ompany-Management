package com.example.demo.service.movement;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.EquipmentCriteria;
import com.example.demo.model.movement.Equipment;
import com.example.demo.repository.movement.EquipmentRepository;
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
public class EquipmentService {

  private final EquipmentRepository equipmentRepository;

  public Optional<Equipment> findById(String id) {
    return equipmentRepository.findById(id);
  }

  public Page<Equipment> findAll(
      PageFromOne page, BoundedPageSize pageSize, EquipmentCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return equipmentRepository.findAll(toSpecification(criteria), pageable);
  }

  public List<Equipment> findAll() {
    return equipmentRepository.findAll();
  }

  public List<Equipment> findByWarehouseId(String warehouseId) {
    return equipmentRepository.findByWarehouseId(warehouseId);
  }

  @Transactional
  public Equipment create(Equipment equipment) {
    return equipmentRepository.save(equipment);
  }

  @Transactional
  public Equipment update(Equipment equipment) {
    return equipmentRepository.save(equipment);
  }

  @Transactional
  public List<Equipment> createOrUpdateAll(List<Equipment> equipmentList) {
    return equipmentRepository.saveAll(equipmentList);
  }

  @Transactional
  public void deleteById(String id) {
    equipmentRepository.deleteById(id);
  }

  public boolean existsById(String id) {
    return equipmentRepository.existsById(id);
  }

  private Specification<Equipment> toSpecification(EquipmentCriteria criteria) {
    return Specification.<Equipment>where(equal(criteria.getWarehouseId(), "warehouse", "id"))
        .and(containsIgnoreCase(criteria.getName(), "name"))
        .and(containsIgnoreCase(criteria.getDescription(), "description"))
        .and(equal(criteria.getFloorNumber(), "floorNumber"))
        .and(equal(criteria.getStorageNumber(), "storageNumber"));
  }
}
