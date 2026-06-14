package com.example.demo.service.movement;

import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.movement.Car;
import com.example.demo.model.movement.CarId;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.movement.CarRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
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
public class CarService {

  private final CarRepository carRepository;
  private final EquipmentService equipmentService;
  private final WarehouseService warehouseService;
  private final ModificationUtils modificationUtils;
  private final MovementValidator movementValidator;

  public Optional<Car> findById(String equipmentId, String warehouseId) {
    return carRepository.findByEquipment_IdAndWarehouse_Id(equipmentId, warehouseId);
  }

  public Page<Car> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return carRepository.findAll(pageable);
  }

  @Transactional
  public List<Car> createOrUpdateAll(List<Car> carList) {
    movementValidator.validateCars(carList);
    var currentUser = modificationUtils.takePrimaryUser();
    List<Car> processedCars = new ArrayList<>();

    for (Car car : carList) {
      Car existingCar = carRepository.findById(car.getId()).orElse(null);

      if (existingCar == null) {
        movementValidator.validateCarCreation(car, carRepository);
        Equipment equipment = car.getEquipment();
        Warehouse warehouse = car.getWarehouse();

        warehouseService.createOrUpdateAll(List.of(warehouse));
        equipmentService.createOrUpdateAll(List.of(equipment));
      } else {
        Equipment existingEquipment = existingCar.getEquipment();
        Equipment updatedEquipment = car.getEquipment();
        updatedEquipment.setId(existingEquipment.getId());
        updatedEquipment.setCreatedAt(existingEquipment.getCreatedAt());
        updatedEquipment.setCreatedBy(existingEquipment.getCreatedBy());
        updatedEquipment.setWarehouse(existingEquipment.getWarehouse());
        equipmentService.createOrUpdateAll(List.of(updatedEquipment));

        Warehouse existingWarehouse = existingCar.getWarehouse();
        Warehouse updatedWarehouse = car.getWarehouse();
        updatedWarehouse.setId(existingWarehouse.getId());
        updatedWarehouse.setCreatedAt(existingWarehouse.getCreatedAt());
        updatedWarehouse.setCreatedBy(existingWarehouse.getCreatedBy());
        updatedWarehouse.setJob(existingWarehouse.getJob());
        warehouseService.createOrUpdateAll(List.of(updatedWarehouse));

        car.setEquipment(updatedEquipment);
        car.setWarehouse(updatedWarehouse);
      }

      modificationUtils.createOrUpdateModel(car, existingCar, car.getId().toString(), currentUser);
      processedCars.add(car);
    }

    return carRepository.saveAll(processedCars);
  }

  @Transactional
  public void deleteById(String equipmentId, String warehouseId) {
    CarId carId = new CarId(equipmentId, warehouseId);
    carRepository.deleteById(carId);
  }

  private Specification<Car> toSpecification(String equipmentId, String warehouseId) {
    return Specification.<Car>where(equal(equipmentId, "equipment", "id"))
        .and(equal(warehouseId, "warehouse", "id"));
  }
}
