package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateTravelExpense;
import com.example.demo.client.model.TravelExpense;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.TravelExpenseMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelExpenseCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.TravelExpenseService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TravelExpenseController {

  private final TravelExpenseService travelExpenseService;
  private final TravelExpenseMapper travelExpenseMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_expenses/{id}")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER') or #userId == authentication.principal.id")
  public TravelExpense getTravelExpenseById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    return travelExpenseMapper.toRestTravelExpense(
        travelExpenseService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("TravelExpense with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_expenses")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER') or #userId == authentication.principal.id")
  public PaginatedResponse getTravelExpenses(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "departure_location", required = false) String departureLocation,
      @RequestParam(name = "arrival_location", required = false) String arrivalLocation,
      @RequestParam(name = "arrival_date", required = false) java.time.Instant arrivalDate) {
    TravelExpenseCriteria criteria = new TravelExpenseCriteria();
    criteria.setDepartureLocation(departureLocation);
    criteria.setArrivalLocation(arrivalLocation);
    criteria.setArrivalDate(arrivalDate);

    var result = travelExpenseService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(travelExpenseMapper::toRestTravelExpense).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_expenses")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id")
  public List<TravelExpense> crupdateTravelExpenses(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @Valid @RequestBody List<CrupdateTravelExpense> toWrite) {
    List<com.example.demo.model.money.TravelExpense> saved =
        travelExpenseService.createOrUpdateAll(
            toWrite.stream().map(travelExpenseMapper::toDomain).toList());
    return saved.stream().map(travelExpenseMapper::toRestTravelExpense).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/travel_expenses/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_WORKER')")
  public void deleteTravelExpenseById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    travelExpenseService.deleteById(id);
  }
}
