package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.TravelContainerApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateTravelContainer;
import com.example.demo.client.model.PaginatedResponse;
import com.example.demo.client.model.TravelContainer;
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
@ContextConfiguration(initializers = TravelContainerIT.ContextInitializer.class)
class TravelContainerIT {
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
  void employee_can_get_own_travel_container_by_id() throws Exception {
    TravelContainerApi api = new TravelContainerApi(anApiClient(EMPLOYEE_TOKEN));

    TravelContainer actual =
        api.getTravelContainerById(EMPLOYEE_ID, COMPANY1_ID, JOB1_ID, TRAVEL_CONTAINER1_ID);

    assertEquals(TRAVEL_CONTAINER1_ID, actual.getId());
    assertEquals("Blue Box", actual.getName());
    assertEquals("Grande boîte plastique", actual.getDescription());
    assertNotNull(actual.getTravel());
    assertEquals(TRAVEL_EXPENSE1_ID, actual.getTravel().getId());
  }

  @Test
  void user_with_bad_token_cannot_get_travel_container_by_id() {
    TravelContainerApi api = new TravelContainerApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getTravelContainerById(ADMIN_ID, COMPANY1_ID, JOB1_ID, TRAVEL_CONTAINER1_ID));
  }

  @Test
  void admin_can_get_all_travel_containers() throws Exception {
    TravelContainerApi api = new TravelContainerApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getTravelContainers(ADMIN_ID, COMPANY1_ID, JOB1_ID, 1, 100, null, null);
    List<TravelContainer> list = extractData(resp, TravelContainer.class);

    assertEquals(2, list.size());
    assertTrue(list.stream().anyMatch(tc -> TRAVEL_CONTAINER1_ID.equals(tc.getId())));
    assertTrue(list.stream().anyMatch(tc -> TRAVEL_CONTAINER2_ID.equals(tc.getId())));
  }

  @Test
  void admin_can_filter_travel_containers_by_travel_id() throws Exception {
    TravelContainerApi api = new TravelContainerApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getTravelContainers(ADMIN_ID, COMPANY1_ID, JOB1_ID, 1, 100, TRAVEL_EXPENSE1_ID, null);
    List<TravelContainer> list = extractData(resp, TravelContainer.class);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_CONTAINER1_ID, list.get(0).getId());
  }

  @Test
  void admin_can_filter_travel_containers_by_name() throws Exception {
    TravelContainerApi api = new TravelContainerApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp =
        api.getTravelContainers(ADMIN_ID, COMPANY1_ID, JOB1_ID, 1, 100, null, "Sachet");
    List<TravelContainer> list = extractData(resp, TravelContainer.class);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_CONTAINER2_ID, list.get(0).getId());
  }

  @Test
  @DirtiesContext
  void employee_can_update_own_travel_container() throws Exception {
    TravelContainerApi api = new TravelContainerApi(anApiClient(EMPLOYEE_TOKEN));

    CrupdateTravelContainer toUpdate = new CrupdateTravelContainer();
    toUpdate.setId(TRAVEL_CONTAINER1_ID);
    toUpdate.setTravelId(TRAVEL_EXPENSE1_ID);
    toUpdate.setName("Red Box");
    toUpdate.setDescription("Updated description");
    toUpdate.setComment("Updated comment");

    List<TravelContainer> updated =
        api.crupdateTravelContainers(EMPLOYEE_ID, COMPANY1_ID, JOB1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(TRAVEL_CONTAINER1_ID, updated.get(0).getId());
    assertEquals("Red Box", updated.get(0).getName());
    assertEquals("Updated description", updated.get(0).getDescription());
    assertNotNull(updated.get(0).getCreatedAt());
    assertNotNull(updated.get(0).getUpdatedAt());
  }

  @Test
  void warehouse_worker_can_get_containers() throws Exception {
    TravelContainerApi api = new TravelContainerApi(anApiClient(WAREHOUSE_TOKEN));

    PaginatedResponse resp =
        api.getTravelContainers(WAREHOUSE_ID, COMPANY1_ID, JOB1_ID, 1, 100, null, null);
    List<TravelContainer> list = extractData(resp, TravelContainer.class);
    assertFalse(list.isEmpty());
  }

  @Test
  void administration_cannot_delete_travel_container() {
    TravelContainerApi api = new TravelContainerApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () -> api.deleteTravelContainerById(ADMIN_ID, COMPANY1_ID, JOB1_ID, TRAVEL_CONTAINER1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_travel_container() throws Exception {
    TravelContainerApi api = new TravelContainerApi(anApiClient(ADMIN_TOKEN));

    api.deleteTravelContainerById(ADMIN_ID, COMPANY1_ID, JOB1_ID, TRAVEL_CONTAINER2_ID);

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"TravelContainer with id "
            + TRAVEL_CONTAINER2_ID
            + " not found\"}",
        () -> api.getTravelContainerById(ADMIN_ID, COMPANY1_ID, JOB1_ID, TRAVEL_CONTAINER2_ID));
  }

  @Test
  void employee_cannot_create_travel_container_for_another_user() {
    TravelContainerApi api = new TravelContainerApi(anApiClient(EMPLOYEE_TOKEN));

    CrupdateTravelContainer toCreate = new CrupdateTravelContainer();
    toCreate.setId("new_container_id");
    toCreate.setTravelId(TRAVEL_EXPENSE1_ID);
    toCreate.setName("New Box");

    assertThrowsForbiddenException(
        () ->
            api.crupdateTravelContainers(ADMIN_ID, COMPANY1_ID, JOB1_ID, List.of(toCreate)));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
