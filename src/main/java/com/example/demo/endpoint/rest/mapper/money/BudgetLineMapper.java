package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.BudgetLine;
import com.example.demo.client.model.CrupdateBudgetLine;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.model.Company;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BudgetLineMapper {

  public com.example.demo.model.money.BudgetLine toDomain(CrupdateBudgetLine rest) {
    if (rest == null) return null;

    return com.example.demo.model.money.BudgetLine.builder()
        .id(rest.getId())
        .company(
            rest.getCompanyId() != null ? Company.builder().id(rest.getCompanyId()).build() : null)
        .category(rest.getCategory())
        .plannedAmount(rest.getPlannedAmount())
        .actualAmount(rest.getActualAmount())
        .periodStart(rest.getPeriodStart())
        .periodEnd(rest.getPeriodEnd())
        .description(rest.getDescription())
        .comment(rest.getComment())
        .build();
  }

  public BudgetLine toRestBudgetLine(com.example.demo.model.money.BudgetLine domain) {
    if (domain == null) return null;

    BudgetLine rest = new BudgetLine();
    rest.setId(domain.getId());
    rest.setCompanyId(domain.getCompany() != null ? domain.getCompany().getId() : null);
    rest.setCategory(domain.getCategory());
    rest.setPlannedAmount(domain.getPlannedAmount());
    rest.setActualAmount(domain.getActualAmount());
    rest.setPeriodStart(domain.getPeriodStart());
    rest.setPeriodEnd(domain.getPeriodEnd());
    rest.setDescription(domain.getDescription());
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);

    return rest;
  }

  public List<BudgetLine> toRestBudgetLines(List<com.example.demo.model.money.BudgetLine> domains) {
    return domains.stream().map(this::toRestBudgetLine).toList();
  }
}
