package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateIncomeMoney;
import com.example.demo.client.model.IncomeMoney;
import com.example.demo.endpoint.rest.mapper.JobMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.JobService;
import com.example.demo.service.money.IncomeTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class IncomeMoneyMapper {

  private final JobService jobService;
  private final JobMapper jobMapper;
  private final IncomeTypeService incomeTypeService;
  private final IncomeTypeMapper incomeTypeMapper;

  public com.example.demo.model.money.IncomeMoney toDomain(IncomeMoney restIncome) {
    if (restIncome == null) return null;

    return com.example.demo.model.money.IncomeMoney.builder()
        .id(restIncome.getId())
        .sourceOrganization(restIncome.getSourceOrganization())
        .invoiceReference(restIncome.getInvoiceReference())
        .billingStartDate(restIncome.getBillingStartDate())
        .moneyArrivalDate(restIncome.getMoneyArrivalDate())
        .amount(restIncome.getAmount())
        .description(restIncome.getDescription())
        .comment(restIncome.getComment())
        .job(
            restIncome.getJob() != null && restIncome.getJob().getId() != null
                ? jobService.findById(restIncome.getJob().getId()).orElse(null)
                : null)
        .incomeType(
            restIncome.getIncomeType() != null && restIncome.getIncomeType().getId() != null
                ? incomeTypeService.findById(restIncome.getIncomeType().getId()).orElse(null)
                : null)
        .build();
  }

  public com.example.demo.model.money.IncomeMoney toDomain(CrupdateIncomeMoney restIncome) {
    if (restIncome == null) return null;

    return com.example.demo.model.money.IncomeMoney.builder()
        .id(restIncome.getId())
        .sourceOrganization(restIncome.getSourceOrganization())
        .invoiceReference(restIncome.getInvoiceReference())
        .billingStartDate(restIncome.getBillingStartDate())
        .moneyArrivalDate(restIncome.getMoneyArrivalDate())
        .amount(restIncome.getAmount())
        .description(restIncome.getDescription())
        .comment(restIncome.getComment())
        .job(
            restIncome.getJobId() != null
                ? jobService.findById(restIncome.getJobId()).orElse(null)
                : null)
        .incomeType(
            restIncome.getIncomeTypeId() != null
                ? incomeTypeService.findById(restIncome.getIncomeTypeId()).orElse(null)
                : null)
        .build();
  }

  public IncomeMoney toRestIncome(com.example.demo.model.money.IncomeMoney domainIncome) {
    if (domainIncome == null) return null;

    IncomeMoney restIncome = new IncomeMoney();
    restIncome.setId(domainIncome.getId());
    restIncome.setSourceOrganization(domainIncome.getSourceOrganization());
    restIncome.setInvoiceReference(domainIncome.getInvoiceReference());
    restIncome.setBillingStartDate(domainIncome.getBillingStartDate());
    restIncome.setMoneyArrivalDate(domainIncome.getMoneyArrivalDate());
    restIncome.setAmount(domainIncome.getAmount());
    restIncome.setDescription(domainIncome.getDescription());
    restIncome.setJob(jobMapper.toRestCrupdateJob(domainIncome.getJob()));
    restIncome.setIncomeType(incomeTypeMapper.toRestIncomeType(domainIncome.getIncomeType()));
    RestAuditMapperUtils.mapAuditFields(
        domainIncome,
        restIncome::setCreatedAt,
        restIncome::setUpdatedAt,
        restIncome::setComment,
        restIncome::setCreatedBy,
        restIncome::setUpdatedBy);
    return restIncome;
  }
}
