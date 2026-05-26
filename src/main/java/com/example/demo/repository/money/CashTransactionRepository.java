package com.example.demo.repository.money;

import com.example.demo.model.money.CashTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CashTransactionRepository
    extends JpaRepository<CashTransaction, String>, JpaSpecificationExecutor<CashTransaction> {

  @Query(
      "SELECT COALESCE(SUM(CASE WHEN ct.type = 'CREDIT' THEN ct.amount ELSE 0 END), 0) "
          + "FROM CashTransaction ct "
          + "WHERE (:dateFrom IS NULL OR ct.transactionDate >= :dateFrom) "
          + "AND (:dateTo IS NULL OR ct.transactionDate <= :dateTo)")
  BigDecimal sumCredits(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

  @Query(
      "SELECT COALESCE(SUM(CASE WHEN ct.type = 'DEBIT' THEN ct.amount ELSE 0 END), 0) "
          + "FROM CashTransaction ct "
          + "WHERE (:dateFrom IS NULL OR ct.transactionDate >= :dateFrom) "
          + "AND (:dateTo IS NULL OR ct.transactionDate <= :dateTo)")
  BigDecimal sumDebits(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

  @Query(
      nativeQuery = true,
      value =
          "SELECT sub.fmt, COALESCE(SUM(sub.credits), 0), COALESCE(SUM(sub.debits), 0) FROM ("
              + "  SELECT TO_CHAR(ct.transaction_date, CAST(:format AS TEXT)) AS fmt,"
              + "    CASE WHEN ct.type = 'CREDIT' THEN ct.amount ELSE 0 END AS credits,"
              + "    CASE WHEN ct.type = 'DEBIT' THEN ct.amount ELSE 0 END AS debits"
              + "  FROM cash_transaction ct"
              + "  WHERE ct.transaction_date >= COALESCE(CAST(:dateFrom AS DATE), ct.transaction_date) "
              + "  AND ct.transaction_date <= COALESCE(CAST(:dateTo AS DATE), ct.transaction_date)"
              + ") sub GROUP BY sub.fmt ORDER BY sub.fmt")
  List<Object[]> findCashFlowByPeriod(
      @Param("dateFrom") LocalDate dateFrom,
      @Param("dateTo") LocalDate dateTo,
      @Param("format") String format);
}
