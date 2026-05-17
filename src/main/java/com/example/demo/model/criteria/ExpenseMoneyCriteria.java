package com.example.demo.model.criteria;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseMoneyCriteria {
  private String description;
  private BigDecimal amount;
  private String jobId;
}
