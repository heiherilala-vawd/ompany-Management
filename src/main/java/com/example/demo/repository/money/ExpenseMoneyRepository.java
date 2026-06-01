package com.example.demo.repository.money;

import com.example.demo.model.money.ExpenseMoney;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseMoneyRepository
    extends JpaRepository<ExpenseMoney, String>, JpaSpecificationExecutor<ExpenseMoney> {

  @Query("SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseMoney e WHERE e.job.id = :jobId")
  BigDecimal sumByJobId(@Param("jobId") String jobId);

  @Query(
      "SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseMoney e "
          + "WHERE e.job.id = COALESCE(:jobId, e.job.id) "
          + "AND e.createdAt >= COALESCE(:dateFrom, e.createdAt) "
          + "AND e.createdAt <= COALESCE(:dateTo, e.createdAt)")
  BigDecimal sumExpenses(
      @Param("jobId") String jobId,
      @Param("dateFrom") Instant dateFrom,
      @Param("dateTo") Instant dateTo);

  @Query(
      nativeQuery = true,
      value =
          "SELECT sub.fmt, COALESCE(SUM(sub.amount), 0) FROM ("
              + "  SELECT TO_CHAR(e.created_at, CAST(:format AS TEXT)) AS fmt, e.amount "
              + "  FROM expense_money e "
              + "  WHERE e.job_id = COALESCE(CAST(:jobId AS TEXT), e.job_id) "
              + "  AND e.created_at >= COALESCE(CAST(:dateFrom AS TIMESTAMP), e.created_at) "
              + "  AND e.created_at <= COALESCE(CAST(:dateTo AS TIMESTAMP), e.created_at)"
              + ") sub GROUP BY sub.fmt ORDER BY sub.fmt")
  List<Object[]> findExpensesByPeriod(
      @Param("jobId") String jobId,
      @Param("dateFrom") Instant dateFrom,
      @Param("dateTo") Instant dateTo,
      @Param("format") String format);

  @Query(
      "SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseMoney e WHERE e.bankFee IS NOT NULL "
          + "AND e.job.id = COALESCE(:jobId, e.job.id) "
          + "AND e.createdAt >= COALESCE(:dateFrom, e.createdAt) "
          + "AND e.createdAt <= COALESCE(:dateTo, e.createdAt)")
  BigDecimal sumBankExpenses(
      @Param("jobId") String jobId,
      @Param("dateFrom") Instant dateFrom,
      @Param("dateTo") Instant dateTo);

  @Query(
      "SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseMoney e WHERE e.purchase IS NOT NULL "
          + "AND e.job.id = COALESCE(:jobId, e.job.id) "
          + "AND e.createdAt >= COALESCE(:dateFrom, e.createdAt) "
          + "AND e.createdAt <= COALESCE(:dateTo, e.createdAt)")
  BigDecimal sumPurchaseExpenses(
      @Param("jobId") String jobId,
      @Param("dateFrom") Instant dateFrom,
      @Param("dateTo") Instant dateTo);

  @Query(
      "SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseMoney e WHERE e.travelExpense IS NOT NULL "
          + "AND e.job.id = COALESCE(:jobId, e.job.id) "
          + "AND e.createdAt >= COALESCE(:dateFrom, e.createdAt) "
          + "AND e.createdAt <= COALESCE(:dateTo, e.createdAt)")
  BigDecimal sumTravelExpenses(
      @Param("jobId") String jobId,
      @Param("dateFrom") Instant dateFrom,
      @Param("dateTo") Instant dateTo);

  @Query(
      "SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseMoney e WHERE e.employeePayment IS NOT NULL "
          + "AND e.job.id = COALESCE(:jobId, e.job.id) "
          + "AND e.createdAt >= COALESCE(:dateFrom, e.createdAt) "
          + "AND e.createdAt <= COALESCE(:dateTo, e.createdAt)")
  BigDecimal sumLaborExpenses(
      @Param("jobId") String jobId,
      @Param("dateFrom") Instant dateFrom,
      @Param("dateTo") Instant dateTo);

  @Query(
      nativeQuery = true,
      value =
          "SELECT sub.fmt, sub.type, COALESCE(SUM(sub.amount), 0) FROM ("
              + "SELECT TO_CHAR(e.created_at, CAST(:format AS TEXT)) AS fmt, "
              + "  CASE "
              + "    WHEN ep.id IS NOT NULL THEN 'LABOR' "
              + "    WHEN te.id IS NOT NULL THEN 'TRANSPORT' "
              + "    WHEN p.id IS NOT NULL THEN 'PURCHASE' "
              + "    WHEN bf.id IS NOT NULL THEN 'BANK' "
              + "    WHEN oe.id IS NOT NULL THEN 'OTHER' "
              + "    WHEN m.id IS NOT NULL THEN 'MAINTENANCE' "
              + "  END AS type, "
              + "  e.amount "
              + "FROM expense_money e "
              + "LEFT JOIN employee_payment ep ON ep.expense_id = e.id "
              + "LEFT JOIN travel_expense te ON te.expense_id = e.id "
              + "LEFT JOIN purchase p ON p.expense_id = e.id "
              + "LEFT JOIN bank_fee bf ON bf.expense_id = e.id "
              + "LEFT JOIN other_expense oe ON oe.expense_id = e.id "
              + "LEFT JOIN maintenance m ON m.expense_id = e.id "
              + "WHERE e.job_id = COALESCE(CAST(:jobId AS TEXT), e.job_id) "
              + "AND e.created_at >= COALESCE(CAST(:dateFrom AS TIMESTAMP), e.created_at) "
              + "AND e.created_at <= COALESCE(CAST(:dateTo AS TIMESTAMP), e.created_at)"
              + ") sub GROUP BY sub.fmt, sub.type ORDER BY sub.fmt, sub.type")
  List<Object[]> findExpenseBreakdownByPeriod(
      @Param("jobId") String jobId,
      @Param("dateFrom") Instant dateFrom,
      @Param("dateTo") Instant dateTo,
      @Param("format") String format);
}
