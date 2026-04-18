package com.example.demo.repository.money;

import com.example.demo.model.money.ExpenseMoney;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseMoneyRepository extends JpaRepository<ExpenseMoney, String> {
  Optional<ExpenseMoney> findByMonetaryMovementId(String monetaryMovementId);
}
