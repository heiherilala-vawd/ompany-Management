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
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, TRAVEL_EXPENSE1_ID, TRAVEL_PEOPLE1_ID);

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
                COMPANY1_ID,
                JOB1_ID,
                EMPLOYEE_ID,
                EXPENSE1_ID,
                TRAVEL_EXPENSE1_ID,
                TRAVEL_PEOPLE1_ID));
  }

  @Test
  void admin_can_get_all_travel_people() throws Exception {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(ADMIN_TOKEN));

    List<TravelPeople> list =
        api.getTravelPeople(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, TRAVEL_EXPENSE1_ID, 1, 100, null);

    assertEquals(2, list.size());
    assertTrue(list.stream().anyMatch(tp -> TRAVEL_PEOPLE1_ID.equals(tp.getId())));
    assertTrue(list.stream().anyMatch(tp -> TRAVEL_PEOPLE2_ID.equals(tp.getId())));
  }

  @Test
  @DirtiesContext
  void employee_can_update_own_travel_people() throws Exception {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(EMPLOYEE_TOKEN));

    CrupdateTravelPeople toUpdate = travelPeopleToCrupdateTravelPeople(travelPeople2());
    toUpdate.setPersonName("Robert Dupont");

    List<TravelPeople> updated =
        api.crupdateTravelPeople(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, TRAVEL_EXPENSE1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(TRAVEL_PEOPLE2_ID, updated.get(0).getId());
    assertEquals("Robert Dupont", updated.get(0).getPersonName());
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
                EXPENSE1_ID,
                TRAVEL_EXPENSE1_ID,
                List.of(someCreatableTravelPeople())));
  }

  @Test
  void administration_cannot_delete_travel_people() {
    TravelPeopleApi api = new TravelPeopleApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.deleteTravelPeopleById(
                COMPANY1_ID,
                JOB1_ID,
                EMPLOYEE_ID,
                EXPENSE1_ID,
                TRAVEL_EXPENSE1_ID,
                TRAVEL_PEOPLE1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
