package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.Material;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.movement.WarehouseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MaterialMapper {

  private final WarehouseService warehouseService;

  public com.example.demo.model.movement.Material toDomain(Material restMaterial) {
    if (restMaterial == null) return null;

    return com.example.demo.model.movement.Material.builder()
        .id(restMaterial.getId())
        .name(restMaterial.getName())
        .description(restMaterial.getDescription())
        .warehouse(
            restMaterial.getWarehouseId() != null
                ? warehouseService.findById(restMaterial.getWarehouseId()).orElse(null)
                : null)
        .floorNumber(restMaterial.getFloorNumber())
        .storageNumber(restMaterial.getStorageNumber())
        .comment(restMaterial.getComment())
        .build();
  }

  public com.example.demo.model.movement.Material toDomain(CrupdateMaterial restMaterial) {
    if (restMaterial == null) return null;

    return com.example.demo.model.movement.Material.builder()
        .id(restMaterial.getId())
        .name(restMaterial.getName())
        .description(restMaterial.getDescription())
        .warehouse(
            restMaterial.getWarehouseId() != null
                ? warehouseService.findById(restMaterial.getWarehouseId()).orElse(null)
                : null)
        .floorNumber(restMaterial.getFloorNumber())
        .storageNumber(restMaterial.getStorageNumber())
        .comment(restMaterial.getComment())
        .build();
  }

  public Material toRestMaterial(com.example.demo.model.movement.Material domainMaterial) {
    if (domainMaterial == null) return null;

    Material restMaterial = new Material();
    restMaterial.setId(domainMaterial.getId());
    restMaterial.setName(domainMaterial.getName());
    restMaterial.setDescription(domainMaterial.getDescription());
    restMaterial.setWarehouseId(
        domainMaterial.getWarehouse() != null ? domainMaterial.getWarehouse().getId() : null);
    restMaterial.setFloorNumber(domainMaterial.getFloorNumber());
    restMaterial.setStorageNumber(domainMaterial.getStorageNumber());
    RestAuditMapperUtils.mapAuditFields(
        domainMaterial,
        restMaterial::setCreatedAt,
        restMaterial::setUpdatedAt,
        restMaterial::setComment,
        restMaterial::setCreatedBy,
        restMaterial::setUpdatedBy);

    return restMaterial;
  }

  public List<Material> toRestMaterials(
      List<com.example.demo.model.movement.Material> domainMaterials) {
    return domainMaterials.stream().map(this::toRestMaterial).toList();
  }

  public List<com.example.demo.model.movement.Material> toDomain(List<Material> restMaterials) {
    return restMaterials.stream().map(this::toDomain).toList();
  }
}
