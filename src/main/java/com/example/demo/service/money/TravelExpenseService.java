package com.example.demo.service.money;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.repository.money.TravelExpenseRepository;
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
public class TravelExpenseService {

  private final TravelExpenseRepository travelExpenseRepository;

  public Optional<TravelExpense> findById(String id) {
    return travelExpenseRepository.findById(id);
  }

  public Page<TravelExpense> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);

    return travelExpenseRepository.findAll(pageable);
  }

  public Optional<TravelExpense> findByExpenseId(String expenseId) {
    return travelExpenseRepository.findByExpenseId(expenseId);
  }

  public Page<TravelExpense> findByDepartureLocation(
      PageFromOne page, BoundedPageSize pageSize, String location) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return travelExpenseRepository.findByDepartureLocationContainingIgnoreCase(location, pageable);
  }

  @Transactional
  public TravelExpense create(TravelExpense travelExpense) {
    return travelExpenseRepository.save(travelExpense);
  }

  @Transactional
  public TravelExpense update(TravelExpense travelExpense) {
    return travelExpenseRepository.save(travelExpense);
  }

  @Transactional
  public List<TravelExpense> createOrUpdateAll(List<TravelExpense> travelExpenses) {
    return travelExpenseRepository.saveAll(travelExpenses);
  }

  @Transactional
  public void deleteById(String id) {
    travelExpenseRepository.deleteById(id);
  }
}
