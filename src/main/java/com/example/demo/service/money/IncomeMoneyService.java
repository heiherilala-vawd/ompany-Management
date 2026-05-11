package com.example.demo.service.money;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.IncomeMoneyCriteria;
import com.example.demo.model.money.IncomeMoney;
import com.example.demo.model.money.IncomeReceipt;
import com.example.demo.repository.money.IncomeMoneyRepository;
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
public class IncomeMoneyService {

  private final IncomeMoneyRepository incomeMoneyRepository;
  private final ModificationUtils modificationUtils;
  private final MoneyValidator moneyValidator;

  public Optional<IncomeMoney> findById(String id) {
    return incomeMoneyRepository.findById(id);
  }

  public Integer sumByJobId(String jobId) {
    return incomeMoneyRepository.sumByJobId(jobId);
  }

  public Page<IncomeMoney> findAll(
      PageFromOne page, BoundedPageSize pageSize, IncomeMoneyCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return incomeMoneyRepository.findAll(toSpecification(criteria), pageable);
  }

  public List<IncomeMoney> findAll(IncomeMoneyCriteria criteria) {
    return incomeMoneyRepository.findAll(toSpecification(criteria));
  }

  @Transactional
  public List<IncomeMoney> createOrUpdateAll(List<IncomeMoney> incomes) {
    moneyValidator.validateIncomeMonies(incomes);
    List<IncomeMoney> processedIncomeMoneys = new ArrayList<>();
    for (IncomeMoney incomeMoney : incomes) {
      IncomeMoney existingIncomeMoney =
          incomeMoneyRepository.findById(incomeMoney.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          incomeMoney,
          existingIncomeMoney,
          incomeMoney.getId(),
          modificationUtils.takePrimaryUser());
      processedIncomeMoneys.add(incomeMoney);
    }
    return incomeMoneyRepository.saveAll(processedIncomeMoneys);
  }

  @Transactional
  public void deleteById(String id) {
    incomeMoneyRepository.deleteById(id);
  }

  private Specification<IncomeMoney> toSpecification(IncomeMoneyCriteria criteria) {
    return (root, query, cb) -> {
      List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

      if (criteria.getSourceOrganization() != null && !criteria.getSourceOrganization().isBlank()) {
        predicates.add(
            cb.like(
                cb.lower(root.get("sourceOrganization")),
                "%" + criteria.getSourceOrganization().toLowerCase() + "%"));
      }
      if (criteria.getInvoiceReference() != null && !criteria.getInvoiceReference().isBlank()) {
        predicates.add(
            cb.like(
                cb.lower(root.get("invoiceReference")),
                "%" + criteria.getInvoiceReference().toLowerCase() + "%"));
      }
      if (criteria.getDescription() != null && !criteria.getDescription().isBlank()) {
        predicates.add(
            cb.like(
                cb.lower(root.get("description")),
                "%" + criteria.getDescription().toLowerCase() + "%"));
      }
      if (criteria.getAmount() != null) {
        predicates.add(cb.equal(root.get("amount"), criteria.getAmount()));
      }
      if (criteria.getJobId() != null) {
        predicates.add(cb.equal(root.get("job").get("id"), criteria.getJobId()));
      }
      if (criteria.getIncomeTypeId() != null) {
        predicates.add(cb.equal(root.get("incomeType").get("id"), criteria.getIncomeTypeId()));
      }
      if (criteria.getMoneyReceived() != null) {
        jakarta.persistence.criteria.Subquery<Integer> sumReceipts = query.subquery(Integer.class);
        jakarta.persistence.criteria.Root<IncomeReceipt> receiptRoot =
            sumReceipts.from(IncomeReceipt.class);
        sumReceipts.select(cb.coalesce(cb.sum(receiptRoot.get("amount")), 0));
        sumReceipts.where(cb.equal(receiptRoot.get("income").get("id"), root.get("id")));
        if (criteria.getMoneyReceived()) {
          predicates.add(cb.lessThanOrEqualTo(root.get("amount").as(Integer.class), sumReceipts));
        } else {
          predicates.add(cb.greaterThan(root.get("amount").as(Integer.class), sumReceipts));
        }
      }

      return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };
  }
}
