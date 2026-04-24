package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.Material;
import com.example.demo.endpoint.rest.mapper.EnumMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MaterialMapper {

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
    RestAuditMapperUtils.mapAuditFields(
        domainMaterial,
        restMaterial::setCreatedAt,
        restMaterial::setUpdatedAt,
        restMaterial::setComment,
        restMaterial::setCreatedBy,
        restMaterial::setUpdatedBy);

    return restMaterial;
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
