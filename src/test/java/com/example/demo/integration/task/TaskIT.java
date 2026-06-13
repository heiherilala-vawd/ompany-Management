package com.example.demo.integration.task;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.SentryConf;
import com.example.demo.client.api.TaskApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.PaginatedResponse;
import com.example.demo.client.model.CrupdateTask;
import com.example.demo.client.model.PaginatedResponse;
import com.example.demo.client.model.Task;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestDataSqlLoader;
import com.example.demo.integration.conf.TestUtils;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = TaskIT.ContextInitializer.class)
class TaskIT {
  @Autowired private DataSource dataSource;

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
  void administration_can_get_all_tasks() throws Exception {
    TaskApi api = new TaskApi(anApiClient(ADMINISTRATION_TOKEN));
    PaginatedResponse resp = api.getTasks(ADMIN_ID, COMPANY1_ID);

    List<Task> tasks = extractData(resp, Task.class);
    assertEquals(2, tasks.size());
    assertTrue(tasks.stream().anyMatch(t -> "task1_id".equals(t.getId())));
    assertTrue(tasks.stream().anyMatch(t -> "task2_id".equals(t.getId())));
  }

  @Test
  void user_with_bad_token_cannot_get_tasks() {
    TaskApi api = new TaskApi(anApiClient(BAD_TOKEN));
    assertThrowsNotAuthorizedException(() -> api.getTasks(ADMIN_ID, COMPANY1_ID));
  }

  @Test
  void administration_can_get_task_by_id() throws Exception {
    TaskApi api = new TaskApi(anApiClient(ADMINISTRATION_TOKEN));
    Task task = api.getTaskById(ADMIN_ID, COMPANY1_ID, "task1_id");
    assertEquals("task1_id", task.getId());
    assertEquals("Vérifier le matériel", task.getTitle());
    assertEquals(2, task.getAssignedUserIds().size());
    assertEquals(true, task.getCompleted());
  }

  @Test
  @DirtiesContext
  void admin_can_update_task() throws Exception {
    TaskApi api = new TaskApi(anApiClient(ADMIN_TOKEN));
    CrupdateTask toUpdate = someCreatableTask();
    toUpdate.setId("task1_id");
    toUpdate.setTitle("Updated title");
    List<Task> updated = api.crupdateTasks(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));
    assertEquals(1, updated.size());
    assertEquals("task1_id", updated.get(0).getId());
    assertEquals("Updated title", updated.get(0).getTitle());
  }

  @Test
  void employee_cannot_create_tasks() {
    TaskApi api = new TaskApi(anApiClient(EMPLOYEE_TOKEN));
    assertThrowsForbiddenException(
        () -> api.crupdateTasks(EMPLOYEE_ID, COMPANY1_ID, List.of(someCreatableTask())));
  }

  @Test
  void employee_can_list_own_tasks() throws Exception {
    TaskApi api = new TaskApi(anApiClient(EMPLOYEE_TOKEN));

    PaginatedResponse resp = api.getTasks(EMPLOYEE_ID, COMPANY1_ID);


    List<Task> tasks = extractData(resp, Task.class);

    assertEquals(2, tasks.size());
    assertTrue(tasks.stream().anyMatch(t -> "task1_id".equals(t.getId())));
    assertTrue(tasks.stream().anyMatch(t -> "task2_id".equals(t.getId())));
  }

  @Test
  void warehouse_cannot_get_task_not_assigned() {
    TaskApi api = new TaskApi(anApiClient(WAREHOUSE_TOKEN));

    assertThrowsApiException(
        "{\"type\":\"403 FORBIDDEN\",\"message\":\"Task not assigned to the user\"}",
        () -> api.getTaskById(WAREHOUSE_ID, COMPANY1_ID, "task1_id"));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_task() throws Exception {
    TaskApi api = new TaskApi(anApiClient(ADMIN_TOKEN));
    api.deleteTaskById(ADMIN_ID, COMPANY1_ID, "task1_id");
    PaginatedResponse resp = api.getTasks(ADMIN_ID, COMPANY1_ID);

    List<Task> tasks = extractData(resp, Task.class);
    assertEquals(1, tasks.size());
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
