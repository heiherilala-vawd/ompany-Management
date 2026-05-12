package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateIncomeReceipt;
import com.example.demo.client.model.IncomeReceipt;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.money.IncomeMoneyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class IncomeReceiptMapper {

  private final IncomeMoneyService incomeMoneyService;
  private final IncomeMoneyMapper incomeMoneyMapper;

  public com.example.demo.model.money.IncomeReceipt toDomain(IncomeReceipt restReceipt) {
    if (restReceipt == null) return null;

    return com.example.demo.model.money.IncomeReceipt.builder()
        .id(restReceipt.getId())
        .paymentDate(restReceipt.getPaymentDate())
        .amount(restReceipt.getAmount())
        .comment(restReceipt.getComment())
        .income(
            restReceipt.getIncome() != null && restReceipt.getIncome().getId() != null
                ? incomeMoneyService.findById(restReceipt.getIncome().getId()).orElse(null)
                : null)
        .build();
  }

  public com.example.demo.model.money.IncomeReceipt toDomain(CrupdateIncomeReceipt restReceipt) {
    if (restReceipt == null) return null;

    return com.example.demo.model.money.IncomeReceipt.builder()
        .id(restReceipt.getId())
        .paymentDate(restReceipt.getPaymentDate())
        .amount(restReceipt.getAmount())
        .comment(restReceipt.getComment())
        .income(
            restReceipt.getIncomeId() != null
                ? incomeMoneyService.findById(restReceipt.getIncomeId()).orElse(null)
                : null)
        .build();
  }

  public IncomeReceipt toRestIncomeReceipt(
      com.example.demo.model.money.IncomeReceipt domainReceipt) {
    if (domainReceipt == null) return null;

    IncomeReceipt restReceipt = new IncomeReceipt();
    restReceipt.setId(domainReceipt.getId());
    restReceipt.setPaymentDate(domainReceipt.getPaymentDate());
    restReceipt.setAmount(domainReceipt.getAmount());
    restReceipt.setIncome(incomeMoneyMapper.toRestIncome(domainReceipt.getIncome()));
    RestAuditMapperUtils.mapAuditFields(
        domainReceipt,
        restReceipt::setCreatedAt,
        restReceipt::setUpdatedAt,
        restReceipt::setComment,
        restReceipt::setCreatedBy,
        restReceipt::setUpdatedBy);
    return restReceipt;
  }
}
