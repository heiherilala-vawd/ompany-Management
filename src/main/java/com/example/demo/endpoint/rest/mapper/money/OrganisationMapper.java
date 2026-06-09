package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateOrganisation;
import com.example.demo.client.model.Organisation;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.model.Company;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OrganisationMapper {

  public com.example.demo.model.money.Organisation toDomain(CrupdateOrganisation dto, String companyId) {
    if (dto == null) return null;
    return com.example.demo.model.money.Organisation.builder()
        .id(dto.getId() != null ? dto.getId().toString() : null)
        .name(dto.getName())
        .address(dto.getAddress())
        .email(dto.getEmail())
        .phone(dto.getPhone())
        .company(companyId != null ? Company.builder().id(companyId).build() : null)
        .comment(dto.getComment())
        .build();
  }

  public Organisation toRest(com.example.demo.model.money.Organisation domain) {
    if (domain == null) return null;

    Organisation dto = new Organisation();
    dto.setId(UUID.fromString(domain.getId()));
    dto.setName(domain.getName());
    dto.setAddress(domain.getAddress());
    dto.setEmail(domain.getEmail());
    dto.setPhone(domain.getPhone());
    dto.setCompanyId(domain.getCompany() != null ? domain.getCompany().getId() : null);
    RestAuditMapperUtils.mapAuditFields(
        domain,
        dto::setCreatedAt,
        dto::setUpdatedAt,
        dto::setComment,
        dto::setCreatedBy,
        dto::setUpdatedBy);
    return dto;
  }

  public List<Organisation> toRest(List<com.example.demo.model.money.Organisation> domains) {
    return domains.stream().map(this::toRest).toList();
  }

  public CrupdateOrganisation toCrupdate(com.example.demo.model.money.Organisation domain) {
    if (domain == null) return null;
    return new CrupdateOrganisation()
        .id(UUID.fromString(domain.getId()))
        .name(domain.getName())
        .address(domain.getAddress())
        .email(domain.getEmail())
        .phone(domain.getPhone())
        .comment(domain.getComment());
  }
}
