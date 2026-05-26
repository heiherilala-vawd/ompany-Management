package com.example.demo.service.dashboard;

import com.example.demo.model.User;
import com.example.demo.model.dashboard.HrDashboardResponse;
import com.example.demo.model.dashboard.HrDashboardResponse.DepartmentCount;
import com.example.demo.model.dashboard.HrDashboardResponse.JobCost;
import com.example.demo.model.dashboard.HrDashboardResponse.JobCount;
import com.example.demo.model.dashboard.HrDashboardResponse.LeaveBalanceItem;
import com.example.demo.model.dashboard.HrDashboardResponse.LeaveStatusCount;
import com.example.demo.model.dashboard.HrDashboardResponse.LeaveSummary;
import com.example.demo.model.dashboard.HrDashboardResponse.LeaveTypeDays;
import com.example.demo.model.dashboard.HrDashboardResponse.PayrollByType;
import com.example.demo.model.dashboard.HrDashboardResponse.TaskPriorityCount;
import com.example.demo.model.hr.Leave;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.hr.LeaveRepository;
import com.example.demo.repository.money.ExpenseMoneyRepository;
import com.example.demo.repository.task.TaskRepository;
import com.example.demo.service.hr.LeaveService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardHrService {

  private final UserRepository userRepository;
  private final LeaveRepository leaveRepository;
  private final LeaveService leaveService;
  private final ExpenseMoneyRepository expenseMoneyRepository;
  private final TaskRepository taskRepository;

  public HrDashboardResponse getDashboard(
      String companyId, String jobId, LocalDate dateFrom, LocalDate dateTo) {

    List<User> employees = userRepository.findByCompanyId(companyId);
    int totalEmployees = employees.size();

    Map<String, Long> deptCounts =
        employees.stream()
            .filter(u -> u.getDepartment() != null)
            .collect(
                Collectors.groupingBy(u -> u.getDepartment().getName(), Collectors.counting()));
    List<DepartmentCount> employeesByDepartment =
        deptCounts.entrySet().stream()
            .map(
                e ->
                    DepartmentCount.builder()
                        .department(e.getKey())
                        .count(e.getValue().intValue())
                        .build())
            .collect(Collectors.toList());

    LeaveSummary leaveSummary = buildLeaveSummary(companyId, dateFrom, dateTo, totalEmployees);

    BigDecimal payrollTotal = BigDecimal.ZERO;
    if (jobId != null) {
      payrollTotal = expenseMoneyRepository.sumByJobId(jobId);
    }

    List<PayrollByType> payrollByType = List.of();

    long newHires =
        employees.stream()
            .filter(u -> u.getCreatedAt() != null && dateFrom != null && dateTo != null)
            .filter(
                u -> {
                  LocalDate created =
                      u.getCreatedAt().atZone(java.time.ZoneOffset.UTC).toLocalDate();
                  return !created.isBefore(dateFrom) && !created.isAfter(dateTo);
                })
            .count();

    List<LeaveBalanceItem> leaveBalances = buildLeaveBalances(companyId);

    List<TaskPriorityCount> tasksByPriority = buildTasksByPriority(companyId);

    long tasksCompleted = 0;
    if (dateFrom != null && dateTo != null) {
      tasksCompleted =
          taskRepository
              .findByCompanyIdAndCompletedAtBetween(
                  companyId,
                  dateFrom.atStartOfDay().toInstant(java.time.ZoneOffset.UTC),
                  dateTo.atTime(23, 59, 59).toInstant(java.time.ZoneOffset.UTC))
              .size();
    }

    List<JobCount> employeesByJob = List.of();
    List<JobCost> laborCostByJob = List.of();

    return HrDashboardResponse.builder()
        .totalEmployees(totalEmployees)
        .employeesByDepartment(employeesByDepartment)
        .leaveSummary(leaveSummary)
        .payrollTotal(payrollTotal)
        .payrollByType(payrollByType)
        .newHires((int) newHires)
        .leaveBalances(leaveBalances)
        .tasksByPriority(tasksByPriority)
        .tasksCompleted((int) tasksCompleted)
        .employeesByJob(employeesByJob)
        .laborCostByJob(laborCostByJob)
        .build();
  }

  private LeaveSummary buildLeaveSummary(
      String companyId, LocalDate dateFrom, LocalDate dateTo, int totalEmployees) {
    if (dateFrom == null) dateFrom = LocalDate.of(LocalDate.now().getYear(), 1, 1);
    if (dateTo == null) dateTo = LocalDate.now();

    List<Leave> approvedLeaves =
        leaveRepository.findByCompanyIdAndStatusAndDateRange(
            companyId, Leave.LeaveStatus.APPROVED, dateFrom, dateTo);

    BigDecimal totalDays =
        approvedLeaves.stream()
            .map(l -> l.getDurationDays() != null ? l.getDurationDays() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    Map<String, List<Leave>> byType =
        approvedLeaves.stream()
            .filter(l -> l.getLeaveType() != null)
            .collect(Collectors.groupingBy(l -> l.getLeaveType().getName()));

    List<LeaveTypeDays> byLeaveType =
        byType.entrySet().stream()
            .map(
                e -> {
                  BigDecimal days =
                      e.getValue().stream()
                          .map(
                              l ->
                                  l.getDurationDays() != null
                                      ? l.getDurationDays()
                                      : BigDecimal.ZERO)
                          .reduce(BigDecimal.ZERO, BigDecimal::add);
                  return LeaveTypeDays.builder()
                      .leaveType(e.getKey())
                      .days(days)
                      .count(e.getValue().size())
                      .build();
                })
            .collect(Collectors.toList());

    List<Leave> allLeaves =
        leaveRepository.findByCompanyIdAndStatusAndDateRange(companyId, null, dateFrom, dateTo);
    Map<Leave.LeaveStatus, Long> byStatus =
        allLeaves.stream()
            .filter(l -> l.getStatus() != null)
            .collect(Collectors.groupingBy(Leave::getStatus, Collectors.counting()));
    List<LeaveStatusCount> byStatusList =
        byStatus.entrySet().stream()
            .map(
                e ->
                    LeaveStatusCount.builder()
                        .status(e.getKey().name())
                        .count(e.getValue().intValue())
                        .build())
            .collect(Collectors.toList());

    long totalWorkingDays = ChronoUnit.DAYS.between(dateFrom, dateTo);
    BigDecimal absenceRate =
        totalWorkingDays > 0 && totalEmployees > 0
            ? totalDays
                .divide(
                    BigDecimal.valueOf(totalWorkingDays * totalEmployees), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;

    return LeaveSummary.builder()
        .totalDaysApproved(totalDays)
        .byLeaveType(byLeaveType)
        .byStatus(byStatusList)
        .absenceRate(absenceRate)
        .build();
  }

  private List<LeaveBalanceItem> buildLeaveBalances(String companyId) {
    int year = LocalDate.now().getYear();
    List<User> employees = userRepository.findByCompanyId(companyId);
    return employees.stream()
        .map(
            u -> {
              try {
                var balance = leaveService.computeBalance(u.getId(), year);
                return LeaveBalanceItem.builder()
                    .userId(u.getId())
                    .userName(u.getFirstName() + " " + u.getLastName())
                    .accrued(balance.getAccruedDays())
                    .taken(balance.getTakenDays())
                    .remaining(balance.getRemainingDays())
                    .build();
              } catch (Exception ex) {
                return null;
              }
            })
        .filter(b -> b != null)
        .collect(Collectors.toList());
  }

  private List<TaskPriorityCount> buildTasksByPriority(String companyId) {
    return List.of();
  }
}
