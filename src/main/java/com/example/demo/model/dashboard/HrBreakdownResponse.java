package com.example.demo.model.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
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
public class HrBreakdownResponse {
  @JsonProperty("employees_by_department")
  private List<DepartmentCount> employeesByDepartment;

  @JsonProperty("leave_days_by_type")
  private List<LeaveTypeDays> leaveDaysByType;

  @JsonProperty("leave_by_status")
  private List<LeaveStatusCount> leaveByStatus;

  @JsonProperty("employees_by_job")
  private List<JobCount> employeesByJob;

  @JsonProperty("labor_cost_by_job")
  private List<JobCost> laborCostByJob;

  @JsonProperty("payroll_by_type")
  private List<PayrollByType> payrollByType;

  @JsonProperty("leave_balances")
  private List<LeaveBalanceItem> leaveBalances;

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DepartmentCount {
    @JsonProperty("department")
    private String department;

    @JsonProperty("count")
    private Integer count;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class LeaveTypeDays {
    @JsonProperty("leave_type")
    private String leaveType;

    @JsonProperty("days")
    private BigDecimal days;

    @JsonProperty("count")
    private Integer count;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class LeaveStatusCount {
    @JsonProperty("status")
    private String status;

    @JsonProperty("count")
    private Integer count;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JobCount {
    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("job_description")
    private String jobDescription;

    @JsonProperty("count")
    private Integer count;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JobCost {
    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("job_description")
    private String jobDescription;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PayrollByType {
    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class LeaveBalanceItem {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("accrued")
    private BigDecimal accrued;

    @JsonProperty("taken")
    private BigDecimal taken;

    @JsonProperty("remaining")
    private BigDecimal remaining;
  }
}
