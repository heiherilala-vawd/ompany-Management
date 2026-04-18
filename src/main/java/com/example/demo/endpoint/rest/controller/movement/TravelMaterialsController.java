package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateTravelMaterials;
import com.example.demo.client.model.TravelMaterials;
import com.example.demo.endpoint.rest.mapper.movement.TravelMaterialsMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
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
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{travel_expenses_id}/travel_materials/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TravelMaterials getTravelMaterialsById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String travel_expenses_id,
      @PathVariable String id) {
    return travelMaterialsMapper.toRestTravelMaterials(
        travelMaterialsService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("TravelMaterials with id " + id + " not found")));
  }

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{travel_expenses_id}/travel_materials")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<TravelMaterials> getTravelMaterials(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String travel_expenses_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "travel_id", required = false) String travelId) {

    return travelMaterialsService.findAll(page, pageSize, travelId).stream()
        .map(travelMaterialsMapper::toRestTravelMaterials)
        .toList();
  }

  @PutMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{travel_expenses_id}/travel_materials")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<TravelMaterials> crupdateTravelMaterials(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String travel_expenses_id,
      @RequestBody List<CrupdateTravelMaterials> toWrite) {
    var saved =
        travelMaterialsService.createOrUpdateAll(
            toWrite.stream().map(travelMaterialsMapper::toDomain).toList());
    return saved.stream().map(travelMaterialsMapper::toRestTravelMaterials).toList();
  }

  @DeleteMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{travel_expenses_id}/travel_materials/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public void deleteTravelMaterialsById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String travel_expenses_id,
      @PathVariable String id) {
    travelMaterialsService.deleteById(id);
  }
}
