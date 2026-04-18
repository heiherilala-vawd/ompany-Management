package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateIncomeMoney;
import com.example.demo.client.model.IncomeMoney;
import com.example.demo.endpoint.rest.mapper.money.IncomeMoneyMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.IncomeMoneyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class IncomeController {

  private final IncomeMoneyService incomeMoneyService;
  private final IncomeMoneyMapper incomeMoneyMapper;

  @GetMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/incomes/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public IncomeMoney getIncomeById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String id) {
    return incomeMoneyMapper.toRestIncome(
        incomeMoneyService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Income with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/incomes")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<IncomeMoney> getIncomes(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {

    return incomeMoneyService.findAll(page, pageSize).stream()
        .map(incomeMoneyMapper::toRestIncome)
        .toList();
  }

  @PutMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/incomes")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<IncomeMoney> crupdateIncomes(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @RequestBody List<CrupdateIncomeMoney> toWrite) {
    var saved =
        incomeMoneyService.createOrUpdateAll(
            toWrite.stream().map(incomeMoneyMapper::toDomain).toList());
    return saved.stream().map(incomeMoneyMapper::toRestIncome).toList();
  }

  @DeleteMapping("/companies/{comp_id}/job/{job_id}/user/{user_id}/incomes/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public void deleteIncomeById(
      @PathVariable String comp_id,
      @PathVariable String job_id,
      @PathVariable String user_id,
      @PathVariable String id) {
    incomeMoneyService.deleteById(id);
  }
}
