package com.example.demo.repository.money;

import com.example.demo.model.money.EmployeePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeePaymentRepository
    extends JpaRepository<EmployeePayment, String>, JpaSpecificationExecutor<EmployeePayment> {
  Page<EmployeePayment> findByExpenseId(String expenseId, Pageable pageable);

  Page<EmployeePayment> findByPaymentType(
      EmployeePayment.PaymentType paymentType, Pageable pageable);
}
