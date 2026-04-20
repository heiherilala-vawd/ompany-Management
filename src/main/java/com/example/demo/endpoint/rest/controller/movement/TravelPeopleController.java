package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.CrupdateTravelPeople;
import com.example.demo.client.model.TravelPeople;
import com.example.demo.endpoint.rest.mapper.movement.TravelPeopleMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.movement.TravelPeopleService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TravelPeopleController {

  private final TravelPeopleService travelPeopleService;
  private final TravelPeopleMapper travelPeopleMapper;

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{travel_expenses_id}/travel_people/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id")
  public TravelPeople getTravelPeopleById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String travel_expenses_id,
      @PathVariable String id) {
    return travelPeopleMapper.toRestTravelPeople(
        travelPeopleService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Travel with id " + id + " not found")));
  }

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{travel_expenses_id}/travel_people")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id")
  public List<TravelPeople> getTravelPeople(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String travel_expenses_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "travel_id", required = false) String travelId) {

    return travelPeopleService.findAll(page, pageSize, travelId).stream()
        .map(travelPeopleMapper::toRestTravelPeople)
        .toList();
  }

  @PutMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{travel_expenses_id}/travel_people")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id")
  public List<TravelPeople> crupdateTravelPeople(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String travel_expenses_id,
      @RequestBody List<CrupdateTravelPeople> toWrite) {
    var saved =
        travelPeopleService.createOrUpdateAll(
            toWrite.stream().map(travelPeopleMapper::toDomain).toList());
    return saved.stream().map(travelPeopleMapper::toRestTravelPeople).toList();
  }

  @DeleteMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{travel_expenses_id}/travel_people/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteTravelPeopleById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String travel_expenses_id,
      @PathVariable String id) {
    travelPeopleService.deleteById(id);
  }
}
