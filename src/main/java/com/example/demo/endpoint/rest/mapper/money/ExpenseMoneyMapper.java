package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateExpenseMoney;
import com.example.demo.client.model.ExpenseMoney;
import com.example.demo.endpoint.rest.mapper.JobMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.JobService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ExpenseMoneyMapper {

  private final JobService jobService;
  private final JobMapper jobMapper;

  public com.example.demo.model.money.ExpenseMoney toDomain(ExpenseMoney restExpense) {
    if (restExpense == null) return null;

    return com.example.demo.model.money.ExpenseMoney.builder()
        .id(restExpense.getId())
        .amount(restExpense.getAmount())
        .description(restExpense.getDescription())
        .comment(restExpense.getComment())
        .job(
            restExpense.getJob() != null && restExpense.getJob().getId() != null
                ? jobService.findById(restExpense.getJob().getId()).orElse(null)
                : null)
        .build();
  }

  public com.example.demo.model.money.ExpenseMoney toDomain(CrupdateExpenseMoney restExpense) {
    if (restExpense == null) return null;

    return com.example.demo.model.money.ExpenseMoney.builder()
        .id(restExpense.getId())
        .amount(restExpense.getAmount())
        .description(restExpense.getDescription())
        .comment(restExpense.getComment())
        .job(
            restExpense.getJobId() != null
                ? jobService.findById(restExpense.getJobId()).orElse(null)
                : null)
        .build();
  }

  public ExpenseMoney toRestExpense(com.example.demo.model.money.ExpenseMoney domainExpense) {
    if (domainExpense == null) return null;

    ExpenseMoney restExpense = new ExpenseMoney();
    restExpense.setId(domainExpense.getId());
    restExpense.setAmount(domainExpense.getAmount());
    restExpense.setDescription(domainExpense.getDescription());
    restExpense.setJob(jobMapper.toRestCrupdateJob(domainExpense.getJob()));
    RestAuditMapperUtils.mapAuditFields(
        domainExpense,
        restExpense::setCreatedAt,
        restExpense::setUpdatedAt,
        restExpense::setComment,
        restExpense::setCreatedBy,
        restExpense::setUpdatedBy);

    return restExpense;
  }

  public CrupdateExpenseMoney toRestCrupdateExpense(
      com.example.demo.model.money.ExpenseMoney domainExpense) {
    if (domainExpense == null) return null;

    return new CrupdateExpenseMoney()
        .id(domainExpense.getId())
        .jobId(domainExpense.getJob() != null ? domainExpense.getJob().getId() : null)
        .amount(domainExpense.getAmount())
        .description(domainExpense.getDescription())
        .comment(domainExpense.getComment());
  }
}
