package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateTravelMaterials;
import com.example.demo.client.model.TravelMaterials;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.movement.TravelMaterialsMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelMaterialsCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.TravelMaterialsService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TravelMaterialsController {

  private final TravelMaterialsService travelMaterialsService;
  private final TravelMaterialsMapper travelMaterialsMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_materials/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id")
  public TravelMaterials getTravelMaterialsById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    return travelMaterialsMapper.toRestTravelMaterials(
        travelMaterialsService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("TravelMaterials with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_materials")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id")
  public PaginatedResponse getTravelMaterials(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
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

    var result = travelMaterialsService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(travelMaterialsMapper::toRestTravelMaterials).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_materials")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id")
  public List<TravelMaterials> crupdateTravelMaterials(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @Valid @RequestBody List<CrupdateTravelMaterials> toWrite) {
    List<com.example.demo.model.movement.TravelMaterials> saved =
        travelMaterialsService.createOrUpdateAll(
            toWrite.stream().map(travelMaterialsMapper::toDomain).toList());
    return saved.stream().map(travelMaterialsMapper::toRestTravelMaterials).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_materials/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteTravelMaterialsById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    travelMaterialsService.deleteById(id);
  }
}
