package com.example.demo.model.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
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
public class YearlyReport {
  @JsonProperty("year")
  private Integer year;

  @JsonProperty("jobs_with_financials")
  private List<JobWithFinancials> jobsWithFinancials;

  @JsonProperty("summary")
  private Summary summary;

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Summary {
    @JsonProperty("total_income")
    private Integer totalIncome;

    @JsonProperty("total_expense")
    private Integer totalExpense;

    @JsonProperty("net_profit")
    private Integer netProfit;

    @JsonProperty("job_count")
    private Integer jobCount;

    @JsonProperty("in_progress_job_count")
    private Integer inProgressJobCount;
  }
}
