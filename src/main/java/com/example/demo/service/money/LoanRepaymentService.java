package com.example.demo.service.money;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.LoanRepaymentCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.money.Loan;
import com.example.demo.model.money.LoanRepayment;
import com.example.demo.repository.money.LoanRepaymentRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.MoneyValidator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanRepaymentService {

  private final LoanRepaymentRepository loanRepaymentRepository;
  private final LoanService loanService;
  private final ModificationUtils modificationUtils;
  private final MoneyValidator moneyValidator;

  public Optional<LoanRepayment> findById(String id) {
    return loanRepaymentRepository.findById(id);
  }

  public Page<LoanRepayment> findAll(
      PageFromOne page, BoundedPageSize pageSize, LoanRepaymentCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return loanRepaymentRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<LoanRepayment> createOrUpdateAll(List<LoanRepayment> repayments) {
    moneyValidator.validateLoanRepayments(repayments);
    List<LoanRepayment> processedRepayments = new ArrayList<>();
    for (LoanRepayment repayment : repayments) {
      Loan loan = repayment.getLoan();
      Loan existingLoan = loanService.findById(loan.getId()).orElse(null);
      if (existingLoan == null) {
        throw new NotFoundException("Loan with id " + loan.getId() + " not found");
      }
      repayment.setLoan(existingLoan);

      LoanRepayment existingRepayment =
          loanRepaymentRepository.findById(repayment.getId()).orElse(null);

      if (existingRepayment == null) {
        calculateInterestPortion(repayment, existingLoan);
      } else {
        repayment.setPrincipalPortion(existingRepayment.getPrincipalPortion());
        repayment.setInterestPortion(existingRepayment.getInterestPortion());
      }

      modificationUtils.createOrUpdateModel(
          repayment, existingRepayment, repayment.getId(), modificationUtils.takePrimaryUser());
      processedRepayments.add(repayment);
    }
    return loanRepaymentRepository.saveAll(processedRepayments);
  }

  @Transactional
  public void deleteById(String id) {
    loanRepaymentRepository.deleteById(id);
  }

  public List<LoanRepayment> findByLoanId(String loanId) {
    return loanRepaymentRepository.findByLoanIdOrderByPaymentDateAsc(loanId);
  }

  private void calculateInterestPortion(LoanRepayment repayment, Loan loan) {
    LocalDate paymentDate = repayment.getPaymentDate();
    List<LoanRepayment> previousRepayments =
        loanRepaymentRepository.findByLoanIdOrderByPaymentDateAsc(loan.getId());

    LocalDate lastEventDate = loan.getStartDate();
    int totalPrincipalPaid = 0;

    for (LoanRepayment prev : previousRepayments) {
      totalPrincipalPaid += prev.getPrincipalPortion();
      if (prev.getPaymentDate().isBefore(paymentDate)
          || prev.getPaymentDate().isEqual(paymentDate)) {
        lastEventDate = prev.getPaymentDate();
      }
    }

    int outstandingPrincipal = loan.getAmount() - totalPrincipalPaid;

    long daysBetween = ChronoUnit.DAYS.between(lastEventDate, paymentDate);
    if (daysBetween < 0) {
      daysBetween = 0;
    }

    BigDecimal interest =
        BigDecimal.valueOf(outstandingPrincipal)
            .multiply(BigDecimal.valueOf(loan.getInterestRate()))
            .multiply(BigDecimal.valueOf(daysBetween))
            .divide(BigDecimal.valueOf(365 * 10000), RoundingMode.HALF_UP);

    int interestPortion = interest.intValue();
    int repaymentAmount = repayment.getAmount();

    int principalPortion;
    if (repaymentAmount <= interestPortion) {
      principalPortion = 0;
      interestPortion = repaymentAmount;
    } else {
      principalPortion = repaymentAmount - interestPortion;
      if (principalPortion > outstandingPrincipal) {
        principalPortion = outstandingPrincipal;
        interestPortion = repaymentAmount - principalPortion;
      }
    }

    repayment.setPrincipalPortion(principalPortion);
    repayment.setInterestPortion(interestPortion);
  }

  private Specification<LoanRepayment> toSpecification(LoanRepaymentCriteria criteria) {
    return (root, query, cb) -> {
      List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

      if (criteria.getLoanId() != null) {
        predicates.add(cb.equal(root.get("loan").get("id"), criteria.getLoanId()));
      }

      return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };
  }
}
