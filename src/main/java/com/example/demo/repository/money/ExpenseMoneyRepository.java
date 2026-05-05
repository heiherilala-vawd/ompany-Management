package com.example.demo.repository.money;

import com.example.demo.model.money.ExpenseMoney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseMoneyRepository
    extends JpaRepository<ExpenseMoney, String>, JpaSpecificationExecutor<ExpenseMoney> {

  @Query("SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseMoney e WHERE e.job.id = :jobId")
  Integer sumByJobId(@Param("jobId") String jobId);
}
