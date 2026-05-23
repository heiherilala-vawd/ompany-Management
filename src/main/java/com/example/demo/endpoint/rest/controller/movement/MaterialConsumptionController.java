package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateMaterialConsumption;
import com.example.demo.client.model.MaterialConsumption;
import com.example.demo.endpoint.rest.mapper.movement.MaterialConsumptionMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.MaterialConsumptionService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MaterialConsumptionController {

  private final MaterialConsumptionService materialConsumptionService;
  private final MaterialConsumptionMapper materialConsumptionMapper;

  @GetMapping("/companies/{comp_id}/material-consumption/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public MaterialConsumption getMaterialConsumptionById(@PathVariable String comp_id, @PathVariable String id) {
    return materialConsumptionMapper.toRestMaterialConsumption(
        materialConsumptionService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("MaterialConsumption with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/material-consumption")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<MaterialConsumption> getMaterialConsumptions(
      @PathVariable String comp_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return materialConsumptionMapper.toRestMaterialConsumptions(
        materialConsumptionService.findAll(page, pageSize).getContent());
  }

  @PutMapping("/companies/{comp_id}/material-consumption")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<MaterialConsumption> crupdateMaterialConsumptions(
      @PathVariable String comp_id, @RequestBody List<CrupdateMaterialConsumption> toWrite) {
    List<com.example.demo.model.movement.MaterialConsumption> saved =
        materialConsumptionService.createOrUpdateAll(
            toWrite.stream().map(materialConsumptionMapper::toDomain).toList());
    return materialConsumptionMapper.toRestMaterialConsumptions(saved);
  }

  @DeleteMapping("/companies/{comp_id}/material-consumption/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteMaterialConsumptionById(@PathVariable String comp_id, @PathVariable String id) {
    materialConsumptionService.deleteById(id);
  }
}
