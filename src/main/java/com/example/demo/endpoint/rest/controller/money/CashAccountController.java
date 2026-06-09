package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CashAccount;
import com.example.demo.client.model.CrupdateCashAccount;
import com.example.demo.endpoint.rest.mapper.money.CashAccountMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.CashAccountService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CashAccountController {

  private final CashAccountService cashAccountService;
  private final CashAccountMapper cashAccountMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/cash_accounts/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public CashAccount getCashAccountById(@PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return cashAccountMapper.toRestCashAccount(
        cashAccountService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("CashAccount with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/cash_accounts")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<CashAccount> getCashAccounts(
      @PathVariable String userId, @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return cashAccountMapper.toRestCashAccounts(
        cashAccountService.findAll(page, pageSize).getContent());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/cash_accounts")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<CashAccount> crupdateCashAccounts(
      @PathVariable String userId, @PathVariable String companyId, @Valid @RequestBody List<CrupdateCashAccount> toWrite) {
    List<com.example.demo.model.money.CashAccount> saved =
        cashAccountService.createOrUpdateAll(
            toWrite.stream().map(rest -> cashAccountMapper.toDomain(rest, companyId)).toList());
    return cashAccountMapper.toRestCashAccounts(saved);
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/cash_accounts/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteCashAccountById(@PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    cashAccountService.deleteById(id);
  }
}
