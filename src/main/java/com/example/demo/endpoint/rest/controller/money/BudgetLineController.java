package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.BudgetLine;
import com.example.demo.client.model.CrupdateBudgetLine;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.BudgetLineMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.BudgetLineService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class BudgetLineController {

  private final BudgetLineService budgetLineService;
  private final BudgetLineMapper budgetLineMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/budget_lines/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public BudgetLine getBudgetLineById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return budgetLineMapper.toRestBudgetLine(
        budgetLineService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("BudgetLine with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/budget_lines")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getBudgetLines(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    var result = budgetLineService.findAll(page, pageSize);
    return new PaginatedResponse(
        budgetLineMapper.toRestBudgetLines(result.getContent()),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/budget_lines")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<BudgetLine> crupdateBudgetLines(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateBudgetLine> toWrite) {
    List<com.example.demo.model.money.BudgetLine> saved =
        budgetLineService.createOrUpdateAll(
            toWrite.stream().map(rest -> budgetLineMapper.toDomain(rest, companyId)).toList());
    return budgetLineMapper.toRestBudgetLines(saved);
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/budget_lines/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteBudgetLineById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    budgetLineService.deleteById(id);
  }
}
