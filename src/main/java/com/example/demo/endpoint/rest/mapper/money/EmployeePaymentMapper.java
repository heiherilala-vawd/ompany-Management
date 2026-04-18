package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateEmployeePayment;
import com.example.demo.client.model.EmployeePayment;
import com.example.demo.client.model.PaymentType;
import com.example.demo.service.UserService;
import com.example.demo.service.money.ExpenseMoneyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EmployeePaymentMapper {

  private final ExpenseMoneyService expenseMoneyService;
  private final UserService userService;

  public com.example.demo.model.money.EmployeePayment toDomain(EmployeePayment restPayment) {
    if (restPayment == null) return null;

    return com.example.demo.model.money.EmployeePayment.builder()
        .id(restPayment.getId())
        .expense(
            restPayment.getExpenseId() != null
                ? expenseMoneyService.findById(restPayment.getExpenseId()).orElse(null)
                : null)
        .employee(
            restPayment.getEmployeeId() != null
                ? userService.getById(restPayment.getEmployeeId())
                : null)
        .paymentDescription(restPayment.getPaymentDescription())
        .paymentType(
            restPayment.getPaymentType() != null
                ? com.example.demo.model.money.EmployeePayment.PaymentType.valueOf(
                    restPayment.getPaymentType().name())
                : null)
        .build();
  }

  public com.example.demo.model.money.EmployeePayment toDomain(
      CrupdateEmployeePayment restPayment) {
    if (restPayment == null) return null;

    return com.example.demo.model.money.EmployeePayment.builder()
        .id(restPayment.getId())
        .expense(
            restPayment.getExpenseId() != null
                ? expenseMoneyService.findById(restPayment.getExpenseId()).orElse(null)
                : null)
        .employee(
            restPayment.getEmployeeId() != null
                ? userService.getById(restPayment.getEmployeeId())
                : null)
        .paymentDescription(restPayment.getPaymentDescription())
        .paymentType(
            restPayment.getPaymentType() != null
                ? com.example.demo.model.money.EmployeePayment.PaymentType.valueOf(
                    restPayment.getPaymentType().name())
                : null)
        .build();
  }

  public EmployeePayment toRestPayment(com.example.demo.model.money.EmployeePayment domainPayment) {
    if (domainPayment == null) return null;

    EmployeePayment restPayment = new EmployeePayment();
    restPayment.setId(domainPayment.getId());
    restPayment.setExpenseId(
        domainPayment.getExpense() != null ? domainPayment.getExpense().getId() : null);
    restPayment.setEmployeeId(
        domainPayment.getEmployee() != null ? domainPayment.getEmployee().getId() : null);
    restPayment.setPaymentDescription(domainPayment.getPaymentDescription());
    restPayment.setPaymentType(
        domainPayment.getPaymentType() != null
            ? PaymentType.valueOf(domainPayment.getPaymentType().name())
            : null);
    restPayment.setCreatedAt(domainPayment.getCreatedAt());
    restPayment.setUpdatedAt(domainPayment.getUpdatedAt());
    restPayment.setComment(domainPayment.getComment());

    if (domainPayment.getCreatedBy() != null) {
      restPayment.setCreatedBy(domainPayment.getCreatedBy().getId());
    }
    if (domainPayment.getUpdatedBy() != null) {
      restPayment.setUpdatedBy(domainPayment.getUpdatedBy().getId());
    }

    return restPayment;
  }

  public List<EmployeePayment> toRestPayments(
      List<com.example.demo.model.money.EmployeePayment> domainPayments) {
    return domainPayments.stream().map(this::toRestPayment).toList();
  }
}
