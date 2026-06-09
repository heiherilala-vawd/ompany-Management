package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.MaintenanceApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateMaintenance;
import com.example.demo.client.model.Maintenance;
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
@ContextConfiguration(initializers = MaintenanceIT.ContextInitializer.class)
class MaintenanceIT {
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
  void administration_can_get_maintenance_by_id() throws Exception {
    MaintenanceApi api = new MaintenanceApi(anApiClient(ADMINISTRATION_TOKEN));

    Maintenance actual =
        api.getMaintenanceById(ADMIN_ID, COMPANY1_ID, MAINTENANCE1_ID, EQUIPMENT1_ID);

    assertEquals(maintenance1(), actual);
  }

  @Test
  void user_with_bad_token_cannot_get_maintenance_by_id() {
    MaintenanceApi api = new MaintenanceApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getMaintenanceById(ADMIN_ID, COMPANY1_ID, MAINTENANCE1_ID, EQUIPMENT1_ID));
  }

  @Test
  void admin_can_get_all_maintenances() throws Exception {
    MaintenanceApi api = new MaintenanceApi(anApiClient(ADMIN_TOKEN));

    List<Maintenance> maintenances =
        api.getMaintenances(ADMIN_ID, COMPANY1_ID, EQUIPMENT1_ID, 1, 100, null);

    assertEquals(1, maintenances.size());
    assertTrue(maintenances.stream().anyMatch(m -> MAINTENANCE1_ID.equals(m.getId())));
  }

  @Test
  void admin_can_filter_maintenances_by_description() throws Exception {
    MaintenanceApi api = new MaintenanceApi(anApiClient(ADMIN_TOKEN));

    List<Maintenance> maintenances =
        api.getMaintenances(ADMIN_ID, COMPANY1_ID, EQUIPMENT1_ID, 1, 100, "moteur");

    assertEquals(1, maintenances.size());
    assertEquals(MAINTENANCE1_ID, maintenances.get(0).getId());
  }

  @Test
  void admin_can_get_all_maintenances_for_equipment2() throws Exception {
    MaintenanceApi api = new MaintenanceApi(anApiClient(ADMIN_TOKEN));

    List<Maintenance> maintenances =
        api.getMaintenances(ADMIN_ID, COMPANY1_ID, EQUIPMENT2_ID, 1, 100, null);

    assertEquals(1, maintenances.size());
    assertEquals(MAINTENANCE2_ID, maintenances.get(0).getId());
  }

  @Test
  @DirtiesContext
  void administration_can_update_maintenances() throws Exception {
    MaintenanceApi api = new MaintenanceApi(anApiClient(ADMINISTRATION_TOKEN));

    CrupdateMaintenance toUpdate = maintenanceToCrupdateMaintenance(maintenance1());
    toUpdate.setDescription("Revision moteur periodique ajustee");

    List<Maintenance> updated = api.crupdateMaintenances(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(MAINTENANCE1_ID, updated.get(0).getId());
    assertEquals("Revision moteur periodique ajustee", updated.get(0).getDescription());
  }

  @Test
  void employee_cannot_create_maintenances() {
    MaintenanceApi api = new MaintenanceApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () -> api.crupdateMaintenances(ADMIN_ID, COMPANY1_ID, List.of(someCreatableMaintenance())));
  }

  @Test
  void administration_cannot_delete_maintenance() {
    MaintenanceApi api = new MaintenanceApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () -> api.deleteMaintenanceById(ADMIN_ID, COMPANY1_ID, MAINTENANCE1_ID, EQUIPMENT1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_maintenance() throws Exception {
    MaintenanceApi api = new MaintenanceApi(anApiClient(ADMIN_TOKEN));

    api.deleteMaintenanceById(ADMIN_ID, COMPANY1_ID, MAINTENANCE2_ID, EQUIPMENT1_ID);

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"Maintenance with id "
            + MAINTENANCE2_ID
            + " not found\"}",
        () -> api.getMaintenanceById(ADMIN_ID, COMPANY1_ID, MAINTENANCE2_ID, EQUIPMENT2_ID));
  }

  @Test
  void admin_cannot_create_maintenance_with_null_expense_id() {
    MaintenanceApi api = new MaintenanceApi(anApiClient(ADMIN_TOKEN));

    CrupdateMaintenance invalid = someCreatableMaintenance();
    invalid.setExpenseId(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Maintenance must be linked to an expense\"}",
        () -> api.crupdateMaintenances(ADMIN_ID, COMPANY1_ID, List.of(invalid)));
  }

  @Test
  void admin_cannot_create_maintenance_without_description() {
    MaintenanceApi api = new MaintenanceApi(anApiClient(ADMIN_TOKEN));

    CrupdateMaintenance invalid = someCreatableMaintenance();
    invalid.setDescription(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Maintenance description is mandatory\"}",
        () -> api.crupdateMaintenances(ADMIN_ID, COMPANY1_ID, List.of(invalid)));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
