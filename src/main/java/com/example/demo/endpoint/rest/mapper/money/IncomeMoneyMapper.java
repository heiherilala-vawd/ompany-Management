package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateIncomeMoney;
import com.example.demo.client.model.IncomeMoney;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class IncomeMoneyMapper {

  public com.example.demo.model.money.IncomeMoney toDomain(IncomeMoney restIncome) {
    if (restIncome == null) return null;

    return com.example.demo.model.money.IncomeMoney.builder()
        .id(restIncome.getId())
        .sourceOrganization(restIncome.getSourceOrganization())
        .invoiceReference(restIncome.getInvoiceReference())
        .amount(restIncome.getAmount())
        .description(restIncome.getDescription())
        .comment(restIncome.getComment())
        .build();
  }

  public com.example.demo.model.money.IncomeMoney toDomain(CrupdateIncomeMoney restIncome) {
    if (restIncome == null) return null;

    return com.example.demo.model.money.IncomeMoney.builder()
        .id(restIncome.getId())
        .sourceOrganization(restIncome.getSourceOrganization())
        .invoiceReference(restIncome.getInvoiceReference())
        .amount(restIncome.getAmount())
        .description(restIncome.getDescription())
        .comment(restIncome.getComment())
        .build();
  }

  public IncomeMoney toRestIncome(com.example.demo.model.money.IncomeMoney domainIncome) {
    if (domainIncome == null) return null;

    IncomeMoney restIncome = new IncomeMoney();
    restIncome.setId(domainIncome.getId());
    restIncome.setSourceOrganization(domainIncome.getSourceOrganization());
    restIncome.setInvoiceReference(domainIncome.getInvoiceReference());
    restIncome.setAmount(domainIncome.getAmount());
    restIncome.setDescription(domainIncome.getDescription());
    RestAuditMapperUtils.mapAuditFields(
        domainIncome,
        restIncome::setCreatedAt,
        restIncome::setUpdatedAt,
        restIncome::setComment,
        restIncome::setCreatedBy,
        restIncome::setUpdatedBy);
    return restIncome;
  }
}
