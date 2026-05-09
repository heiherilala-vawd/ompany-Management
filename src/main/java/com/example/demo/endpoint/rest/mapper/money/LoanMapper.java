package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateLoan;
import com.example.demo.client.model.Loan;
import com.example.demo.endpoint.rest.mapper.JobMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.JobService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LoanMapper {

  private final JobService jobService;
  private final JobMapper jobMapper;

  public com.example.demo.model.money.Loan toDomain(Loan restLoan) {
    if (restLoan == null) return null;

    return com.example.demo.model.money.Loan.builder()
        .id(restLoan.getId())
        .lender(restLoan.getLender())
        .interestRate(restLoan.getInterestRate())
        .startDate(restLoan.getStartDate())
        .status(
            restLoan.getStatus() != null
                ? com.example.demo.model.money.Loan.LoanStatus.valueOf(restLoan.getStatus().name())
                : null)
        .amount(restLoan.getAmount())
        .description(restLoan.getDescription())
        .comment(restLoan.getComment())
        .job(
            restLoan.getJob() != null && restLoan.getJob().getId() != null
                ? jobService.findById(restLoan.getJob().getId()).orElse(null)
                : null)
        .build();
  }

  public com.example.demo.model.money.Loan toDomain(CrupdateLoan restLoan) {
    if (restLoan == null) return null;

    return com.example.demo.model.money.Loan.builder()
        .id(restLoan.getId())
        .lender(restLoan.getLender())
        .interestRate(restLoan.getInterestRate())
        .startDate(restLoan.getStartDate())
        .status(
            restLoan.getStatus() != null
                ? com.example.demo.model.money.Loan.LoanStatus.valueOf(restLoan.getStatus().name())
                : com.example.demo.model.money.Loan.LoanStatus.ACTIVE)
        .amount(restLoan.getAmount())
        .description(restLoan.getDescription())
        .comment(restLoan.getComment())
        .job(
            restLoan.getJobId() != null
                ? jobService.findById(restLoan.getJobId()).orElse(null)
                : null)
        .build();
  }

  public CrupdateLoan toCrupdateLoan(com.example.demo.model.money.Loan domainLoan) {
    if (domainLoan == null) return null;

    return new CrupdateLoan()
        .id(domainLoan.getId())
        .lender(domainLoan.getLender())
        .interestRate(domainLoan.getInterestRate())
        .startDate(domainLoan.getStartDate())
        .status(
            domainLoan.getStatus() != null
                ? com.example.demo.client.model.LoanStatus.valueOf(domainLoan.getStatus().name())
                : null)
        .amount(domainLoan.getAmount())
        .description(domainLoan.getDescription())
        .jobId(domainLoan.getJob() != null ? domainLoan.getJob().getId() : null)
        .comment(domainLoan.getComment());
  }

  public Loan toRestLoan(com.example.demo.model.money.Loan domainLoan) {
    if (domainLoan == null) return null;

    Loan restLoan = new Loan();
    restLoan.setId(domainLoan.getId());
    restLoan.setLender(domainLoan.getLender());
    restLoan.setInterestRate(domainLoan.getInterestRate());
    restLoan.setStartDate(domainLoan.getStartDate());
    restLoan.setStatus(
        domainLoan.getStatus() != null
            ? com.example.demo.client.model.LoanStatus.valueOf(domainLoan.getStatus().name())
            : null);
    restLoan.setAmount(domainLoan.getAmount());
    restLoan.setDescription(domainLoan.getDescription());
    restLoan.setJob(jobMapper.toRestCrupdateJob(domainLoan.getJob()));
    RestAuditMapperUtils.mapAuditFields(
        domainLoan,
        restLoan::setCreatedAt,
        restLoan::setUpdatedAt,
        restLoan::setComment,
        restLoan::setCreatedBy,
        restLoan::setUpdatedBy);
    return restLoan;
  }
}
