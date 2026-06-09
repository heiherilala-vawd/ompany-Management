package com.example.demo.repository.movement;

import com.example.demo.model.movement.Car;
import com.example.demo.model.movement.CarId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository
    extends JpaRepository<Car, CarId>, JpaSpecificationExecutor<Car> {

  Optional<Car> findByEquipmentIdAndWarehouseId(String equipmentId, String warehouseId);

  boolean existsByEquipmentIdAndWarehouseId(String equipmentId, String warehouseId);
}
