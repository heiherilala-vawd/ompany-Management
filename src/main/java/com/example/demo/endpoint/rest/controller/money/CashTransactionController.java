package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CashTransaction;
import com.example.demo.client.model.CrupdateCashTransaction;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.CashTransactionMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.CashTransactionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CashTransactionController {

  private final CashTransactionService cashTransactionService;
  private final CashTransactionMapper cashTransactionMapper;

  @GetMapping(
      "/users/{userId}/companies/{companyId}/cash_accounts/{cashAccountId}/transactions/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public CashTransaction getCashTransactionById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String cashAccountId,
      @PathVariable String id) {
    return cashTransactionMapper.toRestCashTransaction(
        cashTransactionService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("CashTransaction with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/cash_accounts/{cashAccountId}/transactions")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getCashTransactions(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String cashAccountId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    var result = cashTransactionService.findAll(page, pageSize);
    return new PaginatedResponse(
        cashTransactionMapper.toRestCashTransactions(result.getContent()),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/cash_accounts/{cashAccountId}/transactions")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<CashTransaction> crupdateCashTransactions(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String cashAccountId,
      @Valid @RequestBody List<CrupdateCashTransaction> toWrite) {
    List<com.example.demo.model.money.CashTransaction> saved =
        cashTransactionService.createOrUpdateAll(
            toWrite.stream().map(cashTransactionMapper::toDomain).toList());
    return cashTransactionMapper.toRestCashTransactions(saved);
  }

  @DeleteMapping(
      "/users/{userId}/companies/{companyId}/cash_accounts/{cashAccountId}/transactions/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteCashTransactionById(
      @PathVariable String userId,
      @PathVariable String companyId,
      @PathVariable String cashAccountId,
      @PathVariable String id) {
    cashTransactionService.deleteById(id);
  }
}
