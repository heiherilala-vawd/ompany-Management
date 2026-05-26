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
public class HrDashboardResponse {
  @JsonProperty("total_employees")
  private Integer totalEmployees;

  @JsonProperty("employees_by_department")
  private List<DepartmentCount> employeesByDepartment;

  @JsonProperty("leave_summary")
  private LeaveSummary leaveSummary;

  @JsonProperty("payroll_total")
  private BigDecimal payrollTotal;

  @JsonProperty("payroll_by_type")
  private List<PayrollByType> payrollByType;

  @JsonProperty("new_hires")
  private Integer newHires;

  @JsonProperty("leave_balances")
  private List<LeaveBalanceItem> leaveBalances;

  @JsonProperty("tasks_by_priority")
  private List<TaskPriorityCount> tasksByPriority;

  @JsonProperty("tasks_completed")
  private Integer tasksCompleted;

  @JsonProperty("employees_by_job")
  private List<JobCount> employeesByJob;

  @JsonProperty("labor_cost_by_job")
  private List<JobCost> laborCostByJob;

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
  public static class LeaveSummary {
    @JsonProperty("total_days_approved")
    private BigDecimal totalDaysApproved;

    @JsonProperty("by_leave_type")
    private List<LeaveTypeDays> byLeaveType;

    @JsonProperty("by_status")
    private List<LeaveStatusCount> byStatus;

    @JsonProperty("absence_rate")
    private BigDecimal absenceRate;
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

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class TaskPriorityCount {
    @JsonProperty("priority")
    private String priority;

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
}
