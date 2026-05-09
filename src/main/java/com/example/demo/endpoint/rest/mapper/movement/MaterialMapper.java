package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.Material;
import com.example.demo.client.model.MaterialWarehouseInfo;
import com.example.demo.client.model.MaterialWarehouseView;
import com.example.demo.endpoint.rest.mapper.EnumMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MaterialMapper {

  private final WarehouseMapper warehouseMapper;

  public com.example.demo.model.movement.Material toDomain(Material restMaterial) {
    if (restMaterial == null) return null;

    return com.example.demo.model.movement.Material.builder()
        .id(restMaterial.getId())
        .name(restMaterial.getName())
        .description(restMaterial.getDescription())
        .unit(
            EnumMapper.mapEnum(
                restMaterial.getUnit(), com.example.demo.model.movement.Material.Unit.class))
        .comment(restMaterial.getComment())
        .build();
  }

  public com.example.demo.model.movement.Material toDomain(CrupdateMaterial restMaterial) {
    if (restMaterial == null) return null;

    return com.example.demo.model.movement.Material.builder()
        .id(restMaterial.getId())
        .name(restMaterial.getName())
        .description(restMaterial.getDescription())
        .unit(
            EnumMapper.mapEnum(
                restMaterial.getUnit(), com.example.demo.model.movement.Material.Unit.class))
        .comment(restMaterial.getComment())
        .build();
  }

  public Material toRestMaterial(com.example.demo.model.movement.Material domainMaterial) {
    if (domainMaterial == null) return null;

    Material restMaterial = new Material();
    restMaterial.setId(domainMaterial.getId());
    restMaterial.setName(domainMaterial.getName());
    restMaterial.setDescription(domainMaterial.getDescription());
    restMaterial.setUnit(
        EnumMapper.mapEnum(
            domainMaterial.getUnit(), com.example.demo.client.model.MaterialUnit.class));
    if (domainMaterial.getMaterialWarehouses() != null) {
      restMaterial.setMaterialWarehouses(
          domainMaterial.getMaterialWarehouses().stream()
              .map(this::toRestMaterialWarehouseInfo)
              .collect(Collectors.toList()));
    }
    RestAuditMapperUtils.mapAuditFields(
        domainMaterial,
        restMaterial::setCreatedAt,
        restMaterial::setUpdatedAt,
        restMaterial::setComment,
        restMaterial::setCreatedBy,
        restMaterial::setUpdatedBy);

    return restMaterial;
  }

  public MaterialWarehouseInfo toRestMaterialWarehouseInfo(
      com.example.demo.model.movement.MaterialWarehouse domain) {
    if (domain == null) return null;
    MaterialWarehouseInfo info = new MaterialWarehouseInfo();
    info.setWarehouse(warehouseMapper.toRestWarehouse(domain.getWarehouse()));
    info.setQuantity(domain.getQuantity());
    return info;
  }

  public List<MaterialWarehouseInfo> toRestMaterialWarehouseInfos(
      List<com.example.demo.model.movement.MaterialWarehouse> domainList) {
    return domainList.stream().map(this::toRestMaterialWarehouseInfo).toList();
  }

  public MaterialWarehouseView toRestMaterialWarehouseView(
      com.example.demo.model.movement.MaterialWarehouse domain) {
    if (domain == null) return null;
    MaterialWarehouseView view = new MaterialWarehouseView();
    view.setMaterial(toRestCrupdateMaterial(domain.getMaterial()));
    view.setWarehouse(warehouseMapper.toRestWarehouse(domain.getWarehouse()));
    view.setQuantity(domain.getQuantity());
    return view;
  }

  public List<MaterialWarehouseView> toRestMaterialWarehouseViews(
      List<com.example.demo.model.movement.MaterialWarehouse> domainList) {
    return domainList.stream().map(this::toRestMaterialWarehouseView).toList();
  }

  public CrupdateMaterial toRestCrupdateMaterial(
      com.example.demo.model.movement.Material domainMaterial) {
    if (domainMaterial == null) return null;

    return new CrupdateMaterial()
        .id(domainMaterial.getId())
        .name(domainMaterial.getName())
        .description(domainMaterial.getDescription())
        .unit(
            EnumMapper.mapEnum(
                domainMaterial.getUnit(), com.example.demo.client.model.MaterialUnit.class))
        .comment(domainMaterial.getComment());
  }

  public List<Material> toRestMaterials(
      List<com.example.demo.model.movement.Material> domainMaterials) {
    return domainMaterials.stream().map(this::toRestMaterial).toList();
  }

  public List<com.example.demo.model.movement.Material> toDomain(List<Material> restMaterials) {
    return restMaterials.stream().map(this::toDomain).toList();
  }
}
