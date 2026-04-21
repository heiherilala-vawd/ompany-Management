package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateTravelEquipment;
import com.example.demo.client.model.TransportStatus;
import com.example.demo.client.model.TravelEquipment;
import com.example.demo.endpoint.rest.mapper.movement.TravelEquipmentMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelEquipmentCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.TravelEquipmentService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TravelEquipmentController {

  private final TravelEquipmentService travelEquipmentService;
  private final TravelEquipmentMapper travelEquipmentMapper;

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{travel_expenses_id}/travel_equipment/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #user_id == authentication.principal.id")
  public TravelEquipment getTravelEquipmentById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String travel_expenses_id,
      @PathVariable String id) {
    return travelEquipmentMapper.toRestTravelEquipment(
        travelEquipmentService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("TravelEquipment with id " + id + " not found")));
  }

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{travel_expenses_id}/travel_equipment")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #user_id == authentication.principal.id")
  public List<TravelEquipment> getTravelEquipment(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String travel_expenses_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "travel_id", required = false) String travelId,
      @RequestParam(name = "equipment_id", required = false) String equipmentId,
      @RequestParam(name = "quantity", required = false) Integer quantity,
      @RequestParam(name = "status", required = false) TransportStatus status) {
    TravelEquipmentCriteria criteria = new TravelEquipmentCriteria();
    criteria.setTravelId(travelId);
    criteria.setEquipmentId(equipmentId);
    criteria.setQuantity(quantity);
    criteria.setStatus(
        status != null
            ? com.example.demo.model.movement.TravelEquipment.TransportStatus.valueOf(status.name())
            : null);

    return travelEquipmentService.findAll(page, pageSize, criteria).stream()
        .map(travelEquipmentMapper::toRestTravelEquipment)
        .toList();
  }

  @PutMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{travel_expenses_id}/travel_equipment")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #user_id == authentication.principal.id")
  public List<TravelEquipment> crupdateTravelEquipment(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String travel_expenses_id,
      @RequestBody List<CrupdateTravelEquipment> toWrite) {
    List<com.example.demo.model.movement.TravelEquipment> saved =
        toWrite.stream().map(travelEquipmentMapper::toDomain).toList();
    travelEquipmentService.createOrUpdateAll(saved);
    return saved.stream().map(travelEquipmentMapper::toRestTravelEquipment).toList();
  }

  @DeleteMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{travel_expenses_id}/travel_equipment/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteTravelEquipmentById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String travel_expenses_id,
      @PathVariable String id) {
    travelEquipmentService.deleteById(id);
  }
}
