package com.example.demo.repository.money;

import com.example.demo.model.money.TravelExpense;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelExpenseRepository
    extends JpaRepository<TravelExpense, String>, JpaSpecificationExecutor<TravelExpense> {
  Optional<TravelExpense> findByExpenseId(String expenseId);

  Page<TravelExpense> findByDepartureLocationContainingIgnoreCase(
      String location, Pageable pageable);

  List<TravelExpense> findByDepartureDateBetween(Instant startDate, Instant endDate);

  List<TravelExpense> findByArrivalDateBetween(Instant startDate, Instant endDate);

  List<TravelExpense> findByDepartureLocationAndArrivalLocation(String departure, String arrival);
}
