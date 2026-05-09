package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.CrupdateMaterialWarehouse;
import com.example.demo.client.model.Material;
import com.example.demo.client.model.MaterialUnit;
import com.example.demo.client.model.MaterialWarehouseInfo;
import com.example.demo.client.model.MaterialWarehouseView;
import com.example.demo.endpoint.rest.mapper.EnumMapper;
import com.example.demo.endpoint.rest.mapper.movement.MaterialMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.MaterialCriteria;
import com.example.demo.model.criteria.MaterialWarehouseCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.MaterialService;
import com.example.demo.service.movement.MaterialWarehouseService;
import com.example.demo.service.movement.WarehouseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MaterialController {

  private final MaterialService materialService;
  private final MaterialMapper materialMapper;
  private final MaterialWarehouseService materialWarehouseService;
  private final WarehouseService warehouseService;

  @GetMapping("/materials/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public Material getMaterialById(@PathVariable String id) {
    return materialMapper.toRestMaterial(
        materialService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Material with id " + id + " not found")));
  }

  @GetMapping("/materials")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Material> getMaterials(
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "unit", required = false) MaterialUnit unit,
      @RequestParam(name = "not_arrived", required = false) Boolean notArrived) {
    MaterialCriteria criteria = new MaterialCriteria();
    criteria.setName(name);
    criteria.setDescription(description);
    criteria.setUnit(EnumMapper.mapEnum(unit, com.example.demo.model.movement.Material.Unit.class));
    criteria.setNotArrived(notArrived);

    return materialService.findAll(page, pageSize, criteria).stream()
        .map(materialMapper::toRestMaterial)
        .toList();
  }

  @PutMapping("/materials")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Material> crupdateMaterials(@RequestBody List<CrupdateMaterial> toWrite) {
    List<com.example.demo.model.movement.Material> saved =
        materialService.createOrUpdateAll(toWrite.stream().map(materialMapper::toDomain).toList());
    return saved.stream().map(materialMapper::toRestMaterial).toList();
  }

  @DeleteMapping("/materials/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteMaterialById(@PathVariable String id) {
    materialService.deleteById(id);
  }

  @GetMapping("/companies/{comp_id}/material_warehouse")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<MaterialWarehouseView> getMaterialWarehouses(
      @PathVariable String comp_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "material_id", required = false) String materialId,
      @RequestParam(name = "warehouse_id", required = false) String warehouseId,
      @RequestParam(name = "not_arrived", required = false) Boolean notArrived) {
    MaterialWarehouseCriteria criteria = new MaterialWarehouseCriteria();
    criteria.setMaterialId(materialId);
    criteria.setWarehouseId(warehouseId);
    criteria.setNotArrived(notArrived);

    return materialMapper.toRestMaterialWarehouseViews(
        materialWarehouseService.findAll(page, pageSize, criteria).getContent());
  }

  @PutMapping("/companies/{comp_id}/material_warehouse")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<MaterialWarehouseInfo> crupdateMaterialWarehouses(
      @PathVariable String comp_id, @RequestBody List<CrupdateMaterialWarehouse> toWrite) {
    List<com.example.demo.model.movement.MaterialWarehouse> domainList =
        toWrite.stream()
            .map(
                cw -> {
                  com.example.demo.model.movement.Material material =
                      materialService
                          .findById(cw.getMaterialId())
                          .orElseThrow(
                              () ->
                                  new NotFoundException(
                                      "Material with id " + cw.getMaterialId() + " not found"));
                  com.example.demo.model.movement.Warehouse warehouse =
                      warehouseService
                          .findById(cw.getWarehouseId())
                          .orElseThrow(
                              () ->
                                  new NotFoundException(
                                      "Warehouse with id " + cw.getWarehouseId() + " not found"));
                  return com.example.demo.model.movement.MaterialWarehouse.builder()
                      .material(material)
                      .warehouse(warehouse)
                      .quantity(cw.getQuantity())
                      .build();
                })
            .toList();

    List<com.example.demo.model.movement.MaterialWarehouse> saved =
        materialWarehouseService.createOrUpdateAll(domainList);

    return saved.stream().map(materialMapper::toRestMaterialWarehouseInfo).toList();
  }
}
