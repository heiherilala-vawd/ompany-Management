package com.example.demo.model.report;

import com.example.demo.model.Job;
import com.fasterxml.jackson.annotation.JsonProperty;
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
  private Integer totalIncome;

  @JsonProperty("total_expense")
  private Integer totalExpense;

  @JsonProperty("net_profit")
  private Integer netProfit;

  public static JobWithFinancials fromJobAndAmounts(
      String jobId, Integer totalIncome, Integer totalExpense) {
    return JobWithFinancials.builder()
        .jobId(jobId)
        .totalIncome(totalIncome != null ? totalIncome : 0)
        .totalExpense(totalExpense != null ? totalExpense : 0)
        .netProfit(
            (totalIncome != null ? totalIncome : 0) - (totalExpense != null ? totalExpense : 0))
        .build();
  }
}
