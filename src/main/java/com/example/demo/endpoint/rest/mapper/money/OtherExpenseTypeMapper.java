package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateOtherExpenseType;
import com.example.demo.client.model.OtherExpenseType;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.CompanyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OtherExpenseTypeMapper {

  private final CompanyService companyService;

  public com.example.demo.model.money.OtherExpenseType toDomain(
      OtherExpenseType restOtherExpenseType) {
    if (restOtherExpenseType == null) return null;

    return com.example.demo.model.money.OtherExpenseType.builder()
        .id(restOtherExpenseType.getId())
        .name(restOtherExpenseType.getName())
        .description(restOtherExpenseType.getDescription())
        .company(
            restOtherExpenseType.getCompanyId() != null
                ? companyService.findById(restOtherExpenseType.getCompanyId()).orElse(null)
                : null)
        .comment(restOtherExpenseType.getComment())
        .build();
  }

  public com.example.demo.model.money.OtherExpenseType toDomain(
      CrupdateOtherExpenseType restOtherExpenseType) {
    if (restOtherExpenseType == null) return null;

    return com.example.demo.model.money.OtherExpenseType.builder()
        .id(restOtherExpenseType.getId())
        .name(restOtherExpenseType.getName())
        .description(restOtherExpenseType.getDescription())
        .company(
            restOtherExpenseType.getCompanyId() != null
                ? companyService.findById(restOtherExpenseType.getCompanyId()).orElse(null)
                : null)
        .comment(restOtherExpenseType.getComment())
        .build();
  }

  public OtherExpenseType toRestOtherExpenseType(
      com.example.demo.model.money.OtherExpenseType domainOtherExpenseType) {
    if (domainOtherExpenseType == null) return null;

    OtherExpenseType restOtherExpenseType = new OtherExpenseType();
    restOtherExpenseType.setId(domainOtherExpenseType.getId());
    restOtherExpenseType.setName(domainOtherExpenseType.getName());
    restOtherExpenseType.setDescription(domainOtherExpenseType.getDescription());
    restOtherExpenseType.setCompanyId(
        domainOtherExpenseType.getCompany() != null
            ? domainOtherExpenseType.getCompany().getId()
            : null);
    RestAuditMapperUtils.mapAuditFields(
        domainOtherExpenseType,
        restOtherExpenseType::setCreatedAt,
        restOtherExpenseType::setUpdatedAt,
        restOtherExpenseType::setComment,
        restOtherExpenseType::setCreatedBy,
        restOtherExpenseType::setUpdatedBy);
    return restOtherExpenseType;
  }

  public List<OtherExpenseType> toRestOtherExpenseTypes(
      List<com.example.demo.model.money.OtherExpenseType> domainOtherExpenseTypes) {
    return domainOtherExpenseTypes.stream().map(this::toRestOtherExpenseType).toList();
  }
}
