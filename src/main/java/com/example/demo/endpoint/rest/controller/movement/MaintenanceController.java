package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateMaintenance;
import com.example.demo.client.model.Maintenance;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.movement.MaintenanceMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.EquipmentService;
import com.example.demo.service.movement.MaintenanceService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MaintenanceController {

  private final MaintenanceService maintenanceService;
  private final MaintenanceMapper maintenanceMapper;
  private final EquipmentService equipmentService;

  @GetMapping("/users/{userId}/companies/{companyId}/maintenances/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public Maintenance getMaintenanceById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return maintenanceMapper.toRestMaintenance(
        maintenanceService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Maintenance with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/maintenances")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public PaginatedResponse getMaintenances(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(required = false) String equipment_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "description", required = false) String description) {
    var result = maintenanceService.findAll(equipment_id, page, pageSize, description);
    return new PaginatedResponse(
        result.stream().map(maintenanceMapper::toRestMaintenance).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/maintenances")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Maintenance> crupdateMaintenances(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateMaintenance> toWrite) {
    List<com.example.demo.model.movement.Maintenance> saved =
        maintenanceService.createOrUpdateAll(
            toWrite.stream()
                .map(
                    cm -> {
                      com.example.demo.model.movement.Maintenance m =
                          maintenanceMapper.toDomain(cm);
                      if (cm.getEquipmentId() != null) {
                        m.setEquipment(
                            equipmentService
                                .findById(cm.getEquipmentId())
                                .orElseThrow(
                                    () ->
                                        new NotFoundException(
                                            "Equipment with id "
                                                + cm.getEquipmentId()
                                                + " not found")));
                      }
                      return m;
                    })
                .toList());
    return saved.stream().map(maintenanceMapper::toRestMaintenance).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/maintenances/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteMaintenanceById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    maintenanceService.deleteById(id);
  }
}
