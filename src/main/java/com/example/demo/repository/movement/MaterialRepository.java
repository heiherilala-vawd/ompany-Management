package com.example.demo.repository.movement;

import com.example.demo.model.movement.Material;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, String> {
  Page<Material> findByWarehouseId(String warehouseId, Pageable pageable);

  List<Material> findByWarehouseId(String warehouseId);

  Optional<Material> findByNameAndWarehouseId(String name, String warehouseId);

  List<Material> findByWarehouseIdOrderByName(String warehouseId);

  List<Material> findByFloorNumber(Integer floorNumber);

  boolean existsByNameAndWarehouseId(String name, String warehouseId);
}
