package com.example.demo.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.demo.model.User;
import com.example.demo.model.dashboard.HrSummaryResponse;
import com.example.demo.model.hr.Leave;
import com.example.demo.model.hr.LeaveType;
import com.example.demo.model.task.Task;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.hr.LeaveRepository;
import com.example.demo.repository.money.EmployeePaymentRepository;
import com.example.demo.repository.money.ExpenseMoneyRepository;
import com.example.demo.repository.task.TaskRepository;
import com.example.demo.service.dashboard.DashboardHrService;
import com.example.demo.service.hr.LeaveService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardHrServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private LeaveRepository leaveRepository;
  @Mock private LeaveService leaveService;
  @Mock private ExpenseMoneyRepository expenseMoneyRepository;
  @Mock private TaskRepository taskRepository;
  @Mock private JobRepository jobRepository;
  @Mock private EmployeePaymentRepository employeePaymentRepository;

  @InjectMocks private DashboardHrService service;

  @Test
  void getSummary_should_return_zero_when_no_employees() {
    when(userRepository.findByCompanyId("c1")).thenReturn(List.of());

    HrSummaryResponse result = service.getSummary("c1", null, null, null);

    assertEquals(0, result.getTotalEmployees().intValue());
    assertEquals(0, result.getNewHires().intValue());
    assertEquals(BigDecimal.ZERO, result.getPayrollTotal());
    assertEquals(0, result.getTasksCompleted().intValue());
    assertEquals(BigDecimal.ZERO, result.getLeaveDaysApproved());
    assertEquals(BigDecimal.ZERO, result.getAbsenceRate());
  }

  @Test
  void getSummary_should_count_new_hires_in_date_range() {
    User oldUser = User.builder().id("u1").createdAt(Instant.parse("2023-01-01T00:00:00Z")).build();
    User newUser = User.builder().id("u2").createdAt(Instant.parse("2024-06-15T00:00:00Z")).build();

    when(userRepository.findByCompanyId("c1")).thenReturn(List.of(oldUser, newUser));

    HrSummaryResponse result =
        service.getSummary("c1", null, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

    assertEquals(2, result.getTotalEmployees().intValue());
    assertEquals(1, result.getNewHires().intValue());
  }

  @Test
  void getSummary_should_use_payroll_when_jobId_provided() {
    when(userRepository.findByCompanyId("c1")).thenReturn(List.of());
    when(expenseMoneyRepository.sumByJobId("job1")).thenReturn(new BigDecimal("50000"));

    HrSummaryResponse result = service.getSummary("c1", "job1", null, null);

    assertEquals(new BigDecimal("50000"), result.getPayrollTotal());
  }

  @Test
  void getSummary_should_count_tasks_completed_in_range() {
    when(userRepository.findByCompanyId("c1")).thenReturn(List.of());
    Task t1 = Task.builder().id("t1").build();
    Task t2 = Task.builder().id("t2").build();
    when(taskRepository.findByCompanyIdAndCompletedAtBetween(
            eq("c1"), any(Instant.class), any(Instant.class)))
        .thenReturn(List.of(t1, t2));

    HrSummaryResponse result =
        service.getSummary("c1", null, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

    assertEquals(2, result.getTasksCompleted().intValue());
  }

  @Test
  void getSummary_should_compute_absence_rate() {
    User emp = User.builder().id("e1").build();
    LeaveType lt = LeaveType.builder().id("lt1").name("Sick").build();
    Leave approved =
        Leave.builder()
            .id("l1")
            .status(Leave.LeaveStatus.APPROVED)
            .leaveType(lt)
            .durationDays(new BigDecimal("5"))
            .build();

    when(userRepository.findByCompanyId("c1")).thenReturn(List.of(emp));
    when(leaveRepository.findByCompanyIdAndStatusAndDateRange(
            eq("c1"), eq(Leave.LeaveStatus.APPROVED), any(), any()))
        .thenReturn(List.of(approved));

    HrSummaryResponse result =
        service.getSummary("c1", null, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));

    assertEquals(new BigDecimal("5"), result.getLeaveDaysApproved());
    assertTrue(result.getAbsenceRate().compareTo(BigDecimal.ZERO) > 0);
  }

  @Test
  void getSummary_should_return_zero_absence_when_no_working_days() {
    when(userRepository.findByCompanyId("c1")).thenReturn(List.of());

    HrSummaryResponse result =
        service.getSummary("c1", null, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1));

    assertEquals(BigDecimal.ZERO, result.getAbsenceRate());
  }

  @Test
  void getSummary_should_not_count_new_hires_without_dates() {
    User emp = User.builder().id("e1").createdAt(Instant.parse("2024-06-15T00:00:00Z")).build();

    when(userRepository.findByCompanyId("c1")).thenReturn(List.of(emp));

    HrSummaryResponse result = service.getSummary("c1", null, null, null);

    assertEquals(0, result.getNewHires().intValue());
  }
}
