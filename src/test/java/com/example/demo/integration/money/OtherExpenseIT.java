package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.OtherExpenseApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateOtherExpense;
import com.example.demo.client.model.OtherExpense;
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
@ContextConfiguration(initializers = OtherExpenseIT.ContextInitializer.class)
class OtherExpenseIT {
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
  void employee_can_get_own_other_expense_by_id() throws Exception {
    OtherExpenseApi api = new OtherExpenseApi(anApiClient(EMPLOYEE_TOKEN));

    OtherExpense actual =
        api.getOtherExpenseById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, OTHER_EXPENSE1_ID);

    assertEquals(otherExpense1(), actual);
  }

  @Test
  void user_with_bad_token_cannot_get_other_expense_by_id() {
    OtherExpenseApi api = new OtherExpenseApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () ->
            api.getOtherExpenseById(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, OTHER_EXPENSE1_ID));
  }

  @Test
  void admin_can_get_all_other_expenses() throws Exception {
    OtherExpenseApi api = new OtherExpenseApi(anApiClient(ADMIN_TOKEN));

    List<OtherExpense> otherExpenses =
        api.getOtherExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, 1, 100, null, null);

    assertEquals(2, otherExpenses.size());
    assertTrue(
        otherExpenses.stream()
            .anyMatch(otherExpense -> OTHER_EXPENSE1_ID.equals(otherExpense.getId())));
    assertTrue(
        otherExpenses.stream()
            .anyMatch(otherExpense -> OTHER_EXPENSE2_ID.equals(otherExpense.getId())));
  }

  @Test
  void admin_can_filter_other_expenses_by_expense_id() throws Exception {
    OtherExpenseApi api = new OtherExpenseApi(anApiClient(ADMIN_TOKEN));

    List<OtherExpense> otherExpenses =
        api.getOtherExpenses(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, 1, 100, EXPENSE2_ID, null);

    assertEquals(1, otherExpenses.size());
    assertEquals(OTHER_EXPENSE2_ID, otherExpenses.get(0).getId());
  }

  @Test
  void admin_can_filter_other_expenses_by_description() throws Exception {
    OtherExpenseApi api = new OtherExpenseApi(anApiClient(ADMIN_TOKEN));

    List<OtherExpense> otherExpenses =
        api.getOtherExpenses(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, 1, 100, null, "administratifs");

    assertEquals(1, otherExpenses.size());
    assertEquals(OTHER_EXPENSE1_ID, otherExpenses.get(0).getId());
  }

  @Test
  @DirtiesContext
  void employee_can_update_own_other_expenses() throws Exception {
    OtherExpenseApi api = new OtherExpenseApi(anApiClient(EMPLOYEE_TOKEN));

    CrupdateOtherExpense otherExpenseToUpdate = otherExpenseToCrupdateOtherExpense(otherExpense1());
    otherExpenseToUpdate.setDescription("Frais administratifs chantier A ajustes");

    List<OtherExpense> updatedOtherExpenses =
        api.crupdateOtherExpenses(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, List.of(otherExpenseToUpdate));

    assertEquals(1, updatedOtherExpenses.size());
    assertEquals(OTHER_EXPENSE1_ID, updatedOtherExpenses.get(0).getId());
    assertEquals(
        "Frais administratifs chantier A ajustes", updatedOtherExpenses.get(0).getDescription());
  }

  @Test
  void warehouse_worker_cannot_create_other_expenses() {
    OtherExpenseApi api = new OtherExpenseApi(anApiClient(WAREHOUSE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateOtherExpenses(
                COMPANY1_ID,
                JOB1_ID,
                EMPLOYEE_ID,
                EXPENSE1_ID,
                List.of(someCreatableOtherExpense())));
  }

  @Test
  void employee_cannot_delete_other_expense() {
    OtherExpenseApi api = new OtherExpenseApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.deleteOtherExpenseById(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, OTHER_EXPENSE1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
