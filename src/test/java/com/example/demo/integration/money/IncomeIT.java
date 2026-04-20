package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.IncomeApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateIncomeMoney;
import com.example.demo.client.model.IncomeMoney;
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
@ContextConfiguration(initializers = IncomeIT.ContextInitializer.class)
class IncomeIT {
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
  void administration_can_get_income_by_id() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    IncomeApi api = new IncomeApi(administrationClient);

    IncomeMoney actual = api.getIncomeById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, INCOME1_ID);
    IncomeMoney expected = income1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_income_by_id() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    IncomeApi api = new IncomeApi(badClient);

    assertThrowsNotAuthorizedException(
        () -> api.getIncomeById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, INCOME1_ID));
  }

  @Test
  void admin_can_get_all_incomes() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    List<IncomeMoney> incomes = api.getIncomes(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100);

    assertEquals(2, incomes.size());
    assertTrue(incomes.stream().anyMatch(income -> INCOME1_ID.equals(income.getId())));
    assertTrue(incomes.stream().anyMatch(income -> INCOME2_ID.equals(income.getId())));
  }

  @Test
  void employee_cannot_get_all_incomes() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    IncomeApi api = new IncomeApi(employeeClient);

    assertThrowsForbiddenException(() -> api.getIncomes(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100));
  }

  @Test
  @DirtiesContext
  void administration_can_update_incomes() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    IncomeApi api = new IncomeApi(administrationClient);

    CrupdateIncomeMoney incomeToUpdate = incomeToCrupdateIncome(income1());
    incomeToUpdate.setDescription("Paiement initial chantier A valide");

    List<IncomeMoney> updatedIncomes =
        api.crupdateIncomes(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(incomeToUpdate));
    IncomeMoney updatedIncome = updatedIncomes.get(0);

    assertEquals(1, updatedIncomes.size());
    assertEquals(INCOME1_ID, updatedIncome.getId());
    assertEquals("Paiement initial chantier A valide", updatedIncome.getDescription());
    assertEquals(income1().getAmount(), updatedIncome.getAmount());
  }

  @Test
  void employee_cannot_create_incomes() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    IncomeApi api = new IncomeApi(employeeClient);

    assertThrowsForbiddenException(
        () ->
            api.crupdateIncomes(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(someCreatableIncome())));
  }

  @Test
  void administration_cannot_delete_income() {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    IncomeApi api = new IncomeApi(administrationClient);

    assertThrowsForbiddenException(
        () -> api.deleteIncomeById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, INCOME1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
