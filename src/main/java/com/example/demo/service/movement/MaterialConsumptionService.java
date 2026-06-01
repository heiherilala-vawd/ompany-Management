package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.movement.MaterialConsumption;
import com.example.demo.model.movement.MaterialConsumption.ConsumptionStatus;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.movement.MaterialConsumptionRepository;
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
public class MaterialConsumptionService {

  private final MaterialConsumptionRepository materialConsumptionRepository;
  private final ModificationUtils modificationUtils;
  private final MaterialWarehouseService materialWarehouseService;
  private final WarehouseService warehouseService;
  private final MovementValidator movementValidator;

  public Optional<MaterialConsumption> findById(String id) {
    return materialConsumptionRepository.findById(id);
  }

  public Page<MaterialConsumption> findAll(
      PageFromOne page,
      BoundedPageSize pageSize,
      ConsumptionStatus consumptionStatus,
      String jobId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    var spec =
        SpecificationUtils.<MaterialConsumption>equal(consumptionStatus, "consumptionStatus")
            .and(SpecificationUtils.equal(jobId, "job", "id"));
    return materialConsumptionRepository.findAll(spec, pageable);
  }

  public Page<MaterialConsumption> findAll(PageFromOne page, BoundedPageSize pageSize) {
    return findAll(page, pageSize, null, null);
  }

  @Transactional
  public List<MaterialConsumption> createOrUpdateAll(List<MaterialConsumption> consumptions) {
    movementValidator.validateMaterialConsumptions(consumptions);
    List<MaterialConsumption> processed = new ArrayList<>();
    for (MaterialConsumption consumption : consumptions) {
      MaterialConsumption existing =
          consumption.getId() == null
              ? null
              : materialConsumptionRepository.findById(consumption.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          consumption, existing, consumption.getId(), modificationUtils.takePrimaryUser());

      if (consumption.getConsumptionStatus() == ConsumptionStatus.COMPLETED) {
        boolean wasInProgress =
            existing != null && existing.getConsumptionStatus() == ConsumptionStatus.IN_PROGRESS;
        if (existing == null || wasInProgress) {
          moveToUsedWarehouse(consumption);
        }
      }
      processed.add(consumption);
    }
    return materialConsumptionRepository.saveAll(processed);
  }

  @Transactional
  public MaterialConsumption completeConsumption(String id) {
    MaterialConsumption consumption =
        materialConsumptionRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("MaterialConsumption with id " + id + " not found"));

    if (consumption.getConsumptionStatus() == ConsumptionStatus.COMPLETED) {
      return consumption;
    }

    MaterialConsumption existing =
        materialConsumptionRepository.findById(consumption.getId()).orElse(null);
    modificationUtils.createOrUpdateModel(
        consumption, existing, consumption.getId(), modificationUtils.takePrimaryUser());
    consumption.setConsumptionStatus(ConsumptionStatus.COMPLETED);
    moveToUsedWarehouse(consumption);
    return materialConsumptionRepository.save(consumption);
  }

  @Transactional
  public MaterialConsumption returnMaterials(String id, int returnedQuantity) {
    MaterialConsumption consumption =
        materialConsumptionRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("MaterialConsumption with id " + id + " not found"));

    if (returnedQuantity <= 0) {
      throw new BadRequestException("returned quantity must be positive");
    }

    Warehouse used =
        warehouseService.findById(SpecialWarehouseUtils.usedWarehouseId()).orElse(null);
    Warehouse source = consumption.getWarehouse();
    if (source == null || used == null) {
      throw new BadRequestException("Cannot return materials: missing warehouse");
    }

    MaterialWarehouse decrement = new MaterialWarehouse();
    decrement.setMaterial(consumption.getMaterial());
    decrement.setWarehouse(used);
    decrement.setQuantity(returnedQuantity);
    materialWarehouseService.decrementQuantity(decrement);

    MaterialWarehouse increment = new MaterialWarehouse();
    increment.setMaterial(consumption.getMaterial());
    increment.setWarehouse(source);
    increment.setQuantity(returnedQuantity);
    materialWarehouseService.incrementQuantity(increment);

    MaterialConsumption existing =
        materialConsumptionRepository.findById(consumption.getId()).orElse(null);
    modificationUtils.createOrUpdateModel(
        consumption, existing, consumption.getId(), modificationUtils.takePrimaryUser());
    consumption.setQuantity(consumption.getQuantity() - returnedQuantity);
    if (consumption.getQuantity() <= 0) {
      consumption.setConsumptionStatus(ConsumptionStatus.COMPLETED);
      consumption.setQuantity(0);
    }

    return materialConsumptionRepository.save(consumption);
  }

  @Transactional
  public void deleteById(String id) {
    materialConsumptionRepository.deleteById(id);
  }

  private void moveToUsedWarehouse(MaterialConsumption consumption) {
    Warehouse source = consumption.getWarehouse();
    Warehouse used =
        warehouseService.findById(SpecialWarehouseUtils.usedWarehouseId()).orElse(null);
    if (source == null || used == null || consumption.getQuantity() == null) {
      return;
    }
    MaterialWarehouse decrement = new MaterialWarehouse();
    decrement.setMaterial(consumption.getMaterial());
    decrement.setWarehouse(source);
    decrement.setQuantity(consumption.getQuantity());
    materialWarehouseService.decrementQuantity(decrement);
    MaterialWarehouse increment = new MaterialWarehouse();
    increment.setMaterial(consumption.getMaterial());
    increment.setWarehouse(used);
    increment.setQuantity(consumption.getQuantity());
    materialWarehouseService.incrementQuantity(increment);
  }
}
