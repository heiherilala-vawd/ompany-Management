package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateMaterialConsumption;
import com.example.demo.client.model.MaterialConsumption;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.movement.MaterialConsumptionMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.movement.MaterialConsumption.ConsumptionStatus;
import com.example.demo.service.movement.MaterialConsumptionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MaterialConsumptionController {

  private final MaterialConsumptionService materialConsumptionService;
  private final MaterialConsumptionMapper materialConsumptionMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/material_consumptions/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public MaterialConsumption getMaterialConsumptionById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return materialConsumptionMapper.toRestMaterialConsumption(
        materialConsumptionService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("MaterialConsumption with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/material_consumptions")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public PaginatedResponse getMaterialConsumptions(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "consumption_status", required = false) String consumptionStatus,
      @RequestParam(name = "job_id", required = false) String jobId) {
    ConsumptionStatus status =
        consumptionStatus != null ? ConsumptionStatus.valueOf(consumptionStatus) : null;
    var result = materialConsumptionService.findAll(page, pageSize, status, jobId);
    return new PaginatedResponse(
        materialConsumptionMapper.toRestMaterialConsumptions(result.getContent()),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/material_consumptions")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<MaterialConsumption> crupdateMaterialConsumptions(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateMaterialConsumption> toWrite) {
    List<com.example.demo.model.movement.MaterialConsumption> saved =
        materialConsumptionService.createOrUpdateAll(
            toWrite.stream().map(materialConsumptionMapper::toDomain).toList());
    return materialConsumptionMapper.toRestMaterialConsumptions(saved);
  }

  @PutMapping("/users/{userId}/companies/{companyId}/material_consumptions/{id}/complete")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public MaterialConsumption completeMaterialConsumption(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return materialConsumptionMapper.toRestMaterialConsumption(
        materialConsumptionService.completeConsumption(id));
  }

  @PutMapping("/users/{userId}/companies/{companyId}/material_consumptions/{id}/return")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public MaterialConsumption returnMaterials(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String id,
      @RequestParam(name = "quantity") int quantity) {
    return materialConsumptionMapper.toRestMaterialConsumption(
        materialConsumptionService.returnMaterials(id, quantity));
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/material_consumptions/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteMaterialConsumptionById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    materialConsumptionService.deleteById(id);
  }
}
