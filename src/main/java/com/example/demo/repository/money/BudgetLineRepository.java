package com.example.demo.repository.money;

import com.example.demo.model.money.BudgetLine;
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
}
