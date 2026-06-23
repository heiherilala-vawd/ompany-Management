package com.example.demo.Service.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.model.money.IncomeMoney;
import com.example.demo.model.task.Task;
import com.example.demo.model.task.TaskPriority;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.money.IncomeMoneyRepository;
import com.example.demo.repository.task.TaskRepository;
import com.example.demo.service.dashboard.TaskAutoCreationService;
import com.example.demo.service.notification.NotificationService;
import com.example.demo.service.utils.ModificationUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskAutoCreationServiceTest {

  @Mock private IncomeMoneyRepository incomeMoneyRepository;
  @Mock private JobRepository jobRepository;
  @Mock private TaskRepository taskRepository;
  @Mock private UserRepository userRepository;
  @Mock private NotificationService notificationService;
  @Mock private ModificationUtils modificationUtils;

  @InjectMocks private TaskAutoCreationService service;

  private IncomeMoney dueIncome;
  private IncomeMoney incomeDueLater;
  private Job job;
  private Job endedJobWithNoIncome;

  private User admin;

  @Captor private ArgumentCaptor<Task> taskCaptor;

  @BeforeEach
  void setUp() {
    job = Job.builder().id("job-001").description("Test job").build();

    endedJobWithNoIncome =
        Job.builder().id("job-002").description("Ended job with no invoice").build();

    dueIncome =
        IncomeMoney.builder()
            .id("income-001")
            .amount(new BigDecimal("1500.00"))
            .invoiceReference("INV-001")
            .dueDate(LocalDate.now().plusDays(7))
            .job(job)
            .build();

    incomeDueLater =
        IncomeMoney.builder()
            .id("income-002")
            .amount(new BigDecimal("500.00"))
            .invoiceReference("INV-002")
            .dueDate(LocalDate.now().plusDays(30))
            .job(job)
            .build();

    admin =
        User.builder()
            .id("admin-001")
            .email("admin@test.com")
            .firstName("Admin")
            .lastName("User")
            .role(User.Role.ADMIN)
            .build();
  }

  // ==================== TESTS POUR autoCreateTasks ====================

  @Test
  void autoCreateTasks_ShouldCreateInvoiceReminders_WhenIncomeDueIn7Days() {
    User currentUser = User.builder().id("user-current").build();
    when(incomeMoneyRepository.findAll()).thenReturn(List.of(dueIncome, incomeDueLater));
    when(jobRepository.findByEndDateBefore(any())).thenReturn(List.of());
    when(modificationUtils.takePrimaryUser()).thenReturn(currentUser);
    when(userRepository.findAll()).thenReturn(List.of(admin));

    service.autoCreateTasks();

    ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
    verify(taskRepository, times(1)).save(taskCaptor.capture());
    Task savedTask = taskCaptor.getValue();
    assertThat(savedTask.getTitle()).contains("INV-001");
    assertThat(savedTask.getPriority()).isEqualTo(TaskPriority.MEDIUM);
    assertThat(savedTask.getDueDate()).isEqualTo(LocalDate.now().plusDays(7));
    assertThat(savedTask.getCompleted()).isFalse();

    verify(notificationService)
        .createForUser(eq(admin), anyString(), anyString(), any(Task.class), eq(currentUser));
  }

  @Test
  void autoCreateTasks_ShouldSkipIncome_WhenDueDateNotMatching() {
    when(incomeMoneyRepository.findAll()).thenReturn(List.of(incomeDueLater));
    when(jobRepository.findByEndDateBefore(any())).thenReturn(List.of());

    service.autoCreateTasks();

    verify(taskRepository, never()).save(any(Task.class));
    verify(notificationService, never())
        .createForUser(any(), anyString(), anyString(), any(Task.class), any());
  }

  @Test
  void autoCreateTasks_ShouldCreateClientReminders_WhenJobEndedWithNoIncome() {
    User currentUser = User.builder().id("user-current").build();
    when(incomeMoneyRepository.findAll()).thenReturn(List.of());
    when(jobRepository.findByEndDateBefore(any())).thenReturn(List.of(endedJobWithNoIncome));
    when(incomeMoneyRepository.sumByJobId(endedJobWithNoIncome.getId()))
        .thenReturn(BigDecimal.ZERO);
    when(modificationUtils.takePrimaryUser()).thenReturn(currentUser);
    when(userRepository.findAll()).thenReturn(List.of(admin));

    service.autoCreateTasks();

    verify(taskRepository).save(taskCaptor.capture());
    Task savedTask = taskCaptor.getValue();
    assertThat(savedTask.getTitle()).contains("Facturer travaux");
    assertThat(savedTask.getPriority()).isEqualTo(TaskPriority.LOW);
    assertThat(savedTask.getCompleted()).isFalse();

    verify(notificationService)
        .createForUser(eq(admin), anyString(), anyString(), any(Task.class), eq(currentUser));
  }

  @Test
  void autoCreateTasks_ShouldSkipJob_WhenIncomeExists() {
    when(incomeMoneyRepository.findAll()).thenReturn(List.of());
    when(jobRepository.findByEndDateBefore(any())).thenReturn(List.of(endedJobWithNoIncome));
    when(incomeMoneyRepository.sumByJobId(endedJobWithNoIncome.getId()))
        .thenReturn(new BigDecimal("5000.00"));

    service.autoCreateTasks();

    verify(taskRepository, never()).save(any(Task.class));
    verify(notificationService, never())
        .createForUser(any(), anyString(), anyString(), any(Task.class), any());
  }

  @Test
  void autoCreateTasks_ShouldDoNothing_WhenNoIncomeDueAndNoJobEnded() {
    when(incomeMoneyRepository.findAll()).thenReturn(List.of());
    when(jobRepository.findByEndDateBefore(any())).thenReturn(List.of());

    service.autoCreateTasks();

    verify(taskRepository, never()).save(any(Task.class));
    verify(notificationService, never())
        .createForUser(any(), anyString(), anyString(), any(Task.class), any());
  }

  @Test
  void autoCreateTasks_ShouldHandleIncomeWithNullJob() {
    User currentUser = User.builder().id("user-current").build();
    IncomeMoney incomeNoJob =
        IncomeMoney.builder()
            .id("income-no-job")
            .amount(new BigDecimal("2000.00"))
            .invoiceReference("INV-NO-JOB")
            .dueDate(LocalDate.now().plusDays(7))
            .build();

    when(incomeMoneyRepository.findAll()).thenReturn(List.of(incomeNoJob));
    when(jobRepository.findByEndDateBefore(any())).thenReturn(List.of());
    when(modificationUtils.takePrimaryUser()).thenReturn(currentUser);
    when(userRepository.findAll()).thenReturn(List.of(admin));

    service.autoCreateTasks();

    verify(taskRepository).save(taskCaptor.capture());
    assertThat(taskCaptor.getValue().getCompany()).isNull();
  }

  @Test
  void autoCreateTasks_ShouldFilterNonAdminUsers() {
    User currentUser = User.builder().id("user-current").build();
    User employee =
        User.builder().id("employee-001").email("emp@test.com").role(User.Role.EMPLOYEE).build();

    when(incomeMoneyRepository.findAll()).thenReturn(List.of(dueIncome));
    when(jobRepository.findByEndDateBefore(any())).thenReturn(List.of());
    when(modificationUtils.takePrimaryUser()).thenReturn(currentUser);
    when(userRepository.findAll()).thenReturn(List.of(admin, employee));

    service.autoCreateTasks();

    verify(notificationService, times(1))
        .createForUser(any(User.class), anyString(), anyString(), any(Task.class), eq(currentUser));
  }

  @Test
  void autoCreateTasks_ShouldFilterNonAdminUsers_WhenCreatingClientReminders() {
    User currentUser = User.builder().id("user-current").build();
    User employee =
        User.builder().id("employee-001").email("emp@test.com").role(User.Role.EMPLOYEE).build();

    when(incomeMoneyRepository.findAll()).thenReturn(List.of());
    when(jobRepository.findByEndDateBefore(any())).thenReturn(List.of(endedJobWithNoIncome));
    when(incomeMoneyRepository.sumByJobId(endedJobWithNoIncome.getId()))
        .thenReturn(BigDecimal.ZERO);
    when(modificationUtils.takePrimaryUser()).thenReturn(currentUser);
    when(userRepository.findAll()).thenReturn(List.of(admin, employee));

    service.autoCreateTasks();

    verify(notificationService, times(1))
        .createForUser(any(User.class), anyString(), anyString(), any(Task.class), eq(currentUser));
  }

  @Test
  void autoCreateTasks_ShouldNotifyAllAdmins() {
    User currentUser = User.builder().id("user-current").build();
    User admin2 =
        User.builder().id("admin-002").email("admin2@test.com").role(User.Role.ADMIN).build();

    when(incomeMoneyRepository.findAll()).thenReturn(List.of(dueIncome));
    when(jobRepository.findByEndDateBefore(any())).thenReturn(List.of());
    when(modificationUtils.takePrimaryUser()).thenReturn(currentUser);
    when(userRepository.findAll()).thenReturn(List.of(admin, admin2));

    service.autoCreateTasks();

    verify(notificationService, times(2))
        .createForUser(any(User.class), anyString(), anyString(), any(Task.class), eq(currentUser));
  }
}
