package com.example.demo.model.dashboard;

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
public class HrSummaryResponse {
  @JsonProperty("total_employees")
  private Integer totalEmployees;

  @JsonProperty("new_hires")
  private Integer newHires;

  @JsonProperty("payroll_total")
  private BigDecimal payrollTotal;

  @JsonProperty("tasks_completed")
  private Integer tasksCompleted;

  @JsonProperty("leave_days_approved")
  private BigDecimal leaveDaysApproved;

  @JsonProperty("absence_rate")
  private BigDecimal absenceRate;
}
