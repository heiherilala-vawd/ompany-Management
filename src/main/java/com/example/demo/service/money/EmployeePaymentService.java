package com.example.demo.service.money;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.EmployeePaymentCriteria;
import com.example.demo.model.money.EmployeePayment;
import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.repository.money.EmployeePaymentRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.MoneyValidator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeePaymentService {

  private final EmployeePaymentRepository employeePaymentRepository;
  private final ExpenseMoneyService expenseMoneyService;
  private final ModificationUtils modificationUtils;
  private final MoneyValidator moneyValidator;

  public Optional<EmployeePayment> findById(String id) {
    return employeePaymentRepository.findById(id);
  }

  public Page<EmployeePayment> findAll(
      PageFromOne page, BoundedPageSize pageSize, EmployeePaymentCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return employeePaymentRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<EmployeePayment> createOrUpdateAll(List<EmployeePayment> payments) {
    moneyValidator.validateEmployeePayments(payments);

    List<ExpenseMoney> expenses =
        payments.stream().map(EmployeePayment::getExpense).collect(Collectors.toList());
    expenseMoneyService.createOrUpdateAll(expenses);

    return employeePaymentRepository.saveAll(payments);
  }

  @Transactional
  public void deleteById(String id) {
    employeePaymentRepository.deleteById(id);
  }

  private Specification<EmployeePayment> toSpecification(EmployeePaymentCriteria criteria) {
    return Specification.<EmployeePayment>where(equal(criteria.getEmployeeId(), "employee", "id"))
        .and(equal(criteria.getExpenseId(), "expense", "id"))
        .and(containsIgnoreCase(criteria.getPaymentDescription(), "paymentDescription"))
        .and(equal(criteria.getPaymentType(), "paymentType"));
  }
}
