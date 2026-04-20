package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateExpenseMoney;
import com.example.demo.client.model.ExpenseMoney;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ExpenseMoneyMapper {

  public com.example.demo.model.money.ExpenseMoney toDomain(ExpenseMoney restExpense) {
    if (restExpense == null) return null;

    return com.example.demo.model.money.ExpenseMoney.builder()
        .id(restExpense.getId())
        .amount(restExpense.getAmount())
        .description(restExpense.getDescription())
        .comment(restExpense.getComment())
        .build();
  }

  public com.example.demo.model.money.ExpenseMoney toDomain(CrupdateExpenseMoney restExpense) {
    if (restExpense == null) return null;

    return com.example.demo.model.money.ExpenseMoney.builder()
        .id(restExpense.getId())
        .amount(restExpense.getAmount())
        .description(restExpense.getDescription())
        .comment(restExpense.getComment())
        .build();
  }

  public ExpenseMoney toRestExpense(com.example.demo.model.money.ExpenseMoney domainExpense) {
    if (domainExpense == null) return null;

    ExpenseMoney restExpense = new ExpenseMoney();
    restExpense.setId(domainExpense.getId());
    restExpense.setAmount(domainExpense.getAmount());
    restExpense.setDescription(domainExpense.getDescription());
    RestAuditMapperUtils.mapAuditFields(
        domainExpense,
        restExpense::setCreatedAt,
        restExpense::setUpdatedAt,
        restExpense::setComment,
        restExpense::setCreatedBy,
        restExpense::setUpdatedBy);

    return restExpense;
  }
}
