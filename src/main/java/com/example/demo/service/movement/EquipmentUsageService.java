package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.ForbiddenException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.movement.EquipmentIncident;
import com.example.demo.model.movement.EquipmentUsage;
import com.example.demo.model.movement.IncidentType;
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
import org.springframework.data.jpa.domain.Specification;
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
  private final EquipmentIncidentService equipmentIncidentService;

  public Optional<EquipmentUsage> findById(String id) {
    Optional<EquipmentUsage> usage = equipmentUsageRepository.findById(id);
    usage.ifPresent(this::validateRestrictedUserAccess);
    return usage;
  }

  public Page<EquipmentUsage> findAll(PageFromOne page, BoundedPageSize pageSize, String jobId) {
    User currentUser = modificationUtils.takePrimaryUser();
    Specification<EquipmentUsage> spec = SpecificationUtils.equal(jobId, "job", "id");
    if (isRestrictedUser(currentUser)) {
      spec = spec.and(SpecificationUtils.equal(currentUser.getId(), "usedBy", "id"));
    }
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return equipmentUsageRepository.findAll(spec, pageable);
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
        }
        equipment.setIsDamaged(false);
        equipment.setIsLost(false);
        equipment.setEstEnPanne(false);
        equipmentService.createOrUpdateAll(List.of(equipment));
      }
      case LOST -> {
        equipment.setIsLost(true);
        equipmentService.createOrUpdateAll(List.of(equipment));
        String incidentId =
            "lost_" + usage.getEquipment().getId() + "_" + System.currentTimeMillis();
        EquipmentIncident incident =
            EquipmentIncident.builder()
                .id(incidentId)
                .incidentType(IncidentType.LOST)
                .equipment(equipment)
                .user(modificationUtils.takePrimaryUser())
                .location(
                    usage.getSourceLocation() != null ? usage.getSourceLocation().getId() : null)
                .build();
        equipmentIncidentService.createOrUpdateAll(List.of(incident));
      }
      case BROKEN -> {
        Warehouse source = usage.getSourceLocation();
        if (source != null) {
          equipment.setWarehouse(source);
        }
        equipment.setEstEnPanne(true);
        equipment.setIsDamaged(true);
        equipmentService.createOrUpdateAll(List.of(equipment));
        String incidentId =
            "damaged_" + usage.getEquipment().getId() + "_" + System.currentTimeMillis();
        EquipmentIncident incident =
            EquipmentIncident.builder()
                .id(incidentId)
                .incidentType(IncidentType.DAMAGED)
                .equipment(equipment)
                .user(modificationUtils.takePrimaryUser())
                .location(
                    usage.getSourceLocation() != null ? usage.getSourceLocation().getId() : null)
                .build();
        equipmentIncidentService.createOrUpdateAll(List.of(incident));
      }
    }

    return equipmentUsageRepository.save(usage);
  }

  @Transactional
  public void deleteById(String id) {
    equipmentUsageRepository.deleteById(id);
  }

  private void validateRestrictedUserAccess(EquipmentUsage usage) {
    User currentUser = modificationUtils.takePrimaryUser();
    if (isRestrictedUser(currentUser)) {
      if (usage.getUsedBy() == null || !usage.getUsedBy().getId().equals(currentUser.getId())) {
        throw new ForbiddenException("Equipment usage not associated with the user");
      }
    }
  }

  private boolean isRestrictedUser(User user) {
    return user.getRole() == User.Role.EMPLOYEE || user.getRole() == User.Role.WAREHOUSE_WORKER;
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
