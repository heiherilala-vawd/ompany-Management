package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateIncomeType;
import com.example.demo.client.model.IncomeType;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.CompanyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class IncomeTypeMapper {

  private final CompanyService companyService;

  public com.example.demo.model.money.IncomeType toDomain(IncomeType restIncomeType) {
    if (restIncomeType == null) return null;

    return com.example.demo.model.money.IncomeType.builder()
        .id(restIncomeType.getId())
        .name(restIncomeType.getName())
        .description(restIncomeType.getDescription())
        .company(
            restIncomeType.getCompanyId() != null
                ? companyService.findById(restIncomeType.getCompanyId()).orElse(null)
                : null)
        .comment(restIncomeType.getComment())
        .build();
  }

  public com.example.demo.model.money.IncomeType toDomain(CrupdateIncomeType restIncomeType) {
    if (restIncomeType == null) return null;

    return com.example.demo.model.money.IncomeType.builder()
        .id(restIncomeType.getId())
        .name(restIncomeType.getName())
        .description(restIncomeType.getDescription())
        .company(
            restIncomeType.getCompanyId() != null
                ? companyService.findById(restIncomeType.getCompanyId()).orElse(null)
                : null)
        .comment(restIncomeType.getComment())
        .build();
  }

  public IncomeType toRestIncomeType(com.example.demo.model.money.IncomeType domainIncomeType) {
    if (domainIncomeType == null) return null;

    IncomeType restIncomeType = new IncomeType();
    restIncomeType.setId(domainIncomeType.getId());
    restIncomeType.setName(domainIncomeType.getName());
    restIncomeType.setDescription(domainIncomeType.getDescription());
    restIncomeType.setCompanyId(
        domainIncomeType.getCompany() != null ? domainIncomeType.getCompany().getId() : null);
    RestAuditMapperUtils.mapAuditFields(
        domainIncomeType,
        restIncomeType::setCreatedAt,
        restIncomeType::setUpdatedAt,
        restIncomeType::setComment,
        restIncomeType::setCreatedBy,
        restIncomeType::setUpdatedBy);
    return restIncomeType;
  }

  public List<IncomeType> toRestIncomeTypes(
      List<com.example.demo.model.money.IncomeType> domainIncomeTypes) {
    return domainIncomeTypes.stream().map(this::toRestIncomeType).toList();
  }
}
