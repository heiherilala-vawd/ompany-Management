package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateEquipmentIncident;
import com.example.demo.client.model.EquipmentIncident;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.movement.EquipmentIncidentMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.EquipmentIncidentCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.movement.IncidentType;
import com.example.demo.service.movement.EquipmentIncidentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class EquipmentIncidentController {

  private final EquipmentIncidentService equipmentIncidentService;
  private final EquipmentIncidentMapper equipmentIncidentMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/equipment_incidents/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER', 'EMPLOYEE')")
  public EquipmentIncident getEquipmentIncidentById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return equipmentIncidentMapper.toRest(
        equipmentIncidentService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("EquipmentIncident with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/equipment_incidents")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER', 'EMPLOYEE')")
  public PaginatedResponse getEquipmentIncidents(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "equipment_id", required = false) String equipmentId,
      @RequestParam(name = "incident_type", required = false) String incidentType,
      @RequestParam(name = "user_id", required = false) String userIdParam,
      @RequestParam(name = "travel_id", required = false) String travelId) {
    EquipmentIncidentCriteria criteria = new EquipmentIncidentCriteria();
    criteria.setEquipmentId(equipmentId);
    if (incidentType != null) {
      criteria.setIncidentType(IncidentType.valueOf(incidentType));
    }
    criteria.setUserId(userIdParam);
    criteria.setTravelId(travelId);

    var result = equipmentIncidentService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        equipmentIncidentMapper.toRestList(result.getContent()), (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/equipment_incidents")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<EquipmentIncident> crupdateEquipmentIncidents(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateEquipmentIncident> toWrite) {
    var saved =
        equipmentIncidentService.createOrUpdateAll(
            toWrite.stream().map(equipmentIncidentMapper::toDomain).toList());
    return equipmentIncidentMapper.toRestList(saved);
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/equipment_incidents/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteEquipmentIncidentById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    equipmentIncidentService.deleteById(id);
  }
}
