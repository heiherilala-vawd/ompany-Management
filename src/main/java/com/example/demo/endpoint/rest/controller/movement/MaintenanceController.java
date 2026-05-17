package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateMaintenance;
import com.example.demo.client.model.Maintenance;
import com.example.demo.endpoint.rest.mapper.movement.MaintenanceMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.EquipmentService;
import com.example.demo.service.movement.MaintenanceService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MaintenanceController {

  private final MaintenanceService maintenanceService;
  private final MaintenanceMapper maintenanceMapper;
  private final EquipmentService equipmentService;

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/equipment/{equipment_id}/maintenances/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public Maintenance getMaintenanceById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String equipment_id,
      @PathVariable String id) {
    return maintenanceMapper.toRestMaintenance(
        maintenanceService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Maintenance with id " + id + " not found")));
  }

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/equipment/{equipment_id}/maintenances")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Maintenance> getMaintenances(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String equipment_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "description", required = false) String description) {
    return maintenanceService.findAll(equipment_id, page, pageSize, description).stream()
        .map(maintenanceMapper::toRestMaintenance)
        .toList();
  }

  @PutMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/equipment/{equipment_id}/maintenances")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Maintenance> crupdateMaintenances(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String equipment_id,
      @RequestBody List<CrupdateMaintenance> toWrite) {
    List<com.example.demo.model.movement.Maintenance> saved =
        maintenanceService.createOrUpdateAll(
            equipment_id,
            toWrite.stream()
                .map(
                    cm -> {
                      com.example.demo.model.movement.Maintenance m =
                          maintenanceMapper.toDomain(cm);
                      m.setEquipment(
                          equipmentService
                              .findById(equipment_id)
                              .orElseThrow(
                                  () ->
                                      new NotFoundException(
                                          "Equipment with id " + equipment_id + " not found")));
                      return m;
                    })
                .toList());
    return saved.stream().map(maintenanceMapper::toRestMaintenance).toList();
  }

  @DeleteMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/equipment/{equipment_id}/maintenances/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteMaintenanceById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String equipment_id,
      @PathVariable String id) {
    maintenanceService.deleteById(id);
  }
}
