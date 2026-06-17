package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.endpoint.rest.mapper.CompanyMapper;
import com.example.demo.model.Company;
import com.example.demo.model.money.Organization;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrganizationMapper {

  private final CompanyMapper companyMapper;

  public Organization toDomain(
      com.example.demo.client.model.CrupdateOrganization rest, String companyId) {
    if (rest == null) return null;
    return Organization.builder()
        .id(rest.getId())
        .name(rest.getName())
        .address(rest.getAddress())
        .email(rest.getEmail())
        .phone(rest.getPhone())
        .contactName(rest.getContactName())
        .company(companyId != null ? Company.builder().id(companyId).build() : null)
        .build();
  }

  public com.example.demo.client.model.Organization toRest(Organization domain) {
    if (domain == null) return null;
    var rest = new com.example.demo.client.model.Organization();
    rest.setId(domain.getId());
    rest.setName(domain.getName());
    rest.setAddress(domain.getAddress());
    rest.setEmail(domain.getEmail());
    rest.setPhone(domain.getPhone());
    rest.setContactName(domain.getContactName());
    rest.setCompany(companyMapper.toRestCrupdateCompany(domain.getCompany()));
    return rest;
  }

  public com.example.demo.client.model.CrupdateOrganization toCrupdate(Organization domain) {
    if (domain == null) return null;
    var rest = new com.example.demo.client.model.CrupdateOrganization();
    rest.setId(domain.getId());
    rest.setName(domain.getName());
    rest.setAddress(domain.getAddress());
    rest.setEmail(domain.getEmail());
    rest.setPhone(domain.getPhone());
    rest.setContactName(domain.getContactName());
    rest.setCompanyId(domain.getCompany() != null ? domain.getCompany().getId() : null);
    rest.setComment(domain.getComment());
    return rest;
  }
}
