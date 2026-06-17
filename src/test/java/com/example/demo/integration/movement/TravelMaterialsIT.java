package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.TravelMaterialsApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateTravelMaterials;
import com.example.demo.client.model.PaginatedResponse;
import com.example.demo.client.model.TravelMaterials;
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
@ContextConfiguration(initializers = TravelMaterialsIT.ContextInitializer.class)
class TravelMaterialsIT {
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
  void employee_can_get_own_travel_materials_by_id() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(EMPLOYEE_TOKEN));

    TravelMaterials actual =
        api.getTravelMaterialsById(EMPLOYEE_ID, COMPANY1_ID, JOB1_ID, TRAVEL_MATERIALS1_ID);

    TravelMaterials expected = travelMaterials1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    if (actual.getArrivalLogs() != null && !actual.getArrivalLogs().isEmpty()) {
      for (int i = 0;
          i < expected.getArrivalLogs().size() && i < actual.getArrivalLogs().size();
          i++) {
        expected
            .getArrivalLogs()
            .get(i)
            .setCreatedAt(actual.getArrivalLogs().get(i).getCreatedAt());
        expected
            .getArrivalLogs()
            .get(i)
            .setUpdatedAt(actual.getArrivalLogs().get(i).getUpdatedAt());
        expected
            .getArrivalLogs()
            .get(i)
            .setCreatedBy(actual.getArrivalLogs().get(i).getCreatedBy());
        expected
            .getArrivalLogs()
            .get(i)
            .setUpdatedBy(actual.getArrivalLogs().get(i).getUpdatedBy());
      }
    }

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_travel_materials_by_id() {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getTravelMaterialsById(ADMIN_ID, COMPANY1_ID, JOB1_ID, TRAVEL_MATERIALS1_ID));
  }

  @Test
  void admin_can_get_all_travel_materials() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getTravelMaterials(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null, null, null, null, null);
    List<TravelMaterials> list = extractData(resp, TravelMaterials.class);

    assertEquals(2, list.size());
    assertTrue(list.stream().anyMatch(tm -> TRAVEL_MATERIALS1_ID.equals(tm.getId())));
    assertTrue(list.stream().anyMatch(tm -> TRAVEL_MATERIALS2_ID.equals(tm.getId())));
  }

  @Test
  void admin_can_filter_travel_materials_by_travel_id() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getTravelMaterials(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            1,
            100,
            TRAVEL_EXPENSE2_ID,
            null,
            null,
            null,
            null,
            null);
    List<TravelMaterials> list = extractData(resp, TravelMaterials.class);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_MATERIALS2_ID, list.get(0).getId());
  }

  @Test
  void admin_can_filter_travel_materials_by_material_id() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getTravelMaterials(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, MATERIAL1_ID, null, null, null, null);
    List<TravelMaterials> list = extractData(resp, TravelMaterials.class);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_MATERIALS1_ID, list.get(0).getId());
  }

  @Test
  void admin_can_filter_travel_materials_by_quantity() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getTravelMaterials(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null, 20, null, null, null);
    List<TravelMaterials> list = extractData(resp, TravelMaterials.class);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_MATERIALS2_ID, list.get(0).getId());
  }

  @Test
  void admin_can_filter_travel_materials_by_quantity_received() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getTravelMaterials(
            ADMIN_ID, COMPANY1_ID, JOB1_ID, 1, 100, null, null, null, 5, null, null);
    List<TravelMaterials> list = extractData(resp, TravelMaterials.class);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_MATERIALS1_ID, list.get(0).getId());
  }

  @Test
  void admin_can_filter_travel_materials_by_arrival_location() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));

    // Filters by associated travel's arrival_location.
    // travel_expense2 has arrival_location = warehouse1_id, its material is travel_materials2
    PaginatedResponse resp =
        api.getTravelMaterials(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null, null, null, WAREHOUSE1_ID, null);
    List<TravelMaterials> list = extractData(resp, TravelMaterials.class);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_MATERIALS2_ID, list.get(0).getId());
  }

  @Test
  void admin_can_filter_travel_materials_not_arrived() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getTravelMaterials(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null, null, null, null, true);
    List<TravelMaterials> list = extractData(resp, TravelMaterials.class);

    assertEquals(2, list.size());
  }

  @Test
  @DirtiesContext
  void employee_can_update_own_travel_materials() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(EMPLOYEE_TOKEN));

    CrupdateTravelMaterials toUpdate = travelMaterialsToCrupdateTravelMaterials(travelMaterials1());
    toUpdate.setQuantityReceived(8);

    List<TravelMaterials> updated =
        api.crupdateTravelMaterials(EMPLOYEE_ID, COMPANY1_ID, JOB1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(TRAVEL_MATERIALS1_ID, updated.get(0).getId());
    assertEquals(8, updated.get(0).getQuantityReceived());
    assertEquals(10, updated.get(0).getQuantity());
  }

  @Test
  void warehouse_worker_cannot_crupdate_travel_materials() {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(WAREHOUSE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateTravelMaterials(
                ADMIN_ID, COMPANY1_ID, JOB1_ID, List.of(someCreatableTravelMaterials())));
  }

  @Test
  void administration_cannot_delete_travel_materials() {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () -> api.deleteTravelMaterialsById(ADMIN_ID, COMPANY1_ID, JOB1_ID, TRAVEL_MATERIALS1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_travel_materials() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));

    api.deleteTravelMaterialsById(ADMIN_ID, COMPANY1_ID, JOB1_ID, TRAVEL_MATERIALS2_ID);

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"TravelMaterials with id "
            + TRAVEL_MATERIALS2_ID
            + " not found\"}",
        () -> api.getTravelMaterialsById(ADMIN_ID, COMPANY1_ID, JOB1_ID, TRAVEL_MATERIALS2_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
