package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateEquipment;
import com.example.demo.client.model.Equipment;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.movement.WarehouseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EquipmentMapper {

  private final WarehouseService warehouseService;
  private final WarehouseMapper warehouseMapper;

  public com.example.demo.model.movement.Equipment toDomain(Equipment restEquipment) {
    if (restEquipment == null) return null;

    return com.example.demo.model.movement.Equipment.builder()
        .id(restEquipment.getId())
        .name(restEquipment.getName())
        .description(restEquipment.getDescription())
        .warehouse(
            restEquipment.getWarehouse() != null && restEquipment.getWarehouse().getId() != null
                ? warehouseService.findById(restEquipment.getWarehouse().getId()).orElse(null)
                : null)
        .floorNumber(restEquipment.getFloorNumber())
        .storageNumber(restEquipment.getStorageNumber())
        .comment(restEquipment.getComment())
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
        .comment(restEquipment.getComment())
        .build();
  }

  public Equipment toRestEquipment(com.example.demo.model.movement.Equipment domainEquipment) {
    if (domainEquipment == null) return null;

    Equipment restEquipment = new Equipment();
    restEquipment.setId(domainEquipment.getId());
    restEquipment.setName(domainEquipment.getName());
    restEquipment.setDescription(domainEquipment.getDescription());
    restEquipment.setWarehouse(
        warehouseMapper.toRestCrupdateWarehouse(domainEquipment.getWarehouse()));
    restEquipment.setFloorNumber(domainEquipment.getFloorNumber());
    restEquipment.setStorageNumber(domainEquipment.getStorageNumber());
    RestAuditMapperUtils.mapAuditFields(
        domainEquipment,
        restEquipment::setCreatedAt,
        restEquipment::setUpdatedAt,
        restEquipment::setComment,
        restEquipment::setCreatedBy,
        restEquipment::setUpdatedBy);

    return restEquipment;
  }

  public CrupdateEquipment toRestCrupdateEquipment(
      com.example.demo.model.movement.Equipment domainEquipment) {
    if (domainEquipment == null) return null;

    return new CrupdateEquipment()
        .id(domainEquipment.getId())
        .name(domainEquipment.getName())
        .description(domainEquipment.getDescription())
        .warehouseId(
            domainEquipment.getWarehouse() != null ? domainEquipment.getWarehouse().getId() : null)
        .floorNumber(domainEquipment.getFloorNumber())
        .storageNumber(domainEquipment.getStorageNumber())
        .comment(domainEquipment.getComment());
  }

  public List<Equipment> toRestEquipmentList(
      List<com.example.demo.model.movement.Equipment> domainEquipment) {
    return domainEquipment.stream().map(this::toRestEquipment).toList();
  }

  public List<com.example.demo.model.movement.Equipment> toDomain(List<Equipment> restEquipment) {
    return restEquipment.stream().map(this::toDomain).toList();
  }
}
