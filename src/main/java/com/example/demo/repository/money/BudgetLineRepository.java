package com.example.demo.repository.money;

import com.example.demo.model.money.BudgetLine;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetLineRepository
    extends JpaRepository<BudgetLine, String>, JpaSpecificationExecutor<BudgetLine> {

  @Query(
      "SELECT bl.category, COALESCE(SUM(bl.plannedAmount), 0), COALESCE(SUM(bl.actualAmount), 0) "
          + "FROM BudgetLine bl WHERE bl.company.id = :companyId "
          + "GROUP BY bl.category")
  List<Object[]> findBudgetVsActualByCompany(@Param("companyId") String companyId);

  @Query(
      nativeQuery = true,
      value =
          "SELECT sub.fmt, COALESCE(SUM(sub.planned), 0), COALESCE(SUM(sub.actual), 0) FROM ("
              + "SELECT TO_CHAR(bl.period_start, CAST(:format AS TEXT)) AS fmt, "
              + "  bl.planned_amount AS planned, bl.actual_amount AS actual "
              + "FROM budget_line bl "
              + "WHERE bl.company_id = CAST(:companyId AS TEXT) "
              + "AND bl.period_start >= COALESCE(CAST(:dateFrom AS DATE), bl.period_start) "
              + "AND bl.period_end <= COALESCE(CAST(:dateTo AS DATE), bl.period_end)"
              + ") sub GROUP BY sub.fmt ORDER BY sub.fmt")
  List<Object[]> findBudgetByPeriod(
      @Param("companyId") String companyId,
      @Param("dateFrom") LocalDate dateFrom,
      @Param("dateTo") LocalDate dateTo,
      @Param("format") String format);
}
