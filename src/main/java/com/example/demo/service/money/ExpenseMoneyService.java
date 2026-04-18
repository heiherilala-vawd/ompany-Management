package com.example.demo.service.money;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.repository.money.ExpenseMoneyRepository;
import com.example.demo.service.utils.PageUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseMoneyService {

  private final ExpenseMoneyRepository expenseMoneyRepository;

  public Optional<ExpenseMoney> findById(String id) {
    return expenseMoneyRepository.findById(id);
  }

  public Page<ExpenseMoney> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);

    return expenseMoneyRepository.findAll(pageable);
  }

  public Optional<ExpenseMoney> findByMonetaryMovementId(String monetaryMovementId) {
    return expenseMoneyRepository.findByMonetaryMovementId(monetaryMovementId);
  }

  @Transactional
  public ExpenseMoney create(ExpenseMoney expenseMoney) {
    return expenseMoneyRepository.save(expenseMoney);
  }

  @Transactional
  public ExpenseMoney update(ExpenseMoney expenseMoney) {
    return expenseMoneyRepository.save(expenseMoney);
  }

  @Transactional
  public List<ExpenseMoney> createOrUpdateAll(List<ExpenseMoney> expenses) {
    return expenseMoneyRepository.saveAll(expenses);
  }

  @Transactional
  public void deleteById(String id) {
    expenseMoneyRepository.deleteById(id);
  }
}
