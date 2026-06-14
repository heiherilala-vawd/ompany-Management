package com.example.demo.repository.money;

import com.example.demo.model.money.CompanyFixedCost;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyFixedCostRepository extends JpaRepository<CompanyFixedCost, String> {

  List<CompanyFixedCost> findByCompanyIdOrderByName(String companyId);

  Page<CompanyFixedCost> findByCompanyIdOrderByName(String companyId, Pageable pageable);

  @Query(
      "SELECT c FROM CompanyFixedCost c WHERE c.company.id = :companyId "
          + "AND c.startDate <= :date AND (c.endDate IS NULL OR c.endDate >= :date)")
  List<CompanyFixedCost> findActiveByCompanyIdAtDate(
      @Param("companyId") String companyId, @Param("date") LocalDate date);

  @Query("SELECT COALESCE(SUM(c.amount), 0) FROM CompanyFixedCost c")
  BigDecimal sumActiveCosts();
}
