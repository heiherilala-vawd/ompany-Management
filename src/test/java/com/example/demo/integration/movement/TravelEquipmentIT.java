package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.TravelEquipmentApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateTravelEquipment;
import com.example.demo.client.model.TransportStatus;
import com.example.demo.client.model.TravelEquipment;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestUtils;
import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = TravelEquipmentIT.ContextInitializer.class)
class TravelEquipmentIT {
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
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("db/testdata/V99_1__testdata.sql"));
    }
  }

  @Test
  void employee_can_get_own_travel_equipment_by_id() throws Exception {
    TravelEquipmentApi api = new TravelEquipmentApi(anApiClient(EMPLOYEE_TOKEN));

    TravelEquipment actual =
        api.getTravelEquipmentById(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            EXPENSE1_ID,
            TRAVEL_EXPENSE1_ID,
            TRAVEL_EQUIPMENT1_ID);

    TravelEquipment expected = travelEquipment1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_travel_equipment_by_id() {
    TravelEquipmentApi api = new TravelEquipmentApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () ->
            api.getTravelEquipmentById(
                COMPANY1_ID,
                JOB1_ID,
                EMPLOYEE_ID,
                EXPENSE1_ID,
                TRAVEL_EXPENSE1_ID,
                TRAVEL_EQUIPMENT1_ID));
  }

  @Test
  void admin_can_get_all_travel_equipment() throws Exception {
    TravelEquipmentApi api = new TravelEquipmentApi(anApiClient(ADMIN_TOKEN));

    List<TravelEquipment> list =
        api.getTravelEquipment(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, TRAVEL_EXPENSE1_ID, 1, 100, null);

    assertEquals(2, list.size());
    assertTrue(list.stream().anyMatch(te -> TRAVEL_EQUIPMENT1_ID.equals(te.getId())));
    assertTrue(list.stream().anyMatch(te -> TRAVEL_EQUIPMENT2_ID.equals(te.getId())));
  }

  @Test
  @DirtiesContext
  void employee_can_update_own_travel_equipment() throws Exception {
    TravelEquipmentApi api = new TravelEquipmentApi(anApiClient(EMPLOYEE_TOKEN));

    CrupdateTravelEquipment toUpdate = travelEquipmentToCrupdateTravelEquipment(travelEquipment1());
    toUpdate.setStatus(TransportStatus.ARRIVED);
    toUpdate.setQuantity(3);

    List<TravelEquipment> updated =
        api.crupdateTravelEquipment(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, TRAVEL_EXPENSE1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(TRAVEL_EQUIPMENT1_ID, updated.get(0).getId());
    assertEquals(TransportStatus.ARRIVED, updated.get(0).getStatus());
    assertEquals(3, updated.get(0).getQuantity());
  }

  @Test
  void warehouse_worker_cannot_crupdate_travel_equipment() {
    TravelEquipmentApi api = new TravelEquipmentApi(anApiClient(WAREHOUSE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateTravelEquipment(
                COMPANY1_ID,
                JOB1_ID,
                EMPLOYEE_ID,
                EXPENSE1_ID,
                TRAVEL_EXPENSE1_ID,
                List.of(someCreatableTravelEquipment())));
  }

  @Test
  void administration_cannot_delete_travel_equipment() {
    TravelEquipmentApi api = new TravelEquipmentApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.deleteTravelEquipmentById(
                COMPANY1_ID,
                JOB1_ID,
                EMPLOYEE_ID,
                EXPENSE1_ID,
                TRAVEL_EXPENSE1_ID,
                TRAVEL_EQUIPMENT1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
