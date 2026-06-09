package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.MaintenanceScheduleApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateMaintenanceSchedule;
import com.example.demo.client.model.MaintenanceSchedule;
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
@ContextConfiguration(initializers = MaintenanceScheduleIT.ContextInitializer.class)
class MaintenanceScheduleIT {
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
  void administration_can_get_maintenance_schedule_by_id() throws Exception {
    MaintenanceScheduleApi api = new MaintenanceScheduleApi(anApiClient(ADMINISTRATION_TOKEN));

    MaintenanceSchedule actual =
        api.getMaintenanceScheduleById(ADMINISTRATION_ID, COMPANY1_ID, MS1_ID);
    MaintenanceSchedule expected = maintenanceSchedule1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_maintenance_schedule_by_id() {
    MaintenanceScheduleApi api = new MaintenanceScheduleApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getMaintenanceScheduleById(BAD_TOKEN, COMPANY1_ID, MS1_ID));
  }

  @Test
  void administration_can_get_all_maintenance_schedules() throws Exception {
    MaintenanceScheduleApi api = new MaintenanceScheduleApi(anApiClient(ADMINISTRATION_TOKEN));

    List<MaintenanceSchedule> schedules =
        api.getMaintenanceSchedules(ADMINISTRATION_ID, COMPANY1_ID, null, null);

    assertEquals(2, schedules.size());
    assertTrue(schedules.stream().anyMatch(ms -> MS1_ID.equals(ms.getId())));
    assertTrue(schedules.stream().anyMatch(ms -> MS2_ID.equals(ms.getId())));
  }

  @Test
  void employee_cannot_get_all_maintenance_schedules() {
    MaintenanceScheduleApi api = new MaintenanceScheduleApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () -> api.getMaintenanceSchedules(EMPLOYEE_ID, COMPANY1_ID, null, null));
  }

  @Test
  @DirtiesContext
  void admin_can_update_maintenance_schedule() throws Exception {
    MaintenanceScheduleApi api = new MaintenanceScheduleApi(anApiClient(ADMIN_TOKEN));

    CrupdateMaintenanceSchedule toUpdate =
        maintenanceScheduleToCrupdateMaintenanceSchedule(maintenanceSchedule1());
    toUpdate.setDescription("Révision moteur périodique - mise à jour");

    List<MaintenanceSchedule> updated =
        api.crupdateMaintenanceSchedules(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(MS1_ID, updated.get(0).getId());
    assertEquals("Révision moteur périodique - mise à jour", updated.get(0).getDescription());
  }

  @Test
  void employee_cannot_create_maintenance_schedule() {
    MaintenanceScheduleApi api = new MaintenanceScheduleApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateMaintenanceSchedules(
                EMPLOYEE_ID, COMPANY1_ID, List.of(someCreatableMaintenanceSchedule())));
  }

  @Test
  void administration_cannot_delete_maintenance_schedule() {
    MaintenanceScheduleApi api = new MaintenanceScheduleApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () -> api.deleteMaintenanceScheduleById(ADMINISTRATION_ID, COMPANY1_ID, MS1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_maintenance_schedule() throws Exception {
    MaintenanceScheduleApi api = new MaintenanceScheduleApi(anApiClient(ADMIN_TOKEN));

    api.deleteMaintenanceScheduleById(ADMIN_ID, COMPANY1_ID, MS1_ID);

    List<MaintenanceSchedule> schedules =
        api.getMaintenanceSchedules(ADMIN_ID, COMPANY1_ID, null, null);
    assertEquals(1, schedules.size());
    assertEquals(MS2_ID, schedules.get(0).getId());
  }

  @Test
  void admin_gets_not_found_when_maintenance_schedule_does_not_exist() {
    MaintenanceScheduleApi api = new MaintenanceScheduleApi(anApiClient(ADMIN_TOKEN));

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"MaintenanceSchedule nonexistent_ms not found\"}",
        () -> api.getMaintenanceScheduleById(ADMIN_ID, COMPANY1_ID, "nonexistent_ms"));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
