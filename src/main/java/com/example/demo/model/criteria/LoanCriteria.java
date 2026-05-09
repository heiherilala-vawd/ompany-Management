package com.example.demo.model.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanCriteria {
  private String description;
  private Integer amount;
  private String lender;
  private String status;
  private String jobId;
}
