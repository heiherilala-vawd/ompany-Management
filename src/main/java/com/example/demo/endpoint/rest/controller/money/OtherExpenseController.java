package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateOtherExpense;
import com.example.demo.client.model.OtherExpense;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.OtherExpenseMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.OtherExpenseCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.OtherExpenseService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class OtherExpenseController {

  private final OtherExpenseService otherExpenseService;
  private final OtherExpenseMapper otherExpenseMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/other_expenses/{id}")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id"
          + " or @jobSecurity.isCurrentUserAssignedToJob(#jobId)")
  public OtherExpense getOtherExpenseById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    return otherExpenseMapper.toRestOtherExpense(
        otherExpenseService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("OtherExpense with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/other_expenses")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id"
          + " or @jobSecurity.isCurrentUserAssignedToJob(#jobId)")
  public PaginatedResponse getOtherExpenses(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "description", required = false) String description) {
    OtherExpenseCriteria criteria = new OtherExpenseCriteria();
    criteria.setDescription(description);

    var result = otherExpenseService.findAll(page, pageSize, criteria);
    return new PaginatedResponse(
        result.stream().map(otherExpenseMapper::toRestOtherExpense).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/other_expenses")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION') or #userId == authentication.principal.id"
          + " or @jobSecurity.isCurrentUserAssignedToJob(#jobId)")
  public List<OtherExpense> crupdateOtherExpenses(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @Valid @RequestBody List<CrupdateOtherExpense> toWrite) {
    List<com.example.demo.model.money.OtherExpense> saved =
        otherExpenseService.createOrUpdateAll(
            toWrite.stream().map(otherExpenseMapper::toDomain).toList());
    return saved.stream().map(otherExpenseMapper::toRestOtherExpense).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/other_expenses/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteOtherExpenseById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String jobId,
      @PathVariable String id) {
    otherExpenseService.deleteById(id);
  }
}
