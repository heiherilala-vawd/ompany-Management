package com.example.demo.repository.money;

import com.example.demo.model.money.IncomeMoney;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeMoneyRepository
    extends JpaRepository<IncomeMoney, String>, JpaSpecificationExecutor<IncomeMoney> {

  Page<IncomeMoney> findBySourceOrganizationContainingIgnoreCase(
      String sourceOrganization, Pageable pageable);

  Optional<IncomeMoney> findByInvoiceReference(String invoiceReference);

  boolean existsByInvoiceReference(String invoiceReference);

  @Query("SELECT COALESCE(SUM(i.amount), 0) FROM IncomeMoney i WHERE i.job.id = :jobId")
  BigDecimal sumByJobId(@Param("jobId") String jobId);

  @Query(
      "SELECT COALESCE(SUM(i.amount), 0) FROM IncomeMoney i "
          + "WHERE i.job.id = COALESCE(:jobId, i.job.id) "
          + "AND i.createdAt >= COALESCE(:dateFrom, i.createdAt) "
          + "AND i.createdAt <= COALESCE(:dateTo, i.createdAt)")
  BigDecimal sumIncomes(
      @Param("jobId") String jobId,
      @Param("dateFrom") Instant dateFrom,
      @Param("dateTo") Instant dateTo);

  @Query(
      nativeQuery = true,
      value =
          "SELECT sub.fmt, COALESCE(SUM(sub.amount), 0) FROM ("
              + "SELECT TO_CHAR(i.created_at, CAST(:format AS TEXT)) AS fmt, i.amount "
              + "FROM income_money i "
              + "WHERE i.job_id = COALESCE(CAST(:jobId AS TEXT), i.job_id) "
              + "AND i.created_at >= COALESCE(CAST(:dateFrom AS TIMESTAMPTZ), i.created_at) "
              + "AND i.created_at <= COALESCE(CAST(:dateTo AS TIMESTAMPTZ), i.created_at)"
              + ") sub "
              + "GROUP BY sub.fmt ORDER BY sub.fmt")
  List<Object[]> findIncomesByPeriod(
      @Param("jobId") String jobId,
      @Param("dateFrom") Instant dateFrom,
      @Param("dateTo") Instant dateTo,
      @Param("format") String format);

  @Query(
      nativeQuery = true,
      value =
          "SELECT sub.fmt, COALESCE(SUM(sub.net), 0) FROM ("
              + "SELECT TO_CHAR(i.facturation_date, CAST(:format AS TEXT)) AS fmt, "
              + "  i.amount - COALESCE("
              + "    (SELECT SUM(r.amount) FROM income_receipt r WHERE r.income_id = i.id), 0"
              + "  ) AS net "
              + "FROM income_money i "
              + "WHERE i.job_id = COALESCE(CAST(:jobId AS TEXT), i.job_id) "
              + "AND i.facturation_date >= COALESCE(CAST(:dateFrom AS TIMESTAMPTZ), i.facturation_date) "
              + "AND i.facturation_date <= COALESCE(CAST(:dateTo AS TIMESTAMPTZ), i.facturation_date)"
              + ") sub GROUP BY sub.fmt ORDER BY sub.fmt")
  List<Object[]> findReceivablesByPeriod(
      @Param("jobId") String jobId,
      @Param("dateFrom") Instant dateFrom,
      @Param("dateTo") Instant dateTo,
      @Param("format") String format);
}
