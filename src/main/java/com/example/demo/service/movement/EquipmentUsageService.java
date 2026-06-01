package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.movement.EquipmentUsage;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.movement.EquipmentUsageRepository;
import com.example.demo.repository.specification.SpecificationUtils;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.service.utils.SpecialWarehouseUtils;
import com.example.demo.validator.MovementValidator;
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
public class EquipmentUsageService {

  private final EquipmentUsageRepository equipmentUsageRepository;
  private final ModificationUtils modificationUtils;
  private final EquipmentService equipmentService;
  private final WarehouseService warehouseService;
  private final MovementValidator movementValidator;

  public Optional<EquipmentUsage> findById(String id) {
    return equipmentUsageRepository.findById(id);
  }

  public Page<EquipmentUsage> findAll(PageFromOne page, BoundedPageSize pageSize, String jobId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return equipmentUsageRepository.findAll(SpecificationUtils.equal(jobId, "job", "id"), pageable);
  }

  public Page<EquipmentUsage> findAll(PageFromOne page, BoundedPageSize pageSize) {
    return findAll(page, pageSize, null);
  }

  @Transactional
  public List<EquipmentUsage> createOrUpdateAll(List<EquipmentUsage> usages) {
    movementValidator.validateEquipmentUsages(usages);
    List<EquipmentUsage> processed = new ArrayList<>();
    for (EquipmentUsage usage : usages) {
      EquipmentUsage existing =
          usage.getId() == null
              ? null
              : equipmentUsageRepository.findById(usage.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          usage, existing, usage.getId(), modificationUtils.takePrimaryUser());

      if (usage.getUsageStatus() != null) {
        boolean wasNull = existing == null || existing.getUsageStatus() == null;
        if (wasNull) {
          moveEquipmentToUsed(usage);
        }
      }
      processed.add(usage);
    }
    return equipmentUsageRepository.saveAll(processed);
  }

  @Transactional
  public EquipmentUsage returnEquipment(String id, EquipmentUsage.UsageStatus status) {
    EquipmentUsage usage =
        equipmentUsageRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("EquipmentUsage with id " + id + " not found"));

    usage.setUsageStatus(status);
    modificationUtils.createOrUpdateModel(
        usage, usage, usage.getId(), modificationUtils.takePrimaryUser());

    var equipment = usage.getEquipment();
    if (equipment == null) {
      throw new BadRequestException("Equipment usage has no equipment");
    }

    switch (status) {
      case RETURNED -> {
        Warehouse source = usage.getSourceLocation();
        if (source != null) {
          equipment.setWarehouse(source);
          equipmentService.createOrUpdateAll(List.of(equipment));
        }
      }
      case LOST -> {
        Warehouse unfindable =
            warehouseService.findById(SpecialWarehouseUtils.unfindableWarehouseId()).orElse(null);
        if (unfindable != null) {
          equipment.setWarehouse(unfindable);
          equipmentService.createOrUpdateAll(List.of(equipment));
        }
      }
      case BROKEN -> {
        Warehouse source = usage.getSourceLocation();
        if (source != null) {
          equipment.setWarehouse(source);
        }
        equipment.setEstEnPanne(true);
        equipmentService.createOrUpdateAll(List.of(equipment));
      }
    }

    return equipmentUsageRepository.save(usage);
  }

  @Transactional
  public void deleteById(String id) {
    equipmentUsageRepository.deleteById(id);
  }

  private void moveEquipmentToUsed(EquipmentUsage usage) {
    var used = warehouseService.findById(SpecialWarehouseUtils.usedWarehouseId()).orElse(null);
    if (usage.getEquipment() == null || used == null) {
      return;
    }
    var equipment = usage.getEquipment();
    equipment.setWarehouse(used);
    equipmentService.createOrUpdateAll(List.of(equipment));
  }
}
