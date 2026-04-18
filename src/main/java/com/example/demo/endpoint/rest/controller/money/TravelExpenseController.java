package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateTravelExpense;
import com.example.demo.client.model.TravelExpense;
import com.example.demo.endpoint.rest.mapper.money.TravelExpenseMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.TravelExpenseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TravelExpenseController {

  private final TravelExpenseService travelExpenseService;
  private final TravelExpenseMapper travelExpenseMapper;

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TravelExpense getTravelExpenseById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String id) {
    return travelExpenseMapper.toRestTravelExpense(
        travelExpenseService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("TravelExpense with id " + id + " not found")));
  }

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<TravelExpense> getTravelExpenses(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {

    return travelExpenseService.findAll(page, pageSize).stream()
        .map(travelExpenseMapper::toRestTravelExpense)
        .toList();
  }

  @PutMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<TravelExpense> crupdateTravelExpenses(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @RequestBody List<CrupdateTravelExpense> toWrite) {
    var saved =
        travelExpenseService.createOrUpdateAll(
            toWrite.stream().map(travelExpenseMapper::toDomain).toList());
    return saved.stream().map(travelExpenseMapper::toRestTravelExpense).toList();
  }

  @DeleteMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/travel_expenses/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public void deleteTravelExpenseById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String id) {
    travelExpenseService.deleteById(id);
  }
}
