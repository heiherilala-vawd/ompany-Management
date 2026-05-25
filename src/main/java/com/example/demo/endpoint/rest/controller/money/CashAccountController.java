package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CashAccount;
import com.example.demo.client.model.CrupdateCashAccount;
import com.example.demo.endpoint.rest.mapper.money.CashAccountMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.CashAccountService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CashAccountController {

  private final CashAccountService cashAccountService;
  private final CashAccountMapper cashAccountMapper;

  @GetMapping("/companies/{comp_id}/cash_accounts/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public CashAccount getCashAccountById(@PathVariable String comp_id, @PathVariable String id) {
    return cashAccountMapper.toRestCashAccount(
        cashAccountService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("CashAccount with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/cash_accounts")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<CashAccount> getCashAccounts(
      @PathVariable String comp_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return cashAccountMapper.toRestCashAccounts(
        cashAccountService.findAll(page, pageSize).getContent());
  }

  @PutMapping("/companies/{comp_id}/cash_accounts")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<CashAccount> crupdateCashAccounts(
      @PathVariable String comp_id, @RequestBody List<CrupdateCashAccount> toWrite) {
    List<com.example.demo.model.money.CashAccount> saved =
        cashAccountService.createOrUpdateAll(
            toWrite.stream().map(cashAccountMapper::toDomain).toList());
    return cashAccountMapper.toRestCashAccounts(saved);
  }

  @DeleteMapping("/companies/{comp_id}/cash_accounts/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteCashAccountById(@PathVariable String comp_id, @PathVariable String id) {
    cashAccountService.deleteById(id);
  }
}
