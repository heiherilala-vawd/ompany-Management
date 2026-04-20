package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateOtherExpense;
import com.example.demo.client.model.OtherExpense;
import com.example.demo.endpoint.rest.mapper.money.OtherExpenseMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.OtherExpenseService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class OtherExpenseController {

  private final OtherExpenseService otherExpenseService;
  private final OtherExpenseMapper otherExpenseMapper;

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/other_expenses/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id")
  public OtherExpense getOtherExpenseById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String id) {
    return otherExpenseMapper.toRestOtherExpense(
        otherExpenseService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("OtherExpense with id " + id + " not found")));
  }

  @GetMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/other_expenses")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id")
  public List<OtherExpense> getOtherExpenses(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {

    return otherExpenseService.findAll(page, pageSize).stream()
        .map(otherExpenseMapper::toRestOtherExpense)
        .toList();
  }

  @PutMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/other_expenses")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id")
  public List<OtherExpense> crupdateOtherExpenses(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @RequestBody List<CrupdateOtherExpense> toWrite) {
    var saved =
        otherExpenseService.createOrUpdateAll(
            toWrite.stream().map(otherExpenseMapper::toDomain).toList());
    return saved.stream().map(otherExpenseMapper::toRestOtherExpense).toList();
  }

  @DeleteMapping(
      "/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{expenses_id}/other_expenses/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteOtherExpenseById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String expenses_id,
      @PathVariable String id) {
    otherExpenseService.deleteById(id);
  }
}
