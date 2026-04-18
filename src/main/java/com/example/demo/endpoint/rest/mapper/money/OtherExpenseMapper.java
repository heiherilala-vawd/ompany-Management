package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateOtherExpense;
import com.example.demo.client.model.OtherExpense;
import com.example.demo.service.money.ExpenseMoneyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OtherExpenseMapper {

  private final ExpenseMoneyService expenseMoneyService;

  public com.example.demo.model.money.OtherExpense toDomain(OtherExpense restOtherExpense) {
    if (restOtherExpense == null) return null;

    return com.example.demo.model.money.OtherExpense.builder()
        .id(restOtherExpense.getId())
        .expense(
            restOtherExpense.getExpenseId() != null
                ? expenseMoneyService.findById(restOtherExpense.getExpenseId()).orElse(null)
                : null)
        .description(restOtherExpense.getDescription())
        .build();
  }

  public com.example.demo.model.money.OtherExpense toDomain(CrupdateOtherExpense restOtherExpense) {
    if (restOtherExpense == null) return null;

    return com.example.demo.model.money.OtherExpense.builder()
        .id(restOtherExpense.getId())
        .expense(
            restOtherExpense.getExpenseId() != null
                ? expenseMoneyService.findById(restOtherExpense.getExpenseId()).orElse(null)
                : null)
        .description(restOtherExpense.getDescription())
        .build();
  }

  public OtherExpense toRestOtherExpense(
      com.example.demo.model.money.OtherExpense domainOtherExpense) {
    if (domainOtherExpense == null) return null;

    OtherExpense restOtherExpense = new OtherExpense();
    restOtherExpense.setId(domainOtherExpense.getId());
    restOtherExpense.setExpenseId(
        domainOtherExpense.getExpense() != null ? domainOtherExpense.getExpense().getId() : null);
    restOtherExpense.setDescription(domainOtherExpense.getDescription());

    return restOtherExpense;
  }

  public List<OtherExpense> toRestOtherExpenses(
      List<com.example.demo.model.money.OtherExpense> domainOtherExpenses) {
    return domainOtherExpenses.stream().map(this::toRestOtherExpense).toList();
  }
}
