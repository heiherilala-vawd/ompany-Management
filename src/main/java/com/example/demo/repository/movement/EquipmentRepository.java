package com.example.demo.repository.movement;

import com.example.demo.model.movement.Equipment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository
    extends JpaRepository<Equipment, String>, JpaSpecificationExecutor<Equipment> {
  Page<Equipment> findByWarehouseId(String warehouseId, Pageable pageable);

  List<Equipment> findByWarehouseId(String warehouseId);

  Optional<Equipment> findByNameAndWarehouseId(String name, String warehouseId);

  List<Equipment> findByWarehouseIdOrderByName(String warehouseId);

  List<Equipment> findByFloorNumber(Integer floorNumber);

  boolean existsByNameAndWarehouseId(String name, String warehouseId);
}
