package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateLoan;
import com.example.demo.client.model.Loan;
import com.example.demo.client.model.LoanRepayment;
import com.example.demo.endpoint.rest.mapper.JobMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.JobService;
import java.util.stream.Collectors;
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
        .dueDate(restLoan.getDueDate())
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
        .dueDate(restLoan.getDueDate())
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
        .dueDate(domainLoan.getDueDate())
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
    restLoan.setDueDate(domainLoan.getDueDate());
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

  public Loan toRestLoanWithDetails(com.example.demo.model.money.Loan domainLoan) {
    if (domainLoan == null) return null;

    Loan restLoan = toRestLoan(domainLoan);

    if (domainLoan.getRepayments() != null) {
      restLoan.setRepayments(
          domainLoan.getRepayments().stream()
              .map(this::toRestLoanRepaymentSlim)
              .collect(Collectors.toList()));
    }

    int sumRepayments =
        domainLoan.getRepayments() != null
            ? domainLoan.getRepayments().stream()
                .filter(r -> r.getAmount() != null)
                .mapToInt(com.example.demo.model.money.LoanRepayment::getAmount)
                .sum()
            : 0;
    Integer amount = domainLoan.getAmount();
    int remaining = amount != null ? amount - sumRepayments : 0;
    restLoan.setRemainingAmount(amount != null ? amount - sumRepayments : null);

    restLoan.setStatus(calculateStatus(remaining, domainLoan.getDueDate()));

    return restLoan;
  }

  private com.example.demo.client.model.LoanStatus calculateStatus(
      int remaining, java.time.LocalDate dueDate) {
    if (remaining <= 0) {
      return com.example.demo.client.model.LoanStatus.PAID;
    }
    if (dueDate == null) {
      return com.example.demo.client.model.LoanStatus.ACTIVE;
    }
    return dueDate.isBefore(java.time.LocalDate.now())
        ? com.example.demo.client.model.LoanStatus.DEFAULTED
        : com.example.demo.client.model.LoanStatus.ACTIVE;
  }

  private LoanRepayment toRestLoanRepaymentSlim(com.example.demo.model.money.LoanRepayment domain) {
    if (domain == null) return null;
    LoanRepayment rest = new LoanRepayment();
    rest.setId(domain.getId());
    rest.setPaymentDate(domain.getPaymentDate());
    rest.setAmount(domain.getAmount());
    rest.setPrincipalPortion(domain.getPrincipalPortion());
    rest.setInterestPortion(domain.getInterestPortion());
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);
    return rest;
  }
}
