package com.example.demo.endpoint.rest.mapper.core;

import com.example.demo.client.model.CrupdateDepartment;
import com.example.demo.client.model.Department;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.model.Company;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DepartmentMapper {

  public com.example.demo.model.core.Department toDomain(
      CrupdateDepartment rest, String companyId) {
    if (rest == null) return null;

    return com.example.demo.model.core.Department.builder()
        .id(rest.getId())
        .name(rest.getName())
        .description(rest.getDescription())
        .company(companyId != null ? Company.builder().id(companyId).build() : null)
        .comment(rest.getComment())
        .build();
  }

  public Department toRestDepartment(com.example.demo.model.core.Department domain) {
    if (domain == null) return null;

    Department rest = new Department();
    rest.setId(domain.getId());
    rest.setName(domain.getName());
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

  public List<Department> toRestDepartments(List<com.example.demo.model.core.Department> domains) {
    return domains.stream().map(this::toRestDepartment).toList();
  }
}
