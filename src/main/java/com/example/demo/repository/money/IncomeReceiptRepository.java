package com.example.demo.repository.money;

import com.example.demo.model.money.IncomeReceipt;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeReceiptRepository
    extends JpaRepository<IncomeReceipt, String>, JpaSpecificationExecutor<IncomeReceipt> {

  List<IncomeReceipt> findByIncomeIdOrderByPaymentDateAsc(String incomeId);

  @Query("SELECT COALESCE(SUM(r.amount), 0) FROM IncomeReceipt r")
  BigDecimal sumAllReceipts();
}
