package com.example.demo.repository.money;

import com.example.demo.model.money.EmployeePayment;
import com.example.demo.model.money.EmployeePayment.PaymentType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeePaymentRepository extends JpaRepository<EmployeePayment, String> {
  Page<EmployeePayment> findByEmployeeId(String employeeId, Pageable pageable);

  Page<EmployeePayment> findByExpenseId(String expenseId, Pageable pageable);

  List<EmployeePayment> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);

  Page<EmployeePayment> findByPaymentType(PaymentType paymentType, Pageable pageable);

  List<EmployeePayment> findByEmployeeIdAndPaymentType(String employeeId, PaymentType paymentType);
}
