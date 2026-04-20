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
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("db/testdata/V99_1__testdata.sql"));
    }
  }

  @Test
  void employee_can_get_own_travel_expense_by_id() throws Exception {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(EMPLOYEE_TOKEN));

    TravelExpense actual =
        api.getTravelExpenseById(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, TRAVEL_EXPENSE1_ID);

    assertEquals(travelExpense1(), actual);
  }

  @Test
  void user_with_bad_token_cannot_get_travel_expense_by_id() {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () ->
            api.getTravelExpenseById(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, TRAVEL_EXPENSE1_ID));
  }

  @Test
  void admin_can_get_all_travel_expenses() throws Exception {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(ADMIN_TOKEN));

    List<TravelExpense> travelExpenses =
        api.getTravelExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, 1, 100);

    assertEquals(2, travelExpenses.size());
    assertTrue(
        travelExpenses.stream()
            .anyMatch(travelExpense -> TRAVEL_EXPENSE1_ID.equals(travelExpense.getId())));
    assertTrue(
        travelExpenses.stream()
            .anyMatch(travelExpense -> TRAVEL_EXPENSE2_ID.equals(travelExpense.getId())));
  }

  @Test
  @DirtiesContext
  void employee_can_update_own_travel_expenses() throws Exception {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(EMPLOYEE_TOKEN));

    CrupdateTravelExpense travelExpenseToUpdate =
        travelExpenseToCrupdateTravelExpense(travelExpense1());
    travelExpenseToUpdate.setArrivalLocation("Mahajanga");

    List<TravelExpense> updatedTravelExpenses =
        api.crupdateTravelExpenses(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, List.of(travelExpenseToUpdate));

    assertEquals(1, updatedTravelExpenses.size());
    assertEquals(TRAVEL_EXPENSE1_ID, updatedTravelExpenses.get(0).getId());
    assertEquals("Mahajanga", updatedTravelExpenses.get(0).getArrivalLocation());
  }

  @Test
  void warehouse_worker_cannot_create_travel_expenses() {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(WAREHOUSE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateTravelExpenses(
                COMPANY1_ID,
                JOB1_ID,
                EMPLOYEE_ID,
                EXPENSE1_ID,
                List.of(someCreatableTravelExpense())));
  }

  @Test
  void administration_cannot_delete_travel_expense() {
    TravelExpenseApi api = new TravelExpenseApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.deleteTravelExpenseById(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, TRAVEL_EXPENSE1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
