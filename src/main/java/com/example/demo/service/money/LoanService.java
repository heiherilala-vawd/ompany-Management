package com.example.demo.service.money;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.LoanCriteria;
import com.example.demo.model.money.Loan;
import com.example.demo.repository.money.LoanRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.MoneyValidator;
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
public class LoanService {

  private final LoanRepository loanRepository;
  private final ModificationUtils modificationUtils;
  private final MoneyValidator moneyValidator;

  public Optional<Loan> findById(String id) {
    return loanRepository.findById(id);
  }

  public Page<Loan> findAll(PageFromOne page, BoundedPageSize pageSize, LoanCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return loanRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<Loan> createOrUpdateAll(List<Loan> loans) {
    moneyValidator.validateLoans(loans);
    List<Loan> processedLoans = new ArrayList<>();
    for (Loan loan : loans) {
      Loan existingLoan = loanRepository.findById(loan.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          loan, existingLoan, loan.getId(), modificationUtils.takePrimaryUser());
      processedLoans.add(loan);
    }
    return loanRepository.saveAll(processedLoans);
  }

  @Transactional
  public void deleteById(String id) {
    loanRepository.deleteById(id);
  }

  private Specification<Loan> toSpecification(LoanCriteria criteria) {
    return (root, query, cb) -> {
      List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

      if (criteria.getDescription() != null && !criteria.getDescription().isBlank()) {
        predicates.add(
            cb.like(
                cb.lower(root.get("description")),
                "%" + criteria.getDescription().toLowerCase() + "%"));
      }
      if (criteria.getAmount() != null) {
        predicates.add(cb.equal(root.get("amount"), criteria.getAmount()));
      }
      if (criteria.getLender() != null && !criteria.getLender().isBlank()) {
        predicates.add(
            cb.like(cb.lower(root.get("lender")), "%" + criteria.getLender().toLowerCase() + "%"));
      }
      if (criteria.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), Loan.LoanStatus.valueOf(criteria.getStatus())));
      }
      if (criteria.getJobId() != null) {
        predicates.add(cb.equal(root.get("job").get("id"), criteria.getJobId()));
      }

      return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };
  }
}
