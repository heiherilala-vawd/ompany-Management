package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CashAccount;
import com.example.demo.client.model.CrupdateCashAccount;
import com.example.demo.endpoint.rest.mapper.CompanyMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.model.Company;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CashAccountMapper {

  private final CompanyMapper companyMapper;

  public com.example.demo.model.money.CashAccount toDomain(
      CrupdateCashAccount rest, String companyId) {
    if (rest == null) return null;

    return com.example.demo.model.money.CashAccount.builder()
        .id(rest.getId())
        .name(rest.getName())
        .balance(rest.getBalance())
        .description(rest.getDescription())
        .company(companyId != null ? Company.builder().id(companyId).build() : null)
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
    rest.setCompany(companyMapper.toRestCrupdateCompany(domain.getCompany()));
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);

    return rest;
  }

  public CrupdateCashAccount toRestCrupdateCashAccount(
      com.example.demo.model.money.CashAccount domain) {
    if (domain == null) return null;
    return new CrupdateCashAccount()
        .id(domain.getId())
        .name(domain.getName())
        .balance(domain.getBalance())
        .description(domain.getDescription())
        .companyId(domain.getCompany() != null ? domain.getCompany().getId() : null)
        .comment(domain.getComment());
  }

  public List<CashAccount> toRestCashAccounts(
      List<com.example.demo.model.money.CashAccount> domains) {
    return domains.stream().map(this::toRestCashAccount).toList();
  }
}
