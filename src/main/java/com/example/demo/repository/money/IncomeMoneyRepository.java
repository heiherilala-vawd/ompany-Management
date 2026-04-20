package com.example.demo.repository.money;

import com.example.demo.model.money.IncomeMoney;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeMoneyRepository
    extends JpaRepository<IncomeMoney, String>, JpaSpecificationExecutor<IncomeMoney> {

  Page<IncomeMoney> findBySourceOrganizationContainingIgnoreCase(
      String sourceOrganization, Pageable pageable);

  Optional<IncomeMoney> findByInvoiceReference(String invoiceReference);

  boolean existsByInvoiceReference(String invoiceReference);
}
