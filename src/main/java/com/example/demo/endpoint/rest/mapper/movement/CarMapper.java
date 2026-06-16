package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.Car;
import com.example.demo.client.model.CrupdateCar;
import com.example.demo.model.movement.CarId;
import com.example.demo.model.movement.CarStatus;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.FuelType;
import com.example.demo.model.movement.Warehouse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {

  public com.example.demo.model.movement.Car toDomain(CrupdateCar dto) {
    if (dto == null) return null;

    CarId carId = new CarId(dto.getEquipmentId(), dto.getWarehouseId());

    Warehouse warehouse =
        Warehouse.builder()
            .id(dto.getWarehouseId())
            .name(dto.getWarehouseName())
            .description(dto.getWarehouseDescription())
            .build();

    Equipment equipment =
        Equipment.builder()
            .id(dto.getEquipmentId())
            .name(dto.getEquipmentName())
            .category(dto.getEquipmentCategory())
            .estEnPanne(dto.getEstEnPanne() != null ? dto.getEstEnPanne() : false)
            .purchasePrice(dto.getPurchasePrice())
            .purchaseDate(dto.getPurchaseDate())
            .warehouse(warehouse)
            .build();

    return com.example.demo.model.movement.Car.builder()
        .id(carId)
        .equipment(equipment)
        .warehouse(warehouse)
        .licensePlate(dto.getLicensePlate())
        .fuelType(dto.getFuelType() != null ? FuelType.valueOf(dto.getFuelType().name()) : null)
        .brand(dto.getBrand())
        .model(dto.getModel())
        .year(dto.getYear())
        .color(dto.getColor())
        .mileage(dto.getMileage())
        .status(dto.getStatus() != null ? CarStatus.valueOf(dto.getStatus().name()) : null)
        .comment(dto.getComment())
        .build();
  }

  public Car toResponse(com.example.demo.model.movement.Car car) {
    if (car == null) return null;

    Car response = new Car();
    response.setEquipmentId(car.getId().getEquipmentId());
    response.setWarehouseId(car.getId().getWarehouseId());
    response.setEquipmentName(car.getEquipment() != null ? car.getEquipment().getName() : null);
    response.setEquipmentCategory(car.getEquipment() != null ? car.getEquipment().getCategory() : null);
    response.setEstEnPanne(car.getEquipment() != null ? car.getEquipment().getEstEnPanne() : null);
    response.setPurchasePrice(car.getEquipment() != null ? car.getEquipment().getPurchasePrice() : null);
    response.setPurchaseDate(car.getEquipment() != null ? car.getEquipment().getPurchaseDate() : null);
    response.setWarehouseName(car.getWarehouse() != null ? car.getWarehouse().getName() : null);
    response.setWarehouseDescription(
        car.getWarehouse() != null ? car.getWarehouse().getDescription() : null);
    response.setLicensePlate(car.getLicensePlate());
    response.setFuelType(
        car.getFuelType() != null
            ? com.example.demo.client.model.FuelType.valueOf(car.getFuelType().name())
            : null);
    response.setStatus(
        car.getStatus() != null
            ? com.example.demo.client.model.CarStatus.valueOf(car.getStatus().name())
            : null);
    response.setBrand(car.getBrand());
    response.setModel(car.getModel());
    response.setYear(car.getYear());
    response.setColor(car.getColor());
    response.setMileage(car.getMileage());
    response.setComment(car.getComment());
    response.setCreatedAt(car.getCreatedAt());
    response.setUpdatedAt(car.getUpdatedAt());
    return response;
  }

  public List<Car> toResponseList(List<com.example.demo.model.movement.Car> cars) {
    return cars.stream().map(this::toResponse).toList();
  }
}
