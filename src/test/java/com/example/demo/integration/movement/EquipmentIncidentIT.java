package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.EquipmentIncidentApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateEquipmentIncident;
import com.example.demo.client.model.EquipmentIncident;
import com.example.demo.client.model.IncidentType;
import com.example.demo.client.model.PaginatedResponse;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = EquipmentIncidentIT.ContextInitializer.class)
class EquipmentIncidentIT {
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
  void admin_can_get_equipment_incident_by_id() throws Exception {
    EquipmentIncidentApi api = new EquipmentIncidentApi(anApiClient(ADMIN_TOKEN));

    EquipmentIncident actual = api.getEquipmentIncidentById(ADMIN_ID, COMPANY1_ID, "incident1_id");

    assertEquals("incident1_id", actual.getId());
    assertEquals(IncidentType.DAMAGED, actual.getIncidentType());
    assertEquals(EQUIPMENT1_ID, actual.getEquipmentId());
    assertEquals(USER1_ID, actual.getUserId());
    assertNotNull(actual.getTravelId());
  }

  @Test
  void user_with_bad_token_cannot_get_equipment_incident() {
    EquipmentIncidentApi api = new EquipmentIncidentApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getEquipmentIncidentById(ADMIN_ID, COMPANY1_ID, "incident1_id"));
  }

  @Test
  void admin_can_get_all_equipment_incidents() throws Exception {
    EquipmentIncidentApi api = new EquipmentIncidentApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getEquipmentIncidents(ADMIN_ID, COMPANY1_ID, 1, 100, null, null, null, null);
    List<EquipmentIncident> list = extractData(resp, EquipmentIncident.class);

    assertTrue(list.size() >= 2);
  }

  @Test
  void admin_can_filter_by_equipment_id() throws Exception {
    EquipmentIncidentApi api = new EquipmentIncidentApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getEquipmentIncidents(ADMIN_ID, COMPANY1_ID, 1, 100, EQUIPMENT1_ID, null, null, null);
    List<EquipmentIncident> list = extractData(resp, EquipmentIncident.class);

    assertTrue(list.size() >= 1);
    assertTrue(list.stream().allMatch(i -> EQUIPMENT1_ID.equals(i.getEquipmentId())));
  }

  @Test
  void admin_can_filter_by_incident_type() throws Exception {
    EquipmentIncidentApi api = new EquipmentIncidentApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getEquipmentIncidents(
            ADMIN_ID, COMPANY1_ID, 1, 100, null, IncidentType.DAMAGED, null, null);
    List<EquipmentIncident> list = extractData(resp, EquipmentIncident.class);

    assertTrue(list.size() >= 1);
    assertTrue(list.stream().allMatch(i -> IncidentType.DAMAGED == i.getIncidentType()));
  }

  @Test
  void admin_can_filter_by_user_id() throws Exception {
    EquipmentIncidentApi api = new EquipmentIncidentApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getEquipmentIncidents(ADMIN_ID, COMPANY1_ID, 1, 100, null, null, USER1_ID, null);
    List<EquipmentIncident> list = extractData(resp, EquipmentIncident.class);

    assertTrue(list.size() >= 1);
    assertTrue(list.stream().allMatch(i -> USER1_ID.equals(i.getUserId())));
  }

  @Test
  void admin_can_filter_by_travel_id() throws Exception {
    EquipmentIncidentApi api = new EquipmentIncidentApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getEquipmentIncidents(
            ADMIN_ID, COMPANY1_ID, 1, 100, null, null, null, TRAVEL_EXPENSE1_ID);
    List<EquipmentIncident> list = extractData(resp, EquipmentIncident.class);

    assertTrue(list.size() >= 1);
    assertTrue(list.stream().allMatch(i -> TRAVEL_EXPENSE1_ID.equals(i.getTravelId())));
  }

  @Test
  @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
  void admin_can_create_equipment_incident() throws Exception {
    EquipmentIncidentApi api = new EquipmentIncidentApi(anApiClient(ADMIN_TOKEN));

    var toCreate =
        new CrupdateEquipmentIncident()
            .id("inc_new_001")
            .incidentType(IncidentType.LOST)
            .equipmentId(EQUIPMENT1_ID)
            .userId(USER1_ID);

    List<EquipmentIncident> created =
        api.crupdateEquipmentIncidents(ADMIN_ID, COMPANY1_ID, List.of(toCreate));

    assertEquals(1, created.size());
    assertEquals("inc_new_001", created.getFirst().getId());
    assertEquals(IncidentType.LOST, created.getFirst().getIncidentType());
    assertEquals(EQUIPMENT1_ID, created.getFirst().getEquipmentId());
  }

  @Test
  @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
  void admin_can_update_equipment_incident() throws Exception {
    EquipmentIncidentApi api = new EquipmentIncidentApi(anApiClient(ADMIN_TOKEN));

    var toUpdate =
        new CrupdateEquipmentIncident()
            .id("incident1_id")
            .incidentType(IncidentType.LOST)
            .equipmentId(EQUIPMENT1_ID)
            .userId(USER1_ID);

    List<EquipmentIncident> updated =
        api.crupdateEquipmentIncidents(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals("incident1_id", updated.getFirst().getId());
    assertEquals(IncidentType.LOST, updated.getFirst().getIncidentType());
  }

  @Test
  @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
  void admin_can_delete_equipment_incident() throws Exception {
    EquipmentIncidentApi api = new EquipmentIncidentApi(anApiClient(ADMIN_TOKEN));

    api.deleteEquipmentIncidentById(ADMIN_ID, COMPANY1_ID, "incident1_id");

    assertThrows(
        com.example.demo.client.invoker.ApiException.class,
        () -> api.getEquipmentIncidentById(ADMIN_ID, COMPANY1_ID, "incident1_id"));
  }

  @Test
  void employee_can_get_own_equipment_incident() throws Exception {
    EquipmentIncidentApi api = new EquipmentIncidentApi(anApiClient(EMPLOYEE_TOKEN));

    EquipmentIncident actual = api.getEquipmentIncidentById(ADMIN_ID, COMPANY1_ID, "incident1_id");

    assertEquals("incident1_id", actual.getId());
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
