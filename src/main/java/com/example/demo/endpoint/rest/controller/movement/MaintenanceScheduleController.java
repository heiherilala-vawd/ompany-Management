package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateMaintenanceSchedule;
import com.example.demo.client.model.MaintenanceSchedule;
import com.example.demo.endpoint.rest.mapper.movement.MaintenanceScheduleMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.MaintenanceScheduleService;
import jakarta.validation.Valid;
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
public class MaintenanceScheduleController {

  private final MaintenanceScheduleService maintenanceScheduleService;
  private final MaintenanceScheduleMapper maintenanceScheduleMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/maintenance_schedules")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<MaintenanceSchedule> getMaintenanceSchedules(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return maintenanceScheduleService.findAll(page, pageSize, companyId).stream()
        .map(maintenanceScheduleMapper::toRestMaintenanceSchedule)
        .toList();
  }

  @PutMapping("/users/{userId}/companies/{companyId}/maintenance_schedules")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<MaintenanceSchedule> crupdateMaintenanceSchedules(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateMaintenanceSchedule> toWrite) {
    return maintenanceScheduleService
        .createOrUpdateAll(
            toWrite.stream()
                .map(rest -> maintenanceScheduleMapper.toDomain(rest, companyId))
                .toList())
        .stream()
        .map(maintenanceScheduleMapper::toRestMaintenanceSchedule)
        .toList();
  }

  @GetMapping("/users/{userId}/companies/{companyId}/maintenance_schedules/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public MaintenanceSchedule getMaintenanceScheduleById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    com.example.demo.model.movement.MaintenanceSchedule schedule =
        maintenanceScheduleService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("MaintenanceSchedule " + id + " not found"));
    return maintenanceScheduleMapper.toRestMaintenanceSchedule(schedule);
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/maintenance_schedules/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteMaintenanceScheduleById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    maintenanceScheduleService.deleteById(id);
  }
}
