package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateTravelMaterials;
import com.example.demo.client.model.TravelMaterials;
import com.example.demo.endpoint.rest.mapper.movement.TravelMaterialsMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelMaterialsCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.TravelMaterialsService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TravelMaterialsController {

  private final TravelMaterialsService travelMaterialsService;
  private final TravelMaterialsMapper travelMaterialsMapper;

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/travel_expenses/{travel_expenses_id}/travel_materials/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #user_id == authentication.principal.id")
  public TravelMaterials getTravelMaterialsById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String travel_expenses_id,
      @PathVariable String id) {
    return travelMaterialsMapper.toRestTravelMaterials(
        travelMaterialsService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("TravelMaterials with id " + id + " not found")));
  }

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/travel_expenses/{travel_expenses_id}/travel_materials")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #user_id == authentication.principal.id")
  public List<TravelMaterials> getTravelMaterials(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String travel_expenses_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "travel_id", required = false) String travelId,
      @RequestParam(name = "material_id", required = false) String materialId,
      @RequestParam(name = "quantity", required = false) Integer quantity,
      @RequestParam(name = "quantity_received", required = false) Integer quantityReceived,
      @RequestParam(name = "arrival_location", required = false) String arrivalLocation,
      @RequestParam(name = "arrival_date_min", required = false) java.time.Instant arrivalDateMin,
      @RequestParam(name = "arrival_date_max", required = false) java.time.Instant arrivalDateMax,
      @RequestParam(name = "not_arrived", required = false) Boolean notArrived) {
    TravelMaterialsCriteria criteria = new TravelMaterialsCriteria();
    criteria.setTravelId(travelId);
    criteria.setMaterialId(materialId);
    criteria.setQuantity(quantity);
    criteria.setQuantityReceived(quantityReceived);
    criteria.setArrivalLocation(arrivalLocation);
    criteria.setArrivalDateMin(arrivalDateMin);
    criteria.setArrivalDateMax(arrivalDateMax);
    criteria.setNotArrived(notArrived);

    return travelMaterialsService.findAll(page, pageSize, criteria).stream()
        .map(travelMaterialsMapper::toRestTravelMaterials)
        .toList();
  }

  @PutMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/travel_expenses/{travel_expenses_id}/travel_materials")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #user_id == authentication.principal.id")
  public List<TravelMaterials> crupdateTravelMaterials(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String travel_expenses_id,
      @RequestBody List<CrupdateTravelMaterials> toWrite) {
    List<com.example.demo.model.movement.TravelMaterials> saved =
        travelMaterialsService.createOrUpdateAll(
            toWrite.stream().map(travelMaterialsMapper::toDomain).toList());
    return saved.stream().map(travelMaterialsMapper::toRestTravelMaterials).toList();
  }

  @DeleteMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/travel_expenses/{travel_expenses_id}/travel_materials/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteTravelMaterialsById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String travel_expenses_id,
      @PathVariable String id) {
    travelMaterialsService.deleteById(id);
  }
}
