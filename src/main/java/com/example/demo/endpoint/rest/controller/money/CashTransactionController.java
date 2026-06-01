package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CashTransaction;
import com.example.demo.client.model.CrupdateCashTransaction;
import com.example.demo.endpoint.rest.mapper.money.CashTransactionMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.CashTransactionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CashTransactionController {

  private final CashTransactionService cashTransactionService;
  private final CashTransactionMapper cashTransactionMapper;

  @GetMapping("/companies/{comp_id}/cash_accounts/{account_id}/transactions/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public CashTransaction getCashTransactionById(
      @PathVariable String comp_id, @PathVariable String account_id, @PathVariable String id) {
    return cashTransactionMapper.toRestCashTransaction(
        cashTransactionService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("CashTransaction with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/cash_accounts/{account_id}/transactions")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<CashTransaction> getCashTransactions(
      @PathVariable String comp_id,
      @PathVariable String account_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return cashTransactionMapper.toRestCashTransactions(
        cashTransactionService.findAll(page, pageSize).getContent());
  }

  @PutMapping("/companies/{comp_id}/cash_accounts/{account_id}/transactions")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<CashTransaction> crupdateCashTransactions(
      @PathVariable String comp_id,
      @PathVariable String account_id,
      @Valid @RequestBody List<CrupdateCashTransaction> toWrite) {
    List<com.example.demo.model.money.CashTransaction> saved =
        cashTransactionService.createOrUpdateAll(
            toWrite.stream().map(cashTransactionMapper::toDomain).toList());
    return cashTransactionMapper.toRestCashTransactions(saved);
  }

  @DeleteMapping("/companies/{comp_id}/cash_accounts/{account_id}/transactions/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteCashTransactionById(
      @PathVariable String comp_id, @PathVariable String account_id, @PathVariable String id) {
    cashTransactionService.deleteById(id);
  }
}
