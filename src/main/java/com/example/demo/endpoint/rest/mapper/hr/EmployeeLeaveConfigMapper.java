package com.example.demo.endpoint.rest.mapper.hr;

import com.example.demo.client.model.CrupdateEmployeeLeaveConfig;
import com.example.demo.client.model.EmployeeLeaveConfig;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.model.Company;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EmployeeLeaveConfigMapper {

  public com.example.demo.model.hr.EmployeeLeaveConfig toDomain(
      CrupdateEmployeeLeaveConfig rest, String companyId) {
    if (rest == null) return null;

    return com.example.demo.model.hr.EmployeeLeaveConfig.builder()
        .id(rest.getId())
        .company(companyId != null ? Company.builder().id(companyId).build() : null)
        .hireDate(rest.getHireDate())
        .contractType(rest.getContractType())
        .vacationDaysPerMonth(rest.getVacationDaysPerMonth())
        .comment(rest.getComment())
        .build();
  }

  public EmployeeLeaveConfig toRestEmployeeLeaveConfig(
      com.example.demo.model.hr.EmployeeLeaveConfig domain) {
    if (domain == null) return null;

    EmployeeLeaveConfig rest = new EmployeeLeaveConfig();
    rest.setId(domain.getId());
    rest.setHireDate(domain.getHireDate());
    rest.setContractType(domain.getContractType());
    rest.setVacationDaysPerMonth(domain.getVacationDaysPerMonth());
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);

    return rest;
  }

  public CrupdateEmployeeLeaveConfig toRestCrupdateEmployeeLeaveConfig(
      com.example.demo.model.hr.EmployeeLeaveConfig domain) {
    if (domain == null) return null;

    return new CrupdateEmployeeLeaveConfig()
        .id(domain.getId())
        .hireDate(domain.getHireDate())
        .contractType(domain.getContractType())
        .vacationDaysPerMonth(domain.getVacationDaysPerMonth())
        .comment(domain.getComment());
  }
}
