package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.BankFee;
import com.example.demo.client.model.CrupdateBankFee;
import com.example.demo.service.money.ExpenseMoneyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BankFeeMapper {

  private final ExpenseMoneyService expenseMoneyService;
  private final ExpenseMoneyMapper expenseMoneyMapper;

  public com.example.demo.model.money.BankFee toDomain(BankFee restBankFee) {
    if (restBankFee == null) return null;

    return com.example.demo.model.money.BankFee.builder()
        .id(restBankFee.getId())
        .expense(
            restBankFee.getExpense() != null && restBankFee.getExpense().getId() != null
                ? expenseMoneyService.findById(restBankFee.getExpense().getId()).orElse(null)
                : null)
        .bankName(restBankFee.getBankName())
        .description(restBankFee.getDescription())
        .build();
  }

  public com.example.demo.model.money.BankFee toDomain(CrupdateBankFee restBankFee) {
    if (restBankFee == null) return null;

    return com.example.demo.model.money.BankFee.builder()
        .id(restBankFee.getId())
        .expense(expenseMoneyMapper.toDomain(restBankFee.getExpense()))
        .bankName(restBankFee.getBankName())
        .description(restBankFee.getDescription())
        .build();
  }

  public BankFee toRestBankFee(com.example.demo.model.money.BankFee domainBankFee) {
    if (domainBankFee == null) return null;

    BankFee restBankFee = new BankFee();
    restBankFee.setId(domainBankFee.getId());
    restBankFee.setExpense(expenseMoneyMapper.toRestCrupdateExpense(domainBankFee.getExpense()));
    restBankFee.setBankName(domainBankFee.getBankName());
    restBankFee.setDescription(domainBankFee.getDescription());

    return restBankFee;
  }

  public List<BankFee> toRestBankFees(List<com.example.demo.model.money.BankFee> domainBankFees) {
    return domainBankFees.stream().map(this::toRestBankFee).toList();
  }
}
