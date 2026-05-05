package com.example.demo.model.criteria;

import com.example.demo.model.money.EmployeePayment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeePaymentCriteria {
  private String employeeId;
  private String paymentDescription;
  private EmployeePayment.PaymentType paymentType;
}
