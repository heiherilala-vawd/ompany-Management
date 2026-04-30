package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.ExpenseApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateExpenseMoney;
import com.example.demo.client.model.ExpenseMoney;
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
@ContextConfiguration(initializers = ExpenseIT.ContextInitializer.class)
class ExpenseIT {
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
  void administration_can_get_expense_by_id() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    ExpenseApi api = new ExpenseApi(administrationClient);

    ExpenseMoney actual = api.getExpenseById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID);
    ExpenseMoney expected = expense1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_expense_by_id() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    ExpenseApi api = new ExpenseApi(badClient);

    assertThrowsNotAuthorizedException(
        () -> api.getExpenseById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID));
  }

  @Test
  void admin_can_get_all_expenses() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    ExpenseApi api = new ExpenseApi(adminClient);

    List<ExpenseMoney> expenses =
        api.getExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null);

    assertEquals(2, expenses.size());
    assertTrue(expenses.stream().anyMatch(expense -> EXPENSE1_ID.equals(expense.getId())));
    assertTrue(expenses.stream().anyMatch(expense -> EXPENSE2_ID.equals(expense.getId())));
  }

  @Test
  void employee_cannot_get_all_expenses() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    ExpenseApi api = new ExpenseApi(employeeClient);

    assertThrowsForbiddenException(
        () -> api.getExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null));
  }

  @Test
  void admin_can_filter_expenses_by_description() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    ExpenseApi api = new ExpenseApi(adminClient);

    List<ExpenseMoney> expenses =
        api.getExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, "sous-traitant", null);

    assertEquals(1, expenses.size());
    assertEquals(EXPENSE2_ID, expenses.get(0).getId());
  }

  @Test
  void admin_can_filter_expenses_by_amount() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    ExpenseApi api = new ExpenseApi(adminClient);

    List<ExpenseMoney> expenses =
        api.getExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, 45000);

    assertEquals(1, expenses.size());
    assertEquals(EXPENSE1_ID, expenses.get(0).getId());
  }

  @Test
  @DirtiesContext
  void administration_can_update_expenses() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    ExpenseApi api = new ExpenseApi(administrationClient);

    CrupdateExpenseMoney expenseToUpdate = expenseToCrupdateExpense(expense1());
    expenseToUpdate.setDescription("Achat materiaux chantier A ajuste");

    List<ExpenseMoney> updatedExpenses =
        api.crupdateExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(expenseToUpdate));
    ExpenseMoney updatedExpense = updatedExpenses.get(0);

    assertEquals(1, updatedExpenses.size());
    assertEquals(EXPENSE1_ID, updatedExpense.getId());
    assertEquals("Achat materiaux chantier A ajuste", updatedExpense.getDescription());
    assertEquals(expense1().getAmount(), updatedExpense.getAmount());
  }

  @Test
  void employee_cannot_create_expenses() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    ExpenseApi api = new ExpenseApi(employeeClient);

    assertThrowsForbiddenException(
        () ->
            api.crupdateExpenses(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(someCreatableExpense())));
  }

  @Test
  void administration_cannot_delete_expense() {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    ExpenseApi api = new ExpenseApi(administrationClient);

    assertThrowsForbiddenException(
        () -> api.deleteExpenseById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID));
  }

  @Test
  void admin_cannot_create_expense_with_negative_amount() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    ExpenseApi api = new ExpenseApi(adminClient);

    CrupdateExpenseMoney invalidExpense = someCreatableExpense();
    invalidExpense.setAmount(-1000);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Amount must be positive\"}",
        () -> api.crupdateExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidExpense)));
  }

  @Test
  void admin_can_create_expense_with_job_from_path() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    ExpenseApi api = new ExpenseApi(adminClient);

    CrupdateExpenseMoney expense = someCreatableExpense();
    expense.setJobId(null); // Controller should set job from path

    List<ExpenseMoney> result =
        api.crupdateExpenses(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(expense));
    assertNotNull(result);
    assertEquals(1, result.size());
    assertNotNull(result.get(0).getJob());
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
