package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateExpenseMoney;
import com.example.demo.client.model.ExpenseMoney;
import com.example.demo.endpoint.rest.mapper.money.ExpenseMoneyMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.ExpenseMoneyCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.ExpenseMoneyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ExpenseController {

  private final ExpenseMoneyService expenseMoneyService;
  private final ExpenseMoneyMapper expenseMoneyMapper;

  @GetMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public ExpenseMoney getExpenseById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String id) {
    return expenseMoneyMapper.toRestExpense(
        expenseMoneyService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Expense with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<ExpenseMoney> getExpenses(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "amount", required = false) Integer amount) {
    ExpenseMoneyCriteria criteria = new ExpenseMoneyCriteria();
    criteria.setDescription(description);
    criteria.setAmount(amount);
    criteria.setJobId(job_id);

    return expenseMoneyService.findAll(page, pageSize, criteria).stream()
        .map(expenseMoneyMapper::toRestExpense)
        .toList();
  }

  @PutMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<ExpenseMoney> crupdateExpenses(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @RequestBody List<CrupdateExpenseMoney> toWrite) {
    List<com.example.demo.model.money.ExpenseMoney> saved =
        expenseMoneyService.createOrUpdateAll(
            toWrite.stream().map(expenseMoneyMapper::toDomain).toList());
    return saved.stream().map(expenseMoneyMapper::toRestExpense).toList();
  }

  @DeleteMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/expenses/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteExpenseById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String id) {
    expenseMoneyService.deleteById(id);
  }
}
