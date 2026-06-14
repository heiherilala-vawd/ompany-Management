package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.endpoint.rest.dto.movement.CarResponse;
import com.example.demo.endpoint.rest.dto.movement.CrupdateCar;
import com.example.demo.model.movement.Car;
import com.example.demo.model.movement.CarId;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Warehouse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {

  public Car toDomain(CrupdateCar dto) {
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

    return Car.builder()
        .id(carId)
        .equipment(equipment)
        .warehouse(warehouse)
        .licensePlate(dto.getLicensePlate())
        .fuelType(dto.getFuelType())
        .brand(dto.getBrand())
        .model(dto.getModel())
        .year(dto.getYear())
        .color(dto.getColor())
        .mileage(dto.getMileage())
        .status(dto.getStatus())
        .comment(dto.getComment())
        .build();
  }

  public CarResponse toResponse(Car car) {
    if (car == null) return null;

    return CarResponse.builder()
        .equipmentId(car.getId().getEquipmentId())
        .warehouseId(car.getId().getWarehouseId())
        .equipmentName(car.getEquipment() != null ? car.getEquipment().getName() : null)
        .equipmentCategory(car.getEquipment() != null ? car.getEquipment().getCategory() : null)
        .estEnPanne(car.getEquipment() != null ? car.getEquipment().getEstEnPanne() : null)
        .purchasePrice(car.getEquipment() != null ? car.getEquipment().getPurchasePrice() : null)
        .purchaseDate(car.getEquipment() != null ? car.getEquipment().getPurchaseDate() : null)
        .warehouseName(car.getWarehouse() != null ? car.getWarehouse().getName() : null)
        .warehouseDescription(
            car.getWarehouse() != null ? car.getWarehouse().getDescription() : null)
        .licensePlate(car.getLicensePlate())
        .fuelType(car.getFuelType())
        .status(car.getStatus())
        .brand(car.getBrand())
        .model(car.getModel())
        .year(car.getYear())
        .color(car.getColor())
        .mileage(car.getMileage())
        .comment(car.getComment())
        .createdAt(car.getCreatedAt())
        .updatedAt(car.getUpdatedAt())
        .build();
  }

  public List<CarResponse> toResponseList(List<Car> cars) {
    return cars.stream().map(this::toResponse).toList();
  }
}
