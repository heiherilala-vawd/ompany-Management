package com.example.demo.model.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomeMoneyCriteria {
  private String sourceOrganization;
  private String invoiceReference;
  private String description;
  private Integer amount;
  private String jobId;
  private String incomeTypeId;
  private Boolean moneyReceived;
}
