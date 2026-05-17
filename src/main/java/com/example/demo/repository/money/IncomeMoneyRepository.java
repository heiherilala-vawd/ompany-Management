package com.example.demo.repository.money;

import com.example.demo.model.money.IncomeMoney;
import java.math.BigDecimal;
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
}
