package com.example.demo.model.criteria;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomeMoneyCriteria {
  private String organizationId;
  private String invoiceReference;
  private String description;
  private BigDecimal amount;
  private String jobId;
  private String incomeTypeId;
  private Boolean moneyReceived;
}
