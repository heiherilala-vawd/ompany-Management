package com.example.demo.model.criteria;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanCriteria {
  private String description;
  private BigDecimal amount;
  private String lender;
  private String jobId;
}
