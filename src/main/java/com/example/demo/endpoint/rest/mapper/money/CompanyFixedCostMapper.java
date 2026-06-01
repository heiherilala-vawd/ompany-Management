package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CompanyFixedCost;
import com.example.demo.client.model.CrupdateCompanyFixedCost;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.CompanyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CompanyFixedCostMapper {

  private final CompanyService companyService;

  public com.example.demo.model.money.CompanyFixedCost toDomain(CompanyFixedCost restFixedCost) {
    if (restFixedCost == null) return null;

    return com.example.demo.model.money.CompanyFixedCost.builder()
        .id(restFixedCost.getId())
        .name(restFixedCost.getName())
        .amount(restFixedCost.getAmount())
        .description(restFixedCost.getDescription())
        .company(
            restFixedCost.getCompanyId() != null
                ? companyService.findById(restFixedCost.getCompanyId()).orElse(null)
                : null)
        .startDate(restFixedCost.getStartDate())
        .endDate(restFixedCost.getEndDate())
        .comment(restFixedCost.getComment())
        .build();
  }

  public com.example.demo.model.money.CompanyFixedCost toDomain(
      CrupdateCompanyFixedCost restFixedCost, String companyId) {
    if (restFixedCost == null) return null;

    return com.example.demo.model.money.CompanyFixedCost.builder()
        .id(restFixedCost.getId())
        .name(restFixedCost.getName())
        .amount(restFixedCost.getAmount())
        .description(restFixedCost.getDescription())
        .company(companyId != null ? companyService.findById(companyId).orElse(null) : null)
        .startDate(restFixedCost.getStartDate())
        .endDate(restFixedCost.getEndDate())
        .comment(restFixedCost.getComment())
        .build();
  }

  public CompanyFixedCost toRestCompanyFixedCost(
      com.example.demo.model.money.CompanyFixedCost domainFixedCost) {
    if (domainFixedCost == null) return null;

    CompanyFixedCost restFixedCost = new CompanyFixedCost();
    restFixedCost.setId(domainFixedCost.getId());
    restFixedCost.setName(domainFixedCost.getName());
    restFixedCost.setAmount(domainFixedCost.getAmount());
    restFixedCost.setDescription(domainFixedCost.getDescription());
    restFixedCost.setCompanyId(
        domainFixedCost.getCompany() != null ? domainFixedCost.getCompany().getId() : null);
    restFixedCost.setStartDate(domainFixedCost.getStartDate());
    restFixedCost.setEndDate(domainFixedCost.getEndDate());
    RestAuditMapperUtils.mapAuditFields(
        domainFixedCost,
        restFixedCost::setCreatedAt,
        restFixedCost::setUpdatedAt,
        restFixedCost::setComment,
        restFixedCost::setCreatedBy,
        restFixedCost::setUpdatedBy);
    return restFixedCost;
  }

  public List<CompanyFixedCost> toRestCompanyFixedCosts(
      List<com.example.demo.model.money.CompanyFixedCost> domainFixedCosts) {
    return domainFixedCosts.stream().map(this::toRestCompanyFixedCost).toList();
  }
}
