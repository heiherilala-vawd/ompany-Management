package com.example.demo.endpoint.rest.mapper;

import com.example.demo.client.model.Company;
import com.example.demo.client.model.CompanyType;
import com.example.demo.client.model.CrupdateCompany;
import com.example.demo.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CompanyMapper {

  private final UserService userService;

  public com.example.demo.model.Company toDomain(Company restCompany) {
    if (restCompany == null) return null;

    return com.example.demo.model.Company.builder()
        .id(restCompany.getId())
        .name(restCompany.getName())
        .rib(restCompany.getRib())
        .description(restCompany.getDescription())
        .comment(restCompany.getComment())
        .companyType(
            EnumMapper.mapEnum(
                restCompany.getCompanyType(), com.example.demo.model.Company.CompanyType.class))
        .build();
  }

  public com.example.demo.model.Company toDomain(CrupdateCompany restCompany) {
    if (restCompany == null) return null;

    return com.example.demo.model.Company.builder()
        .id(restCompany.getId())
        .name(restCompany.getName())
        .rib(restCompany.getRib())
        .description(restCompany.getDescription())
        .comment(restCompany.getComment())
        .companyType(
            EnumMapper.mapEnum(
                restCompany.getCompanyType(), com.example.demo.model.Company.CompanyType.class))
        .build();
  }

  public Company toRestCompany(com.example.demo.model.Company domainCompany) {
    if (domainCompany == null) return null;

    Company restCompany = new Company();
    restCompany.setId(domainCompany.getId());
    restCompany.setName(domainCompany.getName());
    restCompany.setRib(domainCompany.getRib());
    restCompany.setDescription(domainCompany.getDescription());
    restCompany.setCompanyType(
        EnumMapper.mapEnum(domainCompany.getCompanyType(), CompanyType.class));
    RestAuditMapperUtils.mapAuditFields(
        domainCompany,
        restCompany::setCreatedAt,
        restCompany::setUpdatedAt,
        restCompany::setComment,
        restCompany::setCreatedBy,
        restCompany::setUpdatedBy);

    return restCompany;
  }

  public CrupdateCompany toRestCrupdateCompany(com.example.demo.model.Company domainCompany) {
    if (domainCompany == null) return null;

    return new CrupdateCompany()
        .id(domainCompany.getId())
        .name(domainCompany.getName())
        .rib(domainCompany.getRib())
        .description(domainCompany.getDescription())
        .companyType(EnumMapper.mapEnum(domainCompany.getCompanyType(), CompanyType.class))
        .comment(domainCompany.getComment());
  }

  public List<Company> toRestCompanies(List<com.example.demo.model.Company> domainCompanies) {
    return domainCompanies.stream().map(this::toRestCompany).toList();
  }

  public List<com.example.demo.model.Company> toDomain(List<Company> restCompanies) {
    return restCompanies.stream().map(this::toDomain).toList();
  }
}
