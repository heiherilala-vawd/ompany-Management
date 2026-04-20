package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.TravelMaterialsApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateTravelMaterials;
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
        api.getTravelMaterialsById(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            EXPENSE1_ID,
            TRAVEL_EXPENSE1_ID,
            TRAVEL_MATERIALS1_ID);

    TravelMaterials expected = travelMaterials1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_travel_materials_by_id() {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () ->
            api.getTravelMaterialsById(
                COMPANY1_ID,
                JOB1_ID,
                EMPLOYEE_ID,
                EXPENSE1_ID,
                TRAVEL_EXPENSE1_ID,
                TRAVEL_MATERIALS1_ID));
  }

  @Test
  void admin_can_get_all_travel_materials() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));

    List<TravelMaterials> list =
        api.getTravelMaterials(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            EXPENSE1_ID,
            TRAVEL_EXPENSE1_ID,
            1,
            100,
            null,
            null,
            null,
            null);

    assertEquals(2, list.size());
    assertTrue(list.stream().anyMatch(tm -> TRAVEL_MATERIALS1_ID.equals(tm.getId())));
    assertTrue(list.stream().anyMatch(tm -> TRAVEL_MATERIALS2_ID.equals(tm.getId())));
  }

  @Test
  void admin_can_filter_travel_materials_by_travel_id() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));

    List<TravelMaterials> list =
        api.getTravelMaterials(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            EXPENSE1_ID,
            TRAVEL_EXPENSE1_ID,
            1,
            100,
            TRAVEL_EXPENSE2_ID,
            null,
            null,
            null);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_MATERIALS2_ID, list.get(0).getId());
  }

  @Test
  void admin_can_filter_travel_materials_by_material_id() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));

    List<TravelMaterials> list =
        api.getTravelMaterials(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            EXPENSE1_ID,
            TRAVEL_EXPENSE1_ID,
            1,
            100,
            null,
            MATERIAL1_ID,
            null,
            null);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_MATERIALS1_ID, list.get(0).getId());
  }

  @Test
  void admin_can_filter_travel_materials_by_quantity() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));

    List<TravelMaterials> list =
        api.getTravelMaterials(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            EXPENSE1_ID,
            TRAVEL_EXPENSE1_ID,
            1,
            100,
            null,
            null,
            20,
            null);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_MATERIALS2_ID, list.get(0).getId());
  }

  @Test
  void admin_can_filter_travel_materials_by_quantity_received() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMIN_TOKEN));

    List<TravelMaterials> list =
        api.getTravelMaterials(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            EXPENSE1_ID,
            TRAVEL_EXPENSE1_ID,
            1,
            100,
            null,
            null,
            null,
            5);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_MATERIALS1_ID, list.get(0).getId());
  }

  @Test
  @DirtiesContext
  void employee_can_update_own_travel_materials() throws Exception {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(EMPLOYEE_TOKEN));

    CrupdateTravelMaterials toUpdate = travelMaterialsToCrupdateTravelMaterials(travelMaterials1());
    toUpdate.setQuantityReceived(8);

    List<TravelMaterials> updated =
        api.crupdateTravelMaterials(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, TRAVEL_EXPENSE1_ID, List.of(toUpdate));

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
                COMPANY1_ID,
                JOB1_ID,
                EMPLOYEE_ID,
                EXPENSE1_ID,
                TRAVEL_EXPENSE1_ID,
                List.of(someCreatableTravelMaterials())));
  }

  @Test
  void administration_cannot_delete_travel_materials() {
    TravelMaterialsApi api = new TravelMaterialsApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.deleteTravelMaterialsById(
                COMPANY1_ID,
                JOB1_ID,
                EMPLOYEE_ID,
                EXPENSE1_ID,
                TRAVEL_EXPENSE1_ID,
                TRAVEL_MATERIALS1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
