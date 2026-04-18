package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.movement.Equipment;
import com.example.demo.repository.movement.EquipmentRepository;
import com.example.demo.service.utils.PageUtils;
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
public class EquipmentService {

  private final EquipmentRepository equipmentRepository;

  public Optional<Equipment> findById(String id) {
    return equipmentRepository.findById(id);
  }

  public Page<Equipment> findAll(PageFromOne page, BoundedPageSize pageSize, String warehouseId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);

    if (warehouseId != null) {
      return equipmentRepository.findByWarehouseId(warehouseId, pageable);
    }

    return equipmentRepository.findAll(pageable);
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
}
