package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CashTransaction;
import com.example.demo.client.model.CrupdateCashTransaction;
import com.example.demo.client.model.CashTransactionType;
import com.example.demo.endpoint.rest.mapper.EnumMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.money.CashAccountService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CashTransactionMapper {

  private final CashAccountService cashAccountService;

  public com.example.demo.model.money.CashTransaction toDomain(CrupdateCashTransaction rest) {
    if (rest == null) return null;

    return com.example.demo.model.money.CashTransaction.builder()
        .id(rest.getId())
        .cashAccount(
            rest.getCashAccountId() != null
                ? cashAccountService.findById(rest.getCashAccountId()).orElse(null)
                : null)
        .amount(rest.getAmount())
        .transactionDate(rest.getTransactionDate())
        .description(rest.getDescription())
        .type(EnumMapper.mapEnum(rest.getType(), com.example.demo.model.money.CashTransaction.TransactionType.class))
        .comment(rest.getComment())
        .build();
  }

  public CashTransaction toRestCashTransaction(com.example.demo.model.money.CashTransaction domain) {
    if (domain == null) return null;

    CashTransaction rest = new CashTransaction();
    rest.setId(domain.getId());
    rest.setCashAccountId(domain.getCashAccount() != null ? domain.getCashAccount().getId() : null);
    rest.setAmount(domain.getAmount());
    rest.setTransactionDate(domain.getTransactionDate());
    rest.setDescription(domain.getDescription());
    rest.setType(EnumMapper.mapEnum(domain.getType(), CashTransactionType.class));
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);

    return rest;
  }

  public List<CashTransaction> toRestCashTransactions(List<com.example.demo.model.money.CashTransaction> domains) {
    return domains.stream().map(this::toRestCashTransaction).toList();
  }
}
