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
}
