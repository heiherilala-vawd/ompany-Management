package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateIncomeMoney;
import com.example.demo.client.model.IncomeMoney;
import com.example.demo.service.money.MonetaryMovementService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class IncomeMoneyMapper {

  private final MonetaryMovementService monetaryMovementService;

  public com.example.demo.model.money.IncomeMoney toDomain(IncomeMoney restIncome) {
    if (restIncome == null) return null;

    return com.example.demo.model.money.IncomeMoney.builder()
        .id(restIncome.getId())
        .monetaryMovement(
            restIncome.getMonetaryId() != null
                ? monetaryMovementService.findById(restIncome.getMonetaryId()).orElse(null)
                : null)
        .sourceOrganization(restIncome.getSourceOrganization())
        .invoiceReference(restIncome.getInvoiceReference())
        .build();
  }

  public com.example.demo.model.money.IncomeMoney toDomain(CrupdateIncomeMoney restIncome) {
    if (restIncome == null) return null;

    return com.example.demo.model.money.IncomeMoney.builder()
        .id(restIncome.getId())
        .monetaryMovement(
            restIncome.getMonetaryId() != null
                ? monetaryMovementService.findById(restIncome.getMonetaryId()).orElse(null)
                : null)
        .sourceOrganization(restIncome.getSourceOrganization())
        .invoiceReference(restIncome.getInvoiceReference())
        .build();
  }

  public IncomeMoney toRestIncome(com.example.demo.model.money.IncomeMoney domainIncome) {
    if (domainIncome == null) return null;

    IncomeMoney restIncome = new IncomeMoney();
    restIncome.setId(domainIncome.getId());
    restIncome.setMonetaryId(
        domainIncome.getMonetaryMovement() != null
            ? domainIncome.getMonetaryMovement().getId()
            : null);
    restIncome.setSourceOrganization(domainIncome.getSourceOrganization());
    restIncome.setInvoiceReference(domainIncome.getInvoiceReference());

    return restIncome;
  }
}
