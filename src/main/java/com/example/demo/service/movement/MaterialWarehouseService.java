package com.example.demo.service.movement;

import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.MaterialWarehouseId;
import com.example.demo.repository.movement.MaterialWarehouseRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialWarehouseService {

  private final MaterialWarehouseRepository materialWarehouseRepository;

  @Transactional
  public List<MaterialWarehouse> incrementQuantities(List<MaterialWarehouse> materialWarehouses) {
    return materialWarehouses.stream().map(this::incrementQuantity).toList();
  }

  @Transactional
  public MaterialWarehouse incrementQuantity(MaterialWarehouse materialWarehouse) {
    String materialId = materialWarehouse.getMaterial().getId();
    String warehouseId = materialWarehouse.getWarehouse().getId();

    MaterialWarehouse existing =
        materialWarehouseRepository
            .findByMaterial_IdAndWarehouse_Id(materialId, warehouseId)
            .orElse(null);

    if (existing == null) {
      materialWarehouse.setId(new MaterialWarehouseId(materialId, warehouseId));
      return materialWarehouseRepository.save(materialWarehouse);
    }

    existing.setQuantity(existing.getQuantity() + materialWarehouse.getQuantity());
    return materialWarehouseRepository.save(existing);
  }

  public Optional<MaterialWarehouse> findByMaterialAndWarehouse(
      String materialId, String warehouseId) {
    return materialWarehouseRepository.findByMaterial_IdAndWarehouse_Id(materialId, warehouseId);
  }

  @Transactional
  public MaterialWarehouse decrementQuantity(MaterialWarehouse materialWarehouse) {
    String materialId = materialWarehouse.getMaterial().getId();
    String warehouseId = materialWarehouse.getWarehouse().getId();

    MaterialWarehouse existing =
        materialWarehouseRepository
            .findByMaterial_IdAndWarehouse_Id(materialId, warehouseId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "MaterialWarehouse with material "
                            + materialId
                            + " and warehouse "
                            + warehouseId
                            + " not found"));

    int quantityToRemove =
        materialWarehouse.getQuantity() != null ? materialWarehouse.getQuantity() : 0;
    if (quantityToRemove <= 0) {
      throw new BadRequestException("quantity to decrement must be greater than zero");
    }
    if (existing.getQuantity() == null || existing.getQuantity() < quantityToRemove) {
      throw new BadRequestException("insufficient quantity in source warehouse");
    }

    existing.setQuantity(existing.getQuantity() - quantityToRemove);
    return materialWarehouseRepository.save(existing);
  }
}
