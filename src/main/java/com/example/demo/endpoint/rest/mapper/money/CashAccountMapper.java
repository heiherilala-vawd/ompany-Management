package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CashAccount;
import com.example.demo.client.model.CrupdateCashAccount;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.model.Company;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CashAccountMapper {

  public com.example.demo.model.money.CashAccount toDomain(CrupdateCashAccount rest) {
    if (rest == null) return null;

    return com.example.demo.model.money.CashAccount.builder()
        .id(rest.getId())
        .name(rest.getName())
        .balance(rest.getBalance())
        .description(rest.getDescription())
        .company(rest.getCompanyId() != null ? Company.builder().id(rest.getCompanyId()).build() : null)
        .comment(rest.getComment())
        .build();
  }

  public CashAccount toRestCashAccount(com.example.demo.model.money.CashAccount domain) {
    if (domain == null) return null;

    CashAccount rest = new CashAccount();
    rest.setId(domain.getId());
    rest.setName(domain.getName());
    rest.setBalance(domain.getBalance());
    rest.setDescription(domain.getDescription());
    rest.setCompanyId(domain.getCompany() != null ? domain.getCompany().getId() : null);
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);

    return rest;
  }

  public List<CashAccount> toRestCashAccounts(List<com.example.demo.model.money.CashAccount> domains) {
    return domains.stream().map(this::toRestCashAccount).toList();
  }
}
