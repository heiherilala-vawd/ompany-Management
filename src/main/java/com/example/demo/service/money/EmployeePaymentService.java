package com.example.demo.service.money;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.criteria.EmployeePaymentCriteria;
import com.example.demo.model.exception.ForbiddenException;
import com.example.demo.model.money.EmployeePayment;
import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.repository.money.EmployeePaymentRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.MoneyValidator;
import jakarta.persistence.criteria.Join;
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
    Optional<EmployeePayment> payment = employeePaymentRepository.findById(id);
    payment.ifPresent(this::validateRestrictedUserAccess);
    return payment;
  }

  public Page<EmployeePayment> findAll(
      PageFromOne page, BoundedPageSize pageSize, EmployeePaymentCriteria criteria) {
    User currentUser = modificationUtils.takePrimaryUser();
    applyUserIdFilter(criteria, currentUser);
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
    employeePaymentRepository
        .findById(id)
        .ifPresent(
            payment -> {
              if (payment.getExpense() != null) {
                payment.getExpense().setEmployeePayment(null);
              }
              employeePaymentRepository.delete(payment);
            });
  }

  private Specification<EmployeePayment> toSpecification(EmployeePaymentCriteria criteria) {
    return Specification.<EmployeePayment>where(userIdsIn(criteria.getUserIDs()))
        .and(containsIgnoreCase(criteria.getPaymentDescription(), "paymentDescription"))
        .and(equal(criteria.getPaymentType(), "paymentType"));
  }

  private Specification<EmployeePayment> userIdsIn(List<String> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return (root, query, cb) -> cb.conjunction();
    }
    return (root, query, cb) -> {
      Join<EmployeePayment, User> usersJoin = root.join("users");
      return usersJoin.get("id").in(userIds);
    };
  }

  private void validateRestrictedUserAccess(EmployeePayment payment) {
    User currentUser = modificationUtils.takePrimaryUser();
    if (isRestrictedUser(currentUser)) {
      boolean isRelated =
          payment.getUsers() != null
              && payment.getUsers().stream().anyMatch(u -> u.getId().equals(currentUser.getId()));
      if (!isRelated) {
        throw new ForbiddenException("Employee payment not associated with the user");
      }
    }
  }

  private void applyUserIdFilter(EmployeePaymentCriteria criteria, User currentUser) {
    if (isRestrictedUser(currentUser)) {
      criteria.setUserIDs(List.of(currentUser.getId()));
    }
  }

  private boolean isRestrictedUser(User user) {
    return user.getRole() == User.Role.EMPLOYEE || user.getRole() == User.Role.WAREHOUSE_WORKER;
  }
}
