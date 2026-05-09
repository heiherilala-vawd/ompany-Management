package com.example.demo.service.money;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.IncomeReceiptCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.money.IncomeMoney;
import com.example.demo.model.money.IncomeReceipt;
import com.example.demo.repository.money.IncomeReceiptRepository;
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
public class IncomeReceiptService {

  private final IncomeReceiptRepository incomeReceiptRepository;
  private final IncomeMoneyService incomeMoneyService;
  private final ModificationUtils modificationUtils;
  private final MoneyValidator moneyValidator;

  public Optional<IncomeReceipt> findById(String id) {
    return incomeReceiptRepository.findById(id);
  }

  public Page<IncomeReceipt> findAll(
      PageFromOne page, BoundedPageSize pageSize, IncomeReceiptCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return incomeReceiptRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<IncomeReceipt> createOrUpdateAll(List<IncomeReceipt> receipts) {
    moneyValidator.validateIncomeReceipts(receipts);
    List<IncomeReceipt> processedReceipts = new ArrayList<>();
    for (IncomeReceipt receipt : receipts) {
      IncomeMoney income = receipt.getIncome();
      IncomeMoney existingIncome = incomeMoneyService.findById(income.getId()).orElse(null);
      if (existingIncome == null) {
        throw new NotFoundException("Income with id " + income.getId() + " not found");
      }
      receipt.setIncome(existingIncome);

      IncomeReceipt existingReceipt =
          incomeReceiptRepository.findById(receipt.getId()).orElse(null);

      modificationUtils.createOrUpdateModel(
          receipt, existingReceipt, receipt.getId(), modificationUtils.takePrimaryUser());
      processedReceipts.add(receipt);
    }
    return incomeReceiptRepository.saveAll(processedReceipts);
  }

  @Transactional
  public void deleteById(String id) {
    incomeReceiptRepository.deleteById(id);
  }

  public List<IncomeReceipt> findByIncomeId(String incomeId) {
    return incomeReceiptRepository.findByIncomeIdOrderByPaymentDateAsc(incomeId);
  }

  private Specification<IncomeReceipt> toSpecification(IncomeReceiptCriteria criteria) {
    return (root, query, cb) -> {
      List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

      if (criteria.getIncomeId() != null) {
        predicates.add(cb.equal(root.get("income").get("id"), criteria.getIncomeId()));
      }

      return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };
  }
}
