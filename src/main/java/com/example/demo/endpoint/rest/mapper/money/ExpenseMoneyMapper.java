package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateExpenseMoney;
import com.example.demo.client.model.ExpenseMoney;
import com.example.demo.service.money.MonetaryMovementService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ExpenseMoneyMapper {

  private final MonetaryMovementService monetaryMovementService;

  public com.example.demo.model.money.ExpenseMoney toDomain(ExpenseMoney restExpense) {
    if (restExpense == null) return null;

    return com.example.demo.model.money.ExpenseMoney.builder()
        .id(restExpense.getId())
        .monetaryMovement(
            restExpense.getMonetaryId() != null
                ? monetaryMovementService.findById(restExpense.getMonetaryId()).orElse(null)
                : null)
        .build();
  }

  public com.example.demo.model.money.ExpenseMoney toDomain(CrupdateExpenseMoney restExpense) {
    if (restExpense == null) return null;

    return com.example.demo.model.money.ExpenseMoney.builder()
        .id(restExpense.getId())
        .monetaryMovement(
            restExpense.getMonetaryId() != null
                ? monetaryMovementService.findById(restExpense.getMonetaryId()).orElse(null)
                : null)
        .build();
  }

  public ExpenseMoney toRestExpense(com.example.demo.model.money.ExpenseMoney domainExpense) {
    if (domainExpense == null) return null;

    ExpenseMoney restExpense = new ExpenseMoney();
    restExpense.setId(domainExpense.getId());
    restExpense.setMonetaryId(
        domainExpense.getMonetaryMovement() != null
            ? domainExpense.getMonetaryMovement().getId()
            : null);

    return restExpense;
  }
}
