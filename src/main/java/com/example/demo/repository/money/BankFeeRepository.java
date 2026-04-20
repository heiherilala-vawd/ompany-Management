package com.example.demo.repository.money;

import com.example.demo.model.money.BankFee;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BankFeeRepository
    extends JpaRepository<BankFee, String>, JpaSpecificationExecutor<BankFee> {
  Optional<BankFee> findByExpenseId(String expenseId);

  Page<BankFee> findByBankNameContainingIgnoreCase(String bankName, Pageable pageable);
}
