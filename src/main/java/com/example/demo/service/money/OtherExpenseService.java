package com.example.demo.service.money;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.OtherExpenseCriteria;
import com.example.demo.model.money.OtherExpense;
import com.example.demo.repository.money.OtherExpenseRepository;
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
public class OtherExpenseService {

  private final OtherExpenseRepository otherExpenseRepository;

  public Optional<OtherExpense> findById(String id) {
    return otherExpenseRepository.findById(id);
  }

  public Page<OtherExpense> findAll(
      PageFromOne page, BoundedPageSize pageSize, OtherExpenseCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return otherExpenseRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<OtherExpense> createOrUpdateAll(List<OtherExpense> otherExpenses) {
    return otherExpenseRepository.saveAll(otherExpenses);
  }

  @Transactional
  public void deleteById(String id) {
    otherExpenseRepository.deleteById(id);
  }

  private Specification<OtherExpense> toSpecification(OtherExpenseCriteria criteria) {
    return Specification.<OtherExpense>where(equal(criteria.getExpenseId(), "expense", "id"))
        .and(containsIgnoreCase(criteria.getDescription(), "description"));
  }
}
