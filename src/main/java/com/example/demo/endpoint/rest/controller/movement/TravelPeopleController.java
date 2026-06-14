package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateTravelPeople;
import com.example.demo.client.model.TravelPeople;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.movement.TravelPeopleMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelPeopleCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.TravelPeopleService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TravelPeopleController {

  private final TravelPeopleService travelPeopleService;
  private final TravelPeopleMapper travelPeopleMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_people/{id}")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER') or #userId == authentication.principal.id")
  public TravelPeople getTravelPeopleById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    return travelPeopleMapper.toRestTravelPeople(
        travelPeopleService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Travel with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_people")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER') or #userId == authentication.principal.id")
  public PaginatedResponse getTravelPeople(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "travel_id", required = false) String travelId,
      @RequestParam(name = "user_id", required = false) String filterUserId,
      @RequestParam(name = "arrival_location", required = false) String arrivalLocation,
      @RequestParam(name = "arrival_date_min", required = false) java.time.Instant arrivalDateMin,
      @RequestParam(name = "arrival_date_max", required = false) java.time.Instant arrivalDateMax,
      @RequestParam(name = "not_arrived", required = false) Boolean notArrived) {
    TravelPeopleCriteria criteria = new TravelPeopleCriteria();
    criteria.setTravelId(travelId);
    criteria.setUserId(filterUserId);
    criteria.setArrivalLocation(arrivalLocation);
    criteria.setArrivalDateMin(arrivalDateMin);
    criteria.setArrivalDateMax(arrivalDateMax);
    criteria.setNotArrived(notArrived);

    var result = travelPeopleService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(travelPeopleMapper::toRestTravelPeople).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_people")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id")
  public List<TravelPeople> crupdateTravelPeople(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @Valid @RequestBody List<CrupdateTravelPeople> toWrite) {
    List<com.example.demo.model.movement.TravelPeople> saved =
        travelPeopleService.createOrUpdateAll(
            toWrite.stream().map(travelPeopleMapper::toDomain).toList());
    return saved.stream().map(travelPeopleMapper::toRestTravelPeople).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_people/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteTravelPeopleById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    travelPeopleService.deleteById(id);
  }
}
