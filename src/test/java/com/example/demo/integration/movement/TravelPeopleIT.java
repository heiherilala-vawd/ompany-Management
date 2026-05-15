package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.TravelPeopleApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateTravelPeople;
import com.example.demo.client.model.TravelPeople;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestDataSqlLoader;
import com.example.demo.integration.conf.TestUtils;
import java.time.Instant;
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
@ContextConfiguration(initializers = TravelPeopleIT.ContextInitializer.class)
class TravelPeopleIT {
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
  void employee_can_get_own_travel_people_by_id() throws Exception {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(EMPLOYEE_TOKEN));

    TravelPeople actual =
        api.getTravelPeopleById(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, TRAVEL_EXPENSE1_ID, TRAVEL_PEOPLE1_ID);

    TravelPeople expected = travelPeople1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_travel_people_by_id() {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () ->
            api.getTravelPeopleById(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, TRAVEL_EXPENSE1_ID, TRAVEL_PEOPLE1_ID));
  }

  @Test
  void admin_can_get_all_travel_people() throws Exception {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(ADMIN_TOKEN));

    List<TravelPeople> list =
        api.getTravelPeople(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            TRAVEL_EXPENSE1_ID,
            1,
            100,
            null,
            null,
            null,
            null,
            null,
            null);

    assertEquals(2, list.size());
    assertTrue(list.stream().anyMatch(tp -> TRAVEL_PEOPLE1_ID.equals(tp.getId())));
    assertTrue(list.stream().anyMatch(tp -> TRAVEL_PEOPLE2_ID.equals(tp.getId())));
  }

  @Test
  void admin_can_filter_travel_people_by_travel_id() throws Exception {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(ADMIN_TOKEN));

    List<TravelPeople> list =
        api.getTravelPeople(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            TRAVEL_EXPENSE1_ID,
            1,
            100,
            TRAVEL_EXPENSE1_ID,
            null,
            null,
            null,
            null,
            null);

    assertEquals(2, list.size());
    assertTrue(
        list.stream()
            .allMatch(
                tp -> tp.getTravel() != null && TRAVEL_EXPENSE1_ID.equals(tp.getTravel().getId())));
  }

  @Test
  void admin_can_filter_travel_people_by_person_name() throws Exception {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(ADMIN_TOKEN));

    List<TravelPeople> list =
        api.getTravelPeople(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            TRAVEL_EXPENSE1_ID,
            1,
            100,
            null,
            EMPLOYEE_ID,
            null,
            null,
            null,
            null);

    assertEquals(2, list.size());
  }

  @Test
  void admin_can_filter_travel_people_by_arrival_location() throws Exception {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(ADMIN_TOKEN));

    List<TravelPeople> list =
        api.getTravelPeople(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            TRAVEL_EXPENSE1_ID,
            1,
            100,
            null,
            null,
            WAREHOUSE1_ID,
            null,
            null,
            null);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_PEOPLE1_ID, list.get(0).getId());
  }

  @Test
  void admin_can_filter_travel_people_by_arrival_date_interval() throws Exception {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(ADMIN_TOKEN));

    List<TravelPeople> list =
        api.getTravelPeople(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            TRAVEL_EXPENSE1_ID,
            1,
            100,
            null,
            null,
            null,
            Instant.parse("2024-03-01T00:00:00Z"),
            Instant.parse("2024-03-01T23:59:59Z"),
            null);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_PEOPLE1_ID, list.get(0).getId());
  }

  @Test
  void admin_can_filter_travel_people_not_arrived() throws Exception {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(ADMIN_TOKEN));

    // Update a person to have no arrival info
    CrupdateTravelPeople toUpdate = travelPeopleToCrupdateTravelPeople(travelPeople2());
    toUpdate.setArrivalLocation(null);
    toUpdate.setArrivalDate(null);

    api.crupdateTravelPeople(
        COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, TRAVEL_EXPENSE1_ID, List.of(toUpdate));

    List<TravelPeople> list =
        api.getTravelPeople(
            COMPANY1_ID,
            JOB1_ID,
            EMPLOYEE_ID,
            TRAVEL_EXPENSE1_ID,
            1,
            100,
            null,
            null,
            null,
            null,
            null,
            true);

    assertEquals(1, list.size());
    assertEquals(TRAVEL_PEOPLE2_ID, list.get(0).getId());
  }

  @Test
  @DirtiesContext
  void employee_can_update_own_travel_people() throws Exception {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(EMPLOYEE_TOKEN));

    CrupdateTravelPeople toUpdate = travelPeopleToCrupdateTravelPeople(travelPeople2());
    toUpdate.setUserId(EMPLOYEE_ID);
    toUpdate.setArrivalLocation(TestUtils.WAREHOUSE2_ID);
    toUpdate.setArrivalDate(java.time.Instant.parse("2024-03-05T15:00:00Z"));

    List<TravelPeople> updated =
        api.crupdateTravelPeople(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, TRAVEL_EXPENSE1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(TRAVEL_PEOPLE2_ID, updated.get(0).getId());
    assertEquals(EMPLOYEE_ID, updated.get(0).getUser().getId());
    assertEquals(WAREHOUSE2_ID, updated.get(0).getArrivalLocation().getId());
    assertEquals(Instant.parse("2024-03-05T15:00:00Z"), updated.get(0).getArrivalDate());
  }

  @Test
  void warehouse_worker_cannot_crupdate_travel_people() {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(WAREHOUSE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateTravelPeople(
                COMPANY1_ID,
                JOB1_ID,
                EMPLOYEE_ID,
                TRAVEL_EXPENSE1_ID,
                List.of(someCreatableTravelPeople())));
  }

  @Test
  void administration_cannot_delete_travel_people() {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.deleteTravelPeopleById(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, TRAVEL_EXPENSE1_ID, TRAVEL_PEOPLE1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_travel_people() throws Exception {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(ADMIN_TOKEN));

    api.deleteTravelPeopleById(
        COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, TRAVEL_EXPENSE1_ID, TRAVEL_PEOPLE2_ID);

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"Travel with id "
            + TRAVEL_PEOPLE2_ID
            + " not found\"}",
        () ->
            api.getTravelPeopleById(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, TRAVEL_EXPENSE1_ID, TRAVEL_PEOPLE2_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
