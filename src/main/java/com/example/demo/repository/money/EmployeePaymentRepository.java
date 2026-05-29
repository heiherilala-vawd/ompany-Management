package com.example.demo.repository.money;

import com.example.demo.model.money.EmployeePayment;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeePaymentRepository
    extends JpaRepository<EmployeePayment, String>, JpaSpecificationExecutor<EmployeePayment> {
  Page<EmployeePayment> findByExpenseId(String expenseId, Pageable pageable);

  Page<EmployeePayment> findByPaymentType(
      EmployeePayment.PaymentType paymentType, Pageable pageable);

  @Query(
      "SELECT ep.paymentType, COALESCE(SUM(e.amount), 0) FROM EmployeePayment ep "
          + "JOIN ep.expense e WHERE e.job.id = COALESCE(:jobId, e.job.id) "
          + "AND e.createdAt >= COALESCE(:dateFrom, e.createdAt) "
          + "AND e.createdAt <= COALESCE(:dateTo, e.createdAt) "
          + "GROUP BY ep.paymentType")
  List<Object[]> sumByPaymentType(
      @Param("jobId") String jobId,
      @Param("dateFrom") Instant dateFrom,
      @Param("dateTo") Instant dateTo);
}
