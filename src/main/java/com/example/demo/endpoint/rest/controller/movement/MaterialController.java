package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.Material;
import com.example.demo.endpoint.rest.mapper.movement.MaterialMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.MaterialService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MaterialController {

  private final MaterialService materialService;
  private final MaterialMapper materialMapper;

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
      @RequestParam(name = "warehouse_id", required = false) String warehouseId) {

    return materialService.findAll(page, pageSize, warehouseId).stream()
        .map(materialMapper::toRestMaterial)
        .toList();
  }

  @PutMapping("/materials")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Material> crupdateMaterials(@RequestBody List<CrupdateMaterial> toWrite) {
    var saved =
        materialService.createOrUpdateAll(toWrite.stream().map(materialMapper::toDomain).toList());
    return saved.stream().map(materialMapper::toRestMaterial).toList();
  }

  @DeleteMapping("/materials/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteMaterialById(@PathVariable String id) {
    materialService.deleteById(id);
  }
}
