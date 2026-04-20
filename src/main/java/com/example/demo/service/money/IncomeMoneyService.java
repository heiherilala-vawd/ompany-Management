package com.example.demo.service.money;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.IncomeMoneyCriteria;
import com.example.demo.model.money.IncomeMoney;
import com.example.demo.repository.money.IncomeMoneyRepository;
import com.example.demo.service.utils.PageUtils;
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

  public Optional<IncomeMoney> findById(String id) {
    return incomeMoneyRepository.findById(id);
  }

  public Page<IncomeMoney> findAll(
      PageFromOne page, BoundedPageSize pageSize, IncomeMoneyCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return incomeMoneyRepository.findAll(toSpecification(criteria), pageable);
  }

  public Optional<IncomeMoney> findByInvoiceReference(String invoiceReference) {
    return incomeMoneyRepository.findByInvoiceReference(invoiceReference);
  }

  public Page<IncomeMoney> findBySourceOrganization(
      PageFromOne page, BoundedPageSize pageSize, String sourceOrganization) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);

    return incomeMoneyRepository.findBySourceOrganizationContainingIgnoreCase(
        sourceOrganization, pageable);
  }

  @Transactional
  public IncomeMoney create(IncomeMoney incomeMoney) {
    return incomeMoneyRepository.save(incomeMoney);
  }

  @Transactional
  public IncomeMoney update(IncomeMoney incomeMoney) {
    return incomeMoneyRepository.save(incomeMoney);
  }

  @Transactional
  public List<IncomeMoney> createOrUpdateAll(List<IncomeMoney> incomes) {
    return incomeMoneyRepository.saveAll(incomes);
  }

  @Transactional
  public void deleteById(String id) {
    incomeMoneyRepository.deleteById(id);
  }

  private Specification<IncomeMoney> toSpecification(IncomeMoneyCriteria criteria) {
    return Specification.<IncomeMoney>where(
            containsIgnoreCase(criteria.getSourceOrganization(), "sourceOrganization"))
        .and(containsIgnoreCase(criteria.getInvoiceReference(), "invoiceReference"))
        .and(containsIgnoreCase(criteria.getDescription(), "description"))
        .and(equal(criteria.getAmount(), "amount"));
  }
}
