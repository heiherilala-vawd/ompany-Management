package com.example.demo.endpoint.rest.mapper.hr;

import com.example.demo.client.model.CrupdateLeaveType;
import com.example.demo.client.model.LeaveType;
import com.example.demo.endpoint.rest.mapper.CompanyMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.model.Company;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LeaveTypeMapper {

  private final CompanyMapper companyMapper;

  public com.example.demo.model.hr.LeaveType toDomain(LeaveType rest) {
    if (rest == null) return null;

    return com.example.demo.model.hr.LeaveType.builder()
        .id(rest.getId())
        .name(rest.getName())
        .description(rest.getDescription())
        .paid(rest.getPaid())
        .deductFromBalance(rest.getDeductFromBalance())
        .color(rest.getColor())
        .daysPerYear(rest.getDaysPerYear())
        .company(
            rest.getCompany() != null
                ? Company.builder().id(rest.getCompany().getId()).build()
                : null)
        .build();
  }

  public com.example.demo.model.hr.LeaveType toDomain(CrupdateLeaveType rest, String companyId) {
    if (rest == null) return null;

    return com.example.demo.model.hr.LeaveType.builder()
        .id(rest.getId())
        .name(rest.getName())
        .description(rest.getDescription())
        .paid(rest.getPaid())
        .deductFromBalance(rest.getDeductFromBalance())
        .color(rest.getColor())
        .daysPerYear(rest.getDaysPerYear())
        .company(companyId != null ? Company.builder().id(companyId).build() : null)
        .comment(rest.getComment())
        .build();
  }

  public LeaveType toRestLeaveType(com.example.demo.model.hr.LeaveType domain) {
    if (domain == null) return null;

    LeaveType rest = new LeaveType();
    rest.setId(domain.getId());
    rest.setName(domain.getName());
    rest.setDescription(domain.getDescription());
    rest.setPaid(domain.getPaid());
    rest.setDeductFromBalance(domain.getDeductFromBalance());
    rest.setColor(domain.getColor());
    rest.setDaysPerYear(domain.getDaysPerYear());
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

  public CrupdateLeaveType toRestCrupdateLeaveType(com.example.demo.model.hr.LeaveType domain) {
    if (domain == null) return null;

    return new CrupdateLeaveType()
        .id(domain.getId())
        .name(domain.getName())
        .description(domain.getDescription())
        .paid(domain.getPaid())
        .deductFromBalance(domain.getDeductFromBalance())
        .color(domain.getColor())
        .daysPerYear(domain.getDaysPerYear())
        .companyId(domain.getCompany() != null ? domain.getCompany().getId() : null)
        .comment(domain.getComment());
  }
}
