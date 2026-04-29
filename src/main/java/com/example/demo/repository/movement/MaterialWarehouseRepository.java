package com.example.demo.repository.movement;

import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.MaterialWarehouseId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialWarehouseRepository
    extends JpaRepository<MaterialWarehouse, MaterialWarehouseId> {

  Optional<MaterialWarehouse> findByMaterial_IdAndWarehouse_Id(
      String materialId, String warehouseId);

  // Option 1 : Recherche par les IDs depuis l'ID composé
  Optional<MaterialWarehouse> findById_MaterialIdAndId_WarehouseId(
      String materialId, String warehouseId);

  // Option 3 : Recherche par l'ID composé directement
  default Optional<MaterialWarehouse> findByMaterialAndWarehouse(
      String materialId, String warehouseId) {
    MaterialWarehouseId id = new MaterialWarehouseId(materialId, warehouseId);
    return findById(id);
  }
}
