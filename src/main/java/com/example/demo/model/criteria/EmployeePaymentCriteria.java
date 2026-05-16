package com.example.demo.model.criteria;

import com.example.demo.model.money.EmployeePayment;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeePaymentCriteria {
  private List<String> userIDs;
  private String paymentDescription;
  private EmployeePayment.PaymentType paymentType;
}
