package com.example.demo.repository.money;

import com.example.demo.model.money.OtherExpense;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtherExpenseRepository extends JpaRepository<OtherExpense, String> {
  Optional<OtherExpense> findByExpenseId(String expenseId);

  Page<OtherExpense> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
}
