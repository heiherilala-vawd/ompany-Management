package com.example.demo.service.money;

import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.criteria.TravelExpenseCriteria;
import com.example.demo.model.exception.ForbiddenException;
import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.repository.money.TravelExpenseRepository;
import com.example.demo.repository.movement.TravelPeopleRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.MoneyValidator;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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
public class TravelExpenseService {

  private final TravelExpenseRepository travelExpenseRepository;
  private final TravelPeopleRepository travelPeopleRepository;
  private final ExpenseMoneyService expenseMoneyService;
  private final ModificationUtils modificationUtils;
  private final MoneyValidator moneyValidator;

  public Optional<TravelExpense> findById(String id) {
    Optional<TravelExpense> expense = travelExpenseRepository.findById(id);
    expense.ifPresent(this::validateRestrictedUserAccess);
    return expense;
  }

  public Page<TravelExpense> findAll(
      PageFromOne page, BoundedPageSize pageSize, TravelExpenseCriteria criteria) {
    User currentUser = modificationUtils.takePrimaryUser();
    if (isRestrictedUser(currentUser)) {
      criteria.setUserId(currentUser.getId());
    }
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return travelExpenseRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<TravelExpense> createOrUpdateAll(List<TravelExpense> travelExpenses) {
    moneyValidator.validateTravelExpenses(travelExpenses);

    List<ExpenseMoney> expenses =
        travelExpenses.stream().map(TravelExpense::getExpense).collect(Collectors.toList());
    expenseMoneyService.createOrUpdateAll(expenses);

    return travelExpenseRepository.saveAll(travelExpenses);
  }

  @Transactional
  public void deleteById(String id) {
    travelExpenseRepository.deleteById(id);
  }

  private Specification<TravelExpense> toSpecification(TravelExpenseCriteria criteria) {
    Specification<TravelExpense> spec =
        Specification.<TravelExpense>where(
                equal(criteria.getDepartureLocation(), "departureLocation", "id"))
            .and(equal(criteria.getArrivalLocation(), "arrivalLocation", "id"))
            .and(equal(criteria.getArrivalDate(), "arrivalDate"));
    if (criteria.getUserId() != null) {
      spec = spec.and(userIdFilter(criteria.getUserId()));
    }
    return spec;
  }

  private Specification<TravelExpense> userIdFilter(String userId) {
    return (root, query, cb) -> {
      Subquery<String> subquery = query.subquery(String.class);
      Root<com.example.demo.model.movement.TravelPeople> tpRoot =
          subquery.from(com.example.demo.model.movement.TravelPeople.class);
      subquery.select(tpRoot.get("travel").get("id"));
      subquery.where(cb.equal(tpRoot.get("user").get("id"), userId));
      return cb.in(root.get("id")).value(subquery);
    };
  }

  private void validateRestrictedUserAccess(TravelExpense expense) {
    User currentUser = modificationUtils.takePrimaryUser();
    if (isRestrictedUser(currentUser)) {
      boolean isRelated =
          travelPeopleRepository.findByTravelId(expense.getId()).stream()
              .anyMatch(
                  tp -> tp.getUser() != null && tp.getUser().getId().equals(currentUser.getId()));
      if (!isRelated) {
        throw new ForbiddenException("Travel expense not associated with the user");
      }
    }
  }

  private boolean isRestrictedUser(User user) {
    return user.getRole() == User.Role.EMPLOYEE || user.getRole() == User.Role.WAREHOUSE_WORKER;
  }
}
