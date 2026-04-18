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
        .companyType(
            restCompany.getCompanyType() != null
                ? com.example.demo.model.Company.CompanyType.valueOf(
                    restCompany.getCompanyType().name())
                : null)
        .build();
  }

  public com.example.demo.model.Company toDomain(CrupdateCompany restCompany) {
    if (restCompany == null) return null;

    return com.example.demo.model.Company.builder()
        .id(restCompany.getId())
        .name(restCompany.getName())
        .rib(restCompany.getRib())
        .description(restCompany.getDescription())
        .companyType(
            restCompany.getCompanyType() != null
                ? com.example.demo.model.Company.CompanyType.valueOf(
                    restCompany.getCompanyType().name())
                : null)
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
        domainCompany.getCompanyType() != null
            ? CompanyType.valueOf(domainCompany.getCompanyType().name())
            : null);
    restCompany.setCreatedAt(domainCompany.getCreatedAt());
    restCompany.setUpdatedAt(domainCompany.getUpdatedAt());
    restCompany.setComment(domainCompany.getComment());

    if (domainCompany.getCreatedBy() != null) {
      restCompany.setCreatedBy(domainCompany.getCreatedBy().getId());
    }
    if (domainCompany.getUpdatedBy() != null) {
      restCompany.setUpdatedBy(domainCompany.getUpdatedBy().getId());
    }

    return restCompany;
  }

  public List<Company> toRestCompanies(List<com.example.demo.model.Company> domainCompanies) {
    return domainCompanies.stream().map(this::toRestCompany).toList();
  }

  public List<com.example.demo.model.Company> toDomain(List<Company> restCompanies) {
    return restCompanies.stream().map(this::toDomain).toList();
  }
}
