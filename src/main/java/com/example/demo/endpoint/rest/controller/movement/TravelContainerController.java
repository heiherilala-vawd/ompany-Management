package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateTravelContainer;
import com.example.demo.client.model.TravelContainer;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.movement.TravelContainerMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelContainerCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.TravelContainerService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TravelContainerController {

  private final TravelContainerService travelContainerService;
  private final TravelContainerMapper travelContainerMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_containers/{id}")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER') or #userId == authentication.principal.id")
  public TravelContainer getTravelContainerById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    return travelContainerMapper.toRestTravelContainer(
        travelContainerService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("TravelContainer with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_containers")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER') or #userId == authentication.principal.id")
  public PaginatedResponse getTravelContainers(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "travel_id", required = false) String travelId,
      @RequestParam(name = "name", required = false) String name) {
    TravelContainerCriteria criteria = new TravelContainerCriteria();
    criteria.setTravelId(travelId);
    criteria.setName(name);

    var result = travelContainerService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(travelContainerMapper::toRestTravelContainer).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_containers")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER') or #userId == authentication.principal.id")
  public List<TravelContainer> crupdateTravelContainers(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @Valid @RequestBody List<CrupdateTravelContainer> toWrite) {
    List<com.example.demo.model.movement.TravelContainer> saved =
        travelContainerService.createOrUpdateAll(
            toWrite.stream().map(travelContainerMapper::toDomain).toList());
    return saved.stream().map(travelContainerMapper::toRestTravelContainer).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_containers/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteTravelContainerById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    travelContainerService.deleteById(id);
  }
}
