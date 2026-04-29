package com.example.demo.endpoint.rest.controller.movement;

import com.example.demo.client.model.TravelOperationRequest;
import com.example.demo.endpoint.rest.mapper.movement.TravelOperationMapper;
import com.example.demo.service.movement.TravelOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TravelOperationController {

  private final TravelOperationMapper travelOperationMapper;
  private final TravelOperationService travelOperationService;

  @PostMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/travel_operations")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER') or #user_id == authentication.principal.id")
  public void createTravelOperation(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @RequestBody TravelOperationRequest request) {
    travelOperationService.create(travelOperationMapper.toAggregate(job_id, user_id, request));
  }
}
