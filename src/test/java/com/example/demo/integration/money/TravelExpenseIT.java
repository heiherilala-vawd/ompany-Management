package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.TravelExpenseApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateTravelExpense;
import com.example.demo.client.model.TravelExpense;
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
@ContextConfiguration(initializers = TravelExpenseIT.ContextInitializer.class)
class TravelExpenseIT {
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
  void employee_can_get_own_travel_expense_by_id() throws Exception {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(EMPLOYEE_TOKEN));

    TravelExpense actual =
        api.getTravelExpenseById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, TRAVEL_EXPENSE1_ID);

    assertEquals(travelExpense1(), actual);
  }

  @Test
  void user_with_bad_token_cannot_get_travel_expense_by_id() {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getTravelExpenseById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, TRAVEL_EXPENSE1_ID));
  }

  @Test
  void admin_can_get_all_travel_expenses() throws Exception {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(ADMIN_TOKEN));

    List<TravelExpense> travelExpenses =
        api.getTravelExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null);

    assertEquals(2, travelExpenses.size());
    assertTrue(
        travelExpenses.stream()
            .anyMatch(travelExpense -> TRAVEL_EXPENSE1_ID.equals(travelExpense.getId())));
    assertTrue(
        travelExpenses.stream()
            .anyMatch(travelExpense -> TRAVEL_EXPENSE2_ID.equals(travelExpense.getId())));
  }

  @Test
  void admin_can_filter_travel_expenses_by_departure_location() throws Exception {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(ADMIN_TOKEN));

    List<TravelExpense> travelExpenses =
        api.getTravelExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, WAREHOUSE1_ID, null);

    assertEquals(1, travelExpenses.size());
    assertEquals(TRAVEL_EXPENSE1_ID, travelExpenses.get(0).getId());
  }

  @Test
  void admin_can_filter_travel_expenses_by_arrival_location() throws Exception {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(ADMIN_TOKEN));

    List<TravelExpense> travelExpenses =
        api.getTravelExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, WAREHOUSE1_ID);

    assertEquals(1, travelExpenses.size());
    assertEquals(TRAVEL_EXPENSE2_ID, travelExpenses.get(0).getId());
  }

  @Test
  @DirtiesContext
  void employee_can_update_own_travel_expenses() throws Exception {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(EMPLOYEE_TOKEN));

    CrupdateTravelExpense travelExpenseToUpdate =
        travelExpenseToCrupdateTravelExpense(travelExpense1());
    travelExpenseToUpdate.setArrivalLocation(warehouseToCrupdateWarehouse(warehouse2()));

    List<TravelExpense> updatedTravelExpenses =
        api.crupdateTravelExpenses(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(travelExpenseToUpdate));

    assertEquals(1, updatedTravelExpenses.size());
    assertEquals(TRAVEL_EXPENSE1_ID, updatedTravelExpenses.get(0).getId());
    assertEquals(WAREHOUSE2_ID, updatedTravelExpenses.get(0).getArrivalLocation().getId());
  }

  @Test
  void warehouse_worker_cannot_create_travel_expenses() {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(WAREHOUSE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateTravelExpenses(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(someCreatableTravelExpense())));
  }

  @Test
  void admin_cannot_create_travel_expense_without_departure_location() {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(ADMIN_TOKEN));

    CrupdateTravelExpense invalidTravel = someCreatableTravelExpense();
    invalidTravel.setDepartureLocation(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Departure location is mandatory\"}",
        () ->
            api.crupdateTravelExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidTravel)));
  }

  @Test
  void admin_cannot_create_travel_expense_without_arrival_location() {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(ADMIN_TOKEN));

    CrupdateTravelExpense invalidTravel = someCreatableTravelExpense();
    invalidTravel.setArrivalLocation(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Arrival location is mandatory\"}",
        () ->
            api.crupdateTravelExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidTravel)));
  }

  @Test
  void admin_cannot_create_travel_expense_with_departure_after_arrival() {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(ADMIN_TOKEN));

    CrupdateTravelExpense invalidTravel = someCreatableTravelExpense();
    invalidTravel.setDepartureDate(java.time.Instant.now().plus(java.time.Duration.ofDays(2)));
    invalidTravel.setArrivalDate(java.time.Instant.now().plus(java.time.Duration.ofDays(1)));

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Departure date cannot be after arrival date\"}",
        () ->
            api.crupdateTravelExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidTravel)));
  }

  @Test
  void administration_cannot_delete_travel_expense() {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () -> api.deleteTravelExpenseById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, TRAVEL_EXPENSE1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
