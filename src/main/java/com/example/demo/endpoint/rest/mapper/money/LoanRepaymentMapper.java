package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateLoanRepayment;
import com.example.demo.client.model.LoanRepayment;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.service.money.LoanService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LoanRepaymentMapper {

  private final LoanService loanService;
  private final LoanMapper loanMapper;

  public com.example.demo.model.money.LoanRepayment toDomain(LoanRepayment restRepayment) {
    if (restRepayment == null) return null;

    return com.example.demo.model.money.LoanRepayment.builder()
        .id(restRepayment.getId())
        .paymentDate(restRepayment.getPaymentDate())
        .amount(restRepayment.getAmount())
        .principalPortion(restRepayment.getPrincipalPortion())
        .interestPortion(restRepayment.getInterestPortion())
        .comment(restRepayment.getComment())
        .loan(
            restRepayment.getLoan() != null && restRepayment.getLoan().getId() != null
                ? loanService.findById(restRepayment.getLoan().getId()).orElse(null)
                : null)
        .build();
  }

  public com.example.demo.model.money.LoanRepayment toDomain(CrupdateLoanRepayment restRepayment) {
    if (restRepayment == null) return null;

    return com.example.demo.model.money.LoanRepayment.builder()
        .id(restRepayment.getId())
        .paymentDate(restRepayment.getPaymentDate())
        .amount(restRepayment.getAmount())
        .comment(restRepayment.getComment())
        .loan(
            restRepayment.getLoanId() != null
                ? loanService.findById(restRepayment.getLoanId()).orElse(null)
                : null)
        .build();
  }

  public LoanRepayment toRestLoanRepayment(
      com.example.demo.model.money.LoanRepayment domainRepayment) {
    if (domainRepayment == null) return null;

    LoanRepayment restRepayment = new LoanRepayment();
    restRepayment.setId(domainRepayment.getId());
    restRepayment.setPaymentDate(domainRepayment.getPaymentDate());
    restRepayment.setAmount(domainRepayment.getAmount());
    restRepayment.setPrincipalPortion(domainRepayment.getPrincipalPortion());
    restRepayment.setInterestPortion(domainRepayment.getInterestPortion());
    restRepayment.setLoan(loanMapper.toCrupdateLoan(domainRepayment.getLoan()));
    RestAuditMapperUtils.mapAuditFields(
        domainRepayment,
        restRepayment::setCreatedAt,
        restRepayment::setUpdatedAt,
        restRepayment::setComment,
        restRepayment::setCreatedBy,
        restRepayment::setUpdatedBy);
    return restRepayment;
  }
}
