package com.example.demo.service.money;

import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.TravelExpenseCriteria;
import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.repository.money.TravelExpenseRepository;
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
public class TravelExpenseService {

  private final TravelExpenseRepository travelExpenseRepository;
  private final ExpenseMoneyService expenseMoneyService;
  private final ModificationUtils modificationUtils;
  private final MoneyValidator moneyValidator;

  public Optional<TravelExpense> findById(String id) {
    return travelExpenseRepository.findById(id);
  }

  public Page<TravelExpense> findAll(
      PageFromOne page, BoundedPageSize pageSize, TravelExpenseCriteria criteria) {
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
    return Specification.<TravelExpense>where(
            equal(criteria.getDepartureLocation(), "departureLocation", "id"))
        .and(equal(criteria.getArrivalLocation(), "arrivalLocation", "id"))
        .and(equal(criteria.getArrivalDate(), "arrivalDate"));
  }
}
