package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.BudgetLine;
import com.example.demo.client.model.CrupdateBudgetLine;
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

  @GetMapping("/companies/{comp_id}/budget_lines/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public BudgetLine getBudgetLineById(@PathVariable String comp_id, @PathVariable String id) {
    return budgetLineMapper.toRestBudgetLine(
        budgetLineService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("BudgetLine with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/budget_lines")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<BudgetLine> getBudgetLines(
      @PathVariable String comp_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return budgetLineMapper.toRestBudgetLines(
        budgetLineService.findAll(page, pageSize).getContent());
  }

  @PutMapping("/companies/{comp_id}/budget_lines")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<BudgetLine> crupdateBudgetLines(
      @PathVariable String comp_id, @Valid @RequestBody List<CrupdateBudgetLine> toWrite) {
    List<com.example.demo.model.money.BudgetLine> saved =
        budgetLineService.createOrUpdateAll(
            toWrite.stream().map(rest -> budgetLineMapper.toDomain(rest, comp_id)).toList());
    return budgetLineMapper.toRestBudgetLines(saved);
  }

  @DeleteMapping("/companies/{comp_id}/budget_lines/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteBudgetLineById(@PathVariable String comp_id, @PathVariable String id) {
    budgetLineService.deleteById(id);
  }
}
