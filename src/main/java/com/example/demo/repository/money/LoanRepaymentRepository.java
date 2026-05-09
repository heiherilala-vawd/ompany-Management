package com.example.demo.repository.money;

import com.example.demo.model.money.LoanRepayment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepaymentRepository
    extends JpaRepository<LoanRepayment, String>, JpaSpecificationExecutor<LoanRepayment> {

  List<LoanRepayment> findByLoanIdOrderByPaymentDateAsc(String loanId);
}
