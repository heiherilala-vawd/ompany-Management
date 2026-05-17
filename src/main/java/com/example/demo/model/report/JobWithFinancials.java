package com.example.demo.model.report;

import com.example.demo.model.Job;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobWithFinancials {
  @JsonProperty("job_id")
  private String jobId;

  @JsonProperty("job")
  private Job job;

  @JsonProperty("total_income")
  private BigDecimal totalIncome;

  @JsonProperty("total_expense")
  private BigDecimal totalExpense;

  @JsonProperty("net_profit")
  private BigDecimal netProfit;

  public static JobWithFinancials fromJobAndAmounts(
      String jobId, BigDecimal totalIncome, BigDecimal totalExpense) {
    BigDecimal safeIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
    BigDecimal safeExpense = totalExpense != null ? totalExpense : BigDecimal.ZERO;
    return JobWithFinancials.builder()
        .jobId(jobId)
        .totalIncome(safeIncome)
        .totalExpense(safeExpense)
        .netProfit(safeIncome.subtract(safeExpense))
        .build();
  }
}
