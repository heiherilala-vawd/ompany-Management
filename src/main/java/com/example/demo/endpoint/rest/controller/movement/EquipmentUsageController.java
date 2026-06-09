package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateEquipmentUsage;
import com.example.demo.client.model.EquipmentUsage;
import com.example.demo.endpoint.rest.mapper.movement.EquipmentUsageMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.EquipmentUsageService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class EquipmentUsageController {

  private final EquipmentUsageService equipmentUsageService;
  private final EquipmentUsageMapper equipmentUsageMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/equipment_usage/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public EquipmentUsage getEquipmentUsageById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return equipmentUsageMapper.toRestEquipmentUsage(
        equipmentUsageService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("EquipmentUsage with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/equipment_usage")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<EquipmentUsage> getEquipmentUsages(
      @PathVariable String userId, @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "job_id", required = false) String jobId) {
    return equipmentUsageMapper.toRestEquipmentUsages(
        equipmentUsageService.findAll(page, pageSize, jobId).getContent());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/equipment_usage")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<EquipmentUsage> crupdateEquipmentUsages(
      @PathVariable String userId, @PathVariable String companyId, @Valid @RequestBody List<CrupdateEquipmentUsage> toWrite) {
    List<com.example.demo.model.movement.EquipmentUsage> saved =
        equipmentUsageService.createOrUpdateAll(
            toWrite.stream().map(equipmentUsageMapper::toDomain).toList());
    return equipmentUsageMapper.toRestEquipmentUsages(saved);
  }

  @PutMapping("/users/{userId}/companies/{companyId}/equipment_usage/{id}/return")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public EquipmentUsage returnEquipment(
      @PathVariable String userId, @PathVariable String companyId,
      @PathVariable String id,
      @RequestParam(name = "status") String status) {
    com.example.demo.model.movement.EquipmentUsage.UsageStatus usageStatus =
        com.example.demo.model.movement.EquipmentUsage.UsageStatus.valueOf(status);
    return equipmentUsageMapper.toRestEquipmentUsage(
        equipmentUsageService.returnEquipment(id, usageStatus));
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/equipment_usage/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteEquipmentUsageById(@PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    equipmentUsageService.deleteById(id);
  }
}
