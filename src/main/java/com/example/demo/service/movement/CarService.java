package com.example.demo.service.movement;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;
import static com.example.demo.repository.specification.SpecificationUtils.greaterThanOrEqualTo;
import static com.example.demo.repository.specification.SpecificationUtils.lessThanOrEqualTo;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.CarCriteria;
import com.example.demo.model.movement.Car;
import com.example.demo.model.movement.CarId;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.movement.CarRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
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
  private final WarehouseService warehouseService;
  private final EquipmentService equipmentService;
  private final ModificationUtils modificationUtils;

  public Optional<Car> findById(CarId id) {
    return carRepository.findById(id);
  }

  public Optional<Car> findByEquipmentIdAndWarehouseId(String equipmentId, String warehouseId) {
    return carRepository.findByEquipmentIdAndWarehouseId(equipmentId, warehouseId);
  }

  public Page<Car> findAll(PageFromOne page, BoundedPageSize pageSize, CarCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return carRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<Car> createOrUpdateAll(List<CarDomainParts> partsList) {
    List<Car> processedCars = new ArrayList<>();
    for (CarDomainParts parts : partsList) {
      Warehouse savedWarehouse =
          warehouseService.createOrUpdateAll(List.of(parts.warehouse())).get(0);

      Equipment savedEquipment =
          equipmentService.createOrUpdateAll(List.of(parts.equipment())).get(0);

      CarId carId = new CarId(savedEquipment.getId(), savedWarehouse.getId());

      Car car = parts.car();
      car.setId(carId);
      car.setWarehouse(savedWarehouse);
      car.setEquipment(savedEquipment);

      Car existingCar = carRepository.findById(carId).orElse(null);

      modificationUtils.createOrUpdateModel(
          car, existingCar, carId.toString(), modificationUtils.takePrimaryUser());

      processedCars.add(car);
    }
    return carRepository.saveAll(processedCars);
  }

  @Transactional
  public void deleteByEquipmentIdAndWarehouseId(String equipmentId, String warehouseId) {
    carRepository.deleteById(new CarId(equipmentId, warehouseId));
  }

  private Specification<Car> toSpecification(CarCriteria criteria) {
    return Specification.<Car>where(equal(criteria.getEquipmentId(), "equipment", "id"))
        .and(equal(criteria.getWarehouseId(), "warehouse", "id"))
        .and(containsIgnoreCase(criteria.getImmatriculation(), "immatriculation"))
        .and(equal(criteria.getTypeCarburant(), "typeCarburant"))
        .and(containsIgnoreCase(criteria.getMarque(), "marque"))
        .and(containsIgnoreCase(criteria.getModele(), "modele"))
        .and(equal(criteria.getAnnee(), "annee"))
        .and(containsIgnoreCase(criteria.getCouleur(), "couleur"))
        .and(greaterThanOrEqualTo(criteria.getKilometrageMin(), "kilometrage"))
        .and(lessThanOrEqualTo(criteria.getKilometrageMax(), "kilometrage"))
        .and(equal(criteria.getStatut(), "statut"));
  }
}
