package com.example.demo.validator;

import com.example.demo.client.model.ConfirmEquipmentArrival;
import com.example.demo.client.model.ConfirmMaterialArrival;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.movement.Car;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.EquipmentUsage;
import com.example.demo.model.movement.Maintenance;
import com.example.demo.model.movement.Material;
import com.example.demo.model.movement.MaterialConsumption;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.TravelContainer;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.TravelPeople;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.movement.CarRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@lombok.AllArgsConstructor
public class MovementValidator {

  private final CarRepository carRepository;

  public void validateMaterial(Material material) {
    List<String> errors = new ArrayList<>();
    if (material == null) {
      errors.add("Material cannot be null");
    }
    if (material != null && (material.getName() == null || material.getName().isBlank())) {
      errors.add("Material name is mandatory");
    }
    if (material != null && material.getUnit() == null) {
      errors.add("Material unit is mandatory");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateMaterials(List<Material> materials) {
    List<String> errors = new ArrayList<>();
    if (materials == null || materials.isEmpty()) {
      errors.add("Material list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    materials.forEach(this::validateMaterial);
  }

  public void validateEquipment(Equipment equipment) {
    List<String> errors = new ArrayList<>();
    if (equipment == null) {
      errors.add("Equipment cannot be null");
    }
    if (equipment != null && (equipment.getName() == null || equipment.getName().isBlank())) {
      errors.add("Equipment name is mandatory");
    }
    if (equipment != null
        && (equipment.getWarehouse() == null || equipment.getWarehouse().getId() == null)) {
      errors.add("Equipment must be associated with a warehouse");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateEquipments(List<Equipment> equipments) {
    List<String> errors = new ArrayList<>();
    if (equipments == null || equipments.isEmpty()) {
      errors.add("Equipment list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    equipments.forEach(this::validateEquipment);
  }

  public void validateMaintenance(Maintenance maintenance) {
    List<String> errors = new ArrayList<>();
    if (maintenance == null) {
      errors.add("Maintenance cannot be null");
    }
    if (maintenance != null
        && (maintenance.getExpense() == null || maintenance.getExpense().getId() == null)) {
      errors.add("Maintenance must be linked to an expense");
    }
    if (maintenance != null
        && maintenance.getExpense() != null
        && maintenance.getExpense().getAmount() != null
        && maintenance.getExpense().getAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
      errors.add("Maintenance expense amount must be non-negative");
    }
    if (maintenance != null
        && (maintenance.getDescription() == null || maintenance.getDescription().isBlank())) {
      errors.add("Maintenance description is mandatory");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateMaintenances(List<Maintenance> maintenances) {
    List<String> errors = new ArrayList<>();
    if (maintenances == null || maintenances.isEmpty()) {
      errors.add("Maintenance list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    maintenances.forEach(this::validateMaintenance);
  }

  public void validateWarehouse(Warehouse warehouse) {
    List<String> errors = new ArrayList<>();
    if (warehouse == null) {
      errors.add("Warehouse cannot be null");
    }
    if (warehouse != null && (warehouse.getName() == null || warehouse.getName().isBlank())) {
      errors.add("Warehouse name is mandatory");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateWarehouses(List<Warehouse> warehouses) {
    List<String> errors = new ArrayList<>();
    if (warehouses == null || warehouses.isEmpty()) {
      errors.add("Warehouse list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    warehouses.forEach(this::validateWarehouse);
  }

  public void validateTravelEquipment(TravelEquipment travelEquipment) {
    List<String> errors = new ArrayList<>();
    if (travelEquipment == null) {
      errors.add("Travel equipment cannot be null");
    }
    if (travelEquipment != null
        && (travelEquipment.getTravel() == null || travelEquipment.getTravel().getId() == null)) {
      errors.add("Travel equipment must be linked to a travel expense");
    }
    if (travelEquipment != null
        && (travelEquipment.getEquipment() == null
            || travelEquipment.getEquipment().getId() == null)) {
      errors.add("Equipment is mandatory for travel equipment");
    }
    if (travelEquipment != null
        && (travelEquipment.getQuantity() == null || travelEquipment.getQuantity() <= 0)) {
      errors.add("Quantity must be positive");
    }
    if (travelEquipment != null && travelEquipment.getStatus() == null) {
      errors.add("Transport status is mandatory");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateTravelEquipments(List<TravelEquipment> travelEquipments) {
    List<String> errors = new ArrayList<>();
    if (travelEquipments == null || travelEquipments.isEmpty()) {
      errors.add("Travel equipment list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    travelEquipments.forEach(this::validateTravelEquipment);
  }

  public void validateTravelMaterials(TravelMaterials travelMaterials) {
    List<String> errors = new ArrayList<>();
    if (travelMaterials == null) {
      errors.add("Travel materials cannot be null");
    }
    if (travelMaterials != null
        && (travelMaterials.getTravel() == null || travelMaterials.getTravel().getId() == null)) {
      errors.add("Travel materials must be linked to a travel expense");
    }
    if (travelMaterials != null
        && (travelMaterials.getMaterial() == null
            || travelMaterials.getMaterial().getId() == null)) {
      errors.add("Material is mandatory for travel materials");
    }
    if (travelMaterials != null
        && (travelMaterials.getQuantity() == null || travelMaterials.getQuantity() <= 0)) {
      errors.add("Quantity must be positive");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateTravelMaterialsList(List<TravelMaterials> travelMaterialsList) {
    List<String> errors = new ArrayList<>();
    if (travelMaterialsList == null || travelMaterialsList.isEmpty()) {
      errors.add("Travel materials list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    travelMaterialsList.forEach(this::validateTravelMaterials);
  }

  public void validateTravelPeople(TravelPeople travelPeople) {
    List<String> errors = new ArrayList<>();
    if (travelPeople == null) {
      errors.add("Travel people cannot be null");
    }
    if (travelPeople != null
        && (travelPeople.getTravel() == null || travelPeople.getTravel().getId() == null)) {
      errors.add("Travel people must be linked to a travel expense");
    }
    if (travelPeople != null
        && (travelPeople.getUser() == null || travelPeople.getUser().getId() == null)) {
      errors.add("User is mandatory for travel people");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateTravelPeoples(List<TravelPeople> travelPeoples) {
    List<String> errors = new ArrayList<>();
    if (travelPeoples == null || travelPeoples.isEmpty()) {
      errors.add("Travel people list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    travelPeoples.forEach(this::validateTravelPeople);
  }

  public void validateTravelContainer(TravelContainer container) {
    List<String> errors = new ArrayList<>();
    if (container == null) {
      errors.add("Travel container cannot be null");
    }
    if (container != null
        && (container.getTravel() == null || container.getTravel().getId() == null)) {
      errors.add("Travel container must be linked to a travel expense");
    }
    if (container != null
        && (container.getName() == null || container.getName().isBlank())) {
      errors.add("Container name is mandatory");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateTravelContainers(List<TravelContainer> containers) {
    if (containers == null || containers.isEmpty()) {
      return;
    }
    containers.forEach(this::validateTravelContainer);
  }

  public void validateMaterialConsumption(MaterialConsumption consumption) {
    List<String> errors = new ArrayList<>();
    if (consumption == null) {
      errors.add("Material consumption cannot be null");
    }
    if (consumption != null
        && (consumption.getMaterial() == null || consumption.getMaterial().getId() == null)) {
      errors.add("Material is mandatory");
    }
    if (consumption != null && consumption.getQuantity() == null) {
      errors.add("Quantity is mandatory");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateMaterialConsumptions(List<MaterialConsumption> consumptions) {
    List<String> errors = new ArrayList<>();
    if (consumptions == null || consumptions.isEmpty()) {
      errors.add("Material consumption list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    consumptions.forEach(this::validateMaterialConsumption);
  }

  public void validateEquipmentUsage(EquipmentUsage usage) {
    List<String> errors = new ArrayList<>();
    if (usage == null) {
      errors.add("Equipment usage cannot be null");
    }
    if (usage != null && (usage.getEquipment() == null || usage.getEquipment().getId() == null)) {
      errors.add("Equipment is mandatory");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateEquipmentUsages(List<EquipmentUsage> usages) {
    List<String> errors = new ArrayList<>();
    if (usages == null || usages.isEmpty()) {
      errors.add("Equipment usage list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    usages.forEach(this::validateEquipmentUsage);
  }

  public void validateMaterialWarehouse(MaterialWarehouse materialWarehouse) {
    List<String> errors = new ArrayList<>();
    if (materialWarehouse == null) {
      errors.add("Material warehouse cannot be null");
    }
    if (materialWarehouse != null
        && (materialWarehouse.getMaterial() == null
            || materialWarehouse.getMaterial().getId() == null)) {
      errors.add("Material is mandatory for material warehouse");
    }
    if (materialWarehouse != null
        && (materialWarehouse.getWarehouse() == null
            || materialWarehouse.getWarehouse().getId() == null)) {
      errors.add("Warehouse is mandatory for material warehouse");
    }
    if (materialWarehouse != null
        && (materialWarehouse.getQuantity() == null || materialWarehouse.getQuantity() < 0)) {
      errors.add("Quantity must be non-negative");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateConfirmEquipmentArrival(ConfirmEquipmentArrival arrival) {
    List<String> errors = new ArrayList<>();
    if (arrival == null) {
      errors.add("Confirm equipment arrival cannot be null");
    }
    if (arrival != null && (arrival.getId() == null || arrival.getId().isBlank())) {
      errors.add("Travel equipment id is mandatory");
    }
    if (arrival != null && arrival.getStatus() == null) {
      errors.add("Transport status is mandatory");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateConfirmEquipmentArrival(List<ConfirmEquipmentArrival> arrivals) {
    List<String> errors = new ArrayList<>();
    if (arrivals == null || arrivals.isEmpty()) {
      errors.add("Confirm equipment arrival list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    arrivals.forEach(this::validateConfirmEquipmentArrival);
  }

  public void validateConfirmMaterialArrival(ConfirmMaterialArrival arrival) {
    List<String> errors = new ArrayList<>();
    if (arrival == null) {
      errors.add("Confirm material arrival cannot be null");
    }
    if (arrival != null && (arrival.getId() == null || arrival.getId().isBlank())) {
      errors.add("Travel materials id is mandatory");
    }
    if (arrival != null && (arrival.getLogId() == null || arrival.getLogId().isBlank())) {
      errors.add("Log id is mandatory");
    }
    if (arrival != null
        && arrival.getQuantityReceived() != null
        && arrival.getQuantityReceived() < 0) {
      errors.add("Quantity received must be non-negative");
    }
    if (arrival != null && arrival.getQuantityLost() != null && arrival.getQuantityLost() < 0) {
      errors.add("Quantity lost must be non-negative");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateConfirmMaterialArrival(List<ConfirmMaterialArrival> arrivals) {
    List<String> errors = new ArrayList<>();
    if (arrivals == null || arrivals.isEmpty()) {
      errors.add("Confirm material arrival list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    arrivals.forEach(this::validateConfirmMaterialArrival);
  }

  public void validateCar(Car car) {
    List<String> errors = new ArrayList<>();
    if (car == null) {
      errors.add("Car cannot be null");
    }
    if (car != null && (car.getId() == null || car.getId().getEquipmentId() == null)) {
      errors.add("Equipment ID is mandatory");
    }
    if (car != null && (car.getId() == null || car.getId().getWarehouseId() == null)) {
      errors.add("Warehouse ID is mandatory");
    }
    if (car != null
        && (car.getEquipment() == null
            || car.getEquipment().getName() == null
            || car.getEquipment().getName().isBlank())) {
      errors.add("Equipment name is mandatory");
    }
    if (car != null
        && (car.getWarehouse() == null
            || car.getWarehouse().getName() == null
            || car.getWarehouse().getName().isBlank())) {
      errors.add("Warehouse name is mandatory");
    }
    if (car != null && car.getFuelType() == null) {
      errors.add("Fuel type is mandatory");
    }
    if (car != null && car.getStatus() == null) {
      errors.add("Car status is mandatory");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateCars(List<Car> cars) {
    List<String> errors = new ArrayList<>();
    if (cars == null || cars.isEmpty()) {
      errors.add("Car list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    cars.forEach(this::validateCar);
  }

  public void validateCarCreation(Car car, CarRepository repository) {
    List<String> errors = new ArrayList<>();
    if (car != null
        && car.getEquipment() != null
        && car.getEquipment().getId() != null
        && repository.existsByEquipment_Id(car.getEquipment().getId())) {
      errors.add("Equipment " + car.getEquipment().getId() + " is already associated with a car");
    }
    if (car != null
        && car.getWarehouse() != null
        && car.getWarehouse().getId() != null
        && repository.existsByWarehouse_Id(car.getWarehouse().getId())) {
      errors.add("Warehouse " + car.getWarehouse().getId() + " is already associated with a car");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }
}
