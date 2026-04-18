package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateEquipment;
import com.example.demo.client.model.Equipment;
import com.example.demo.service.movement.WarehouseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EquipmentMapper {

  private final WarehouseService warehouseService;

  public com.example.demo.model.movement.Equipment toDomain(Equipment restEquipment) {
    if (restEquipment == null) return null;

    return com.example.demo.model.movement.Equipment.builder()
        .id(restEquipment.getId())
        .name(restEquipment.getName())
        .description(restEquipment.getDescription())
        .warehouse(
            restEquipment.getWarehouseId() != null
                ? warehouseService.findById(restEquipment.getWarehouseId()).orElse(null)
                : null)
        .floorNumber(restEquipment.getFloorNumber())
        .storageNumber(restEquipment.getStorageNumber())
        .build();
  }

  public com.example.demo.model.movement.Equipment toDomain(CrupdateEquipment restEquipment) {
    if (restEquipment == null) return null;

    return com.example.demo.model.movement.Equipment.builder()
        .id(restEquipment.getId())
        .name(restEquipment.getName())
        .description(restEquipment.getDescription())
        .warehouse(
            restEquipment.getWarehouseId() != null
                ? warehouseService.findById(restEquipment.getWarehouseId()).orElse(null)
                : null)
        .floorNumber(restEquipment.getFloorNumber())
        .storageNumber(restEquipment.getStorageNumber())
        .build();
  }

  public Equipment toRestEquipment(com.example.demo.model.movement.Equipment domainEquipment) {
    if (domainEquipment == null) return null;

    Equipment restEquipment = new Equipment();
    restEquipment.setId(domainEquipment.getId());
    restEquipment.setName(domainEquipment.getName());
    restEquipment.setDescription(domainEquipment.getDescription());
    restEquipment.setWarehouseId(
        domainEquipment.getWarehouse() != null ? domainEquipment.getWarehouse().getId() : null);
    restEquipment.setFloorNumber(domainEquipment.getFloorNumber());
    restEquipment.setStorageNumber(domainEquipment.getStorageNumber());
    restEquipment.setCreatedAt(domainEquipment.getCreatedAt());
    restEquipment.setUpdatedAt(domainEquipment.getUpdatedAt());
    restEquipment.setComment(domainEquipment.getComment());

    if (domainEquipment.getCreatedBy() != null) {
      restEquipment.setCreatedBy(domainEquipment.getCreatedBy().getId());
    }
    if (domainEquipment.getUpdatedBy() != null) {
      restEquipment.setUpdatedBy(domainEquipment.getUpdatedBy().getId());
    }

    return restEquipment;
  }

  public List<Equipment> toRestEquipmentList(
      List<com.example.demo.model.movement.Equipment> domainEquipment) {
    return domainEquipment.stream().map(this::toRestEquipment).toList();
  }

  public List<com.example.demo.model.movement.Equipment> toDomain(List<Equipment> restEquipment) {
    return restEquipment.stream().map(this::toDomain).toList();
  }
}
