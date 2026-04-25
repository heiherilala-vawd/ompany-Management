package com.example.demo.service.movement;

import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.MaterialWarehouseId;
import com.example.demo.repository.movement.MaterialWarehouseRepository;
import java.util.List;
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
}
