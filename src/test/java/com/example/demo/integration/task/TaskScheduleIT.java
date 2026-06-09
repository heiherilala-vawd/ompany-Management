package com.example.demo.integration.task;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.SentryConf;
import com.example.demo.client.api.TaskScheduleApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateTaskSchedule;
import com.example.demo.client.model.TaskPriority;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestDataSqlLoader;
import com.example.demo.integration.conf.TestUtils;
import com.example.demo.model.task.TaskAssignment;
import com.example.demo.repository.task.TaskAssignmentRepository;
import com.example.demo.repository.task.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = "task.scheduler.cron=0/4 * * * * ?")
@Testcontainers
@ContextConfiguration(initializers = TaskScheduleIT.ContextInitializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TaskScheduleIT {
  @Autowired private DataSource dataSource;
  @Autowired private TaskRepository taskRepository;
  @Autowired private TaskAssignmentRepository taskAssignmentRepository;

  @MockitoBean private SentryConf sentryConf;
  @MockitoBean private AuthenticationManager authenticationManagerMock;
  @MockitoBean private JwtUtils jwtServiceMock;

  private static ApiClient anApiClient(String token) {
    return TestUtils.anApiClient(token, ContextInitializer.SERVER_PORT);
  }

  @BeforeEach
  void setUp() throws Exception {
    TestUtils.setUpJwtService(jwtServiceMock);
    TestUtils.setUpAuthenticationManager(authenticationManagerMock);
    TestDataSqlLoader.executeAllSqlScripts(dataSource);
  }

  @Test
  void scheduler_generates_multiple_tasks_from_schedule() throws Exception {
    TaskScheduleApi api = new TaskScheduleApi(anApiClient(ADMIN_TOKEN));
    CrupdateTaskSchedule toCreate = new CrupdateTaskSchedule();
    toCreate.setId(UUID.randomUUID().toString());
    toCreate.setTitle("Test auto-génération");
    toCreate.setDescription("Test 8s");
    toCreate.setPriority(TaskPriority.HIGH);
    toCreate.setFrequency("0/8 * * * * ?");
    toCreate.setScheduledDate(LocalDate.of(2020, 1, 1));
    toCreate.setCompanyId(COMPANY1_ID);
    toCreate.setAssignedUserIds(List.of(ADMIN_ID, EMPLOYEE_ID));
    api.crupdateTaskSchedules(ADMIN_ID, COMPANY1_ID, List.of(toCreate));

    Thread.sleep(10000);

    List<com.example.demo.model.task.Task> generatedTasks =
        taskRepository.findByCompanyId(COMPANY1_ID).stream()
            .filter(t -> "Test auto-génération".equals(t.getTitle()))
            .collect(Collectors.toList());
    assertTrue(
        generatedTasks.size() > 1,
        "Should have generated at least 2 tasks, got: " + generatedTasks.size());

    for (com.example.demo.model.task.Task task : generatedTasks) {
      List<TaskAssignment> assignments = taskAssignmentRepository.findByTaskId(task.getId());
      assertEquals(2, assignments.size());
      assertTrue(assignments.stream().anyMatch(a -> ADMIN_ID.equals(a.getUser().getId())));
      assertTrue(assignments.stream().anyMatch(a -> EMPLOYEE_ID.equals(a.getUser().getId())));
    }
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
