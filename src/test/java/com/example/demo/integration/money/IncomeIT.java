package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.IncomeApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.PaginatedResponse;
import com.example.demo.client.model.CrupdateIncomeMoney;
import com.example.demo.client.model.IncomeMoney;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestDataSqlLoader;
import com.example.demo.integration.conf.TestUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    IncomeMoney actual = api.getIncomeById(ADMIN_ID, COMPANY1_ID, JOB1_ID, INCOME1_ID);
    IncomeMoney expected = income1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());
    expected.getIncomeType().setCreatedAt(actual.getIncomeType().getCreatedAt());
    expected.getIncomeType().setUpdatedAt(actual.getIncomeType().getUpdatedAt());
    expected.getIncomeType().setCreatedBy(actual.getIncomeType().getCreatedBy());
    expected.getIncomeType().setUpdatedBy(actual.getIncomeType().getUpdatedBy());
    expected.getIncomeType().setComment(actual.getIncomeType().getComment());

    if (actual.getReceipts() != null && expected.getReceipts() != null) {
      for (int i = 0;
          i < Math.min(actual.getReceipts().size(), expected.getReceipts().size());
          i++) {
        var actualReceipt = actual.getReceipts().get(i);
        var expectedReceipt = expected.getReceipts().get(i);
        expectedReceipt.setCreatedAt(actualReceipt.getCreatedAt());
        expectedReceipt.setUpdatedAt(actualReceipt.getUpdatedAt());
        expectedReceipt.setCreatedBy(actualReceipt.getCreatedBy());
        expectedReceipt.setUpdatedBy(actualReceipt.getUpdatedBy());
        expectedReceipt.setComment(actualReceipt.getComment());
      }
    }

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_income_by_id() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    IncomeApi api = new IncomeApi(badClient);

    assertThrowsNotAuthorizedException(
        () -> api.getIncomeById(ADMIN_ID, COMPANY1_ID, JOB1_ID, INCOME1_ID));
  }

  @Test
  void admin_can_get_all_incomes() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    PaginatedResponse resp = api.getIncomes(ADMIN_ID, COMPANY1_ID, JOB1_ID, 1, 100, null, null, null, null, null, null);


    List<IncomeMoney> incomes = extractData(resp, IncomeMoney.class);

    assertEquals(7, incomes.size());
    assertTrue(incomes.stream().anyMatch(income -> INCOME1_ID.equals(income.getId())));
    assertTrue(incomes.stream().anyMatch(income -> INCOME2_ID.equals(income.getId())));
    assertTrue(incomes.stream().anyMatch(income -> INCOME3_ID.equals(income.getId())));
    assertTrue(incomes.stream().anyMatch(income -> INCOME4_ID.equals(income.getId())));
    assertTrue(incomes.stream().anyMatch(income -> INCOME5_ID.equals(income.getId())));
  }

  @Test
  void employee_cannot_get_all_incomes() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    IncomeApi api = new IncomeApi(employeeClient);

    assertThrowsForbiddenException(
        () ->
            api.getIncomes(
                ADMIN_ID, COMPANY1_ID, JOB1_ID, 1, 100, null, null, null, null, null, null));
  }

  @Test
  void admin_can_filter_incomes_by_organization() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    PaginatedResponse resp = api.getIncomes(
        ADMIN_ID, COMPANY1_ID, JOB1_ID, 1, 100, "org2_id", null, null, null, null, null);
    List<IncomeMoney> incomes = extractData(resp, IncomeMoney.class);

    assertEquals(7, incomes.size());
  }

  @Test
  void admin_can_filter_incomes_by_invoice_reference() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    PaginatedResponse resp = api.getIncomes(
        ADMIN_ID, COMPANY1_ID, JOB1_ID, 1, 100, null, "INV-2024-002", null, null, null, null);
    List<IncomeMoney> incomes = extractData(resp, IncomeMoney.class);

    assertEquals(1, incomes.size());
    assertEquals(INCOME2_ID, incomes.get(0).getId());
  }

  @Test
  void admin_can_filter_incomes_by_description() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    PaginatedResponse resp = api.getIncomes(
        ADMIN_ID, COMPANY1_ID, JOB1_ID, 1, 100, null, null, "chantier A", null, null, null);
    List<IncomeMoney> incomes = extractData(resp, IncomeMoney.class);

    assertEquals(1, incomes.size());
    assertEquals(INCOME1_ID, incomes.get(0).getId());
  }

  @Test
  void admin_can_filter_incomes_by_amount() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    PaginatedResponse resp = api.getIncomes(
        ADMIN_ID,
        COMPANY1_ID,
        JOB1_ID,
        1,
        100,
        null,
        null,
        null,
        BigDecimal.valueOf(275000),
        null,
        null);
    List<IncomeMoney> incomes = extractData(resp, IncomeMoney.class);

    assertEquals(1, incomes.size());
    assertEquals(INCOME2_ID, incomes.get(0).getId());
  }

  @Test
  void admin_can_filter_incomes_by_income_type() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    PaginatedResponse resp = api.getIncomes(
        ADMIN_ID, COMPANY1_ID, JOB1_ID, 1, 100, null, null, null, null, INCOME_TYPE2_ID, null);
    List<IncomeMoney> incomes = extractData(resp, IncomeMoney.class);

    assertEquals(1, incomes.size());
    assertEquals(INCOME3_ID, incomes.get(0).getId());
  }

  @Test
  void admin_can_filter_incomes_not_received() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    PaginatedResponse resp = api.getIncomes(ADMIN_ID, COMPANY1_ID, JOB1_ID, 1, 100, null, null, null, null, null, false);


    List<IncomeMoney> incomes = extractData(resp, IncomeMoney.class);

    assertEquals(2, incomes.size());
    assertTrue(incomes.stream().anyMatch(i -> INCOME3_ID.equals(i.getId())));
    assertTrue(incomes.stream().anyMatch(i -> INCOME5_ID.equals(i.getId())));
  }

  @Test
  void admin_can_filter_incomes_received() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    PaginatedResponse resp = api.getIncomes(ADMIN_ID, COMPANY1_ID, JOB1_ID, 1, 100, null, null, null, null, null, true);


    List<IncomeMoney> incomes = extractData(resp, IncomeMoney.class);

    assertEquals(5, incomes.size());
    assertTrue(incomes.stream().anyMatch(i -> INCOME1_ID.equals(i.getId())));
    assertTrue(incomes.stream().anyMatch(i -> INCOME2_ID.equals(i.getId())));
    assertTrue(incomes.stream().anyMatch(i -> INCOME4_ID.equals(i.getId())));
    assertTrue(incomes.stream().anyMatch(i -> INCOME6_ID.equals(i.getId())));
    assertTrue(incomes.stream().anyMatch(i -> INCOME7_ID.equals(i.getId())));
  }

  @Test
  @DirtiesContext
  void administration_can_update_incomes() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    IncomeApi api = new IncomeApi(administrationClient);

    CrupdateIncomeMoney incomeToUpdate = incomeToCrupdateIncome(income1());
    incomeToUpdate.setDescription("Paiement initial chantier A valide");

    List<IncomeMoney> updatedIncomes =
        api.crupdateIncomes(ADMIN_ID, COMPANY1_ID, JOB1_ID, List.of(incomeToUpdate));
    IncomeMoney updatedIncome = updatedIncomes.get(0);

    assertEquals(1, updatedIncomes.size());
    assertEquals(INCOME1_ID, updatedIncome.getId());
    assertEquals("Paiement initial chantier A valide", updatedIncome.getDescription());
    assertEquals(income1().getAmount(), updatedIncome.getAmount());
    assertEquals(income1().getDueDate(), updatedIncome.getDueDate());
    assertEquals(income1().getPaymentTerms(), updatedIncome.getPaymentTerms());
  }

  @Test
  @DirtiesContext
  void administration_can_create_income_with_due_date() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    CrupdateIncomeMoney income = someCreatableIncome();
    income.setDueDate(LocalDate.of(2024, 6, 30));
    income.setPaymentTerms("NET-60");

    List<IncomeMoney> created =
        api.crupdateIncomes(ADMIN_ID, COMPANY1_ID, JOB1_ID, List.of(income));

    assertEquals(1, created.size());
    assertEquals(LocalDate.of(2024, 6, 30), created.get(0).getDueDate());
    assertEquals("NET-60", created.get(0).getPaymentTerms());
  }

  @Test
  void admin_cannot_create_income_with_due_date_before_facturation_date() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    CrupdateIncomeMoney income = someCreatableIncome();
    income.facturationDate(java.time.Instant.parse("2024-06-15T10:00:00Z"));
    income.setDueDate(LocalDate.of(2024, 6, 1));

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Due date cannot be before facturation date\"}",
        () -> api.crupdateIncomes(ADMIN_ID, COMPANY1_ID, JOB1_ID, List.of(income)));
  }

  @Test
  void employee_cannot_create_incomes() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    IncomeApi api = new IncomeApi(employeeClient);

    assertThrowsForbiddenException(
        () -> api.crupdateIncomes(ADMIN_ID, COMPANY1_ID, JOB1_ID, List.of(someCreatableIncome())));
  }

  @Test
  void administration_cannot_delete_income() {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    IncomeApi api = new IncomeApi(administrationClient);

    assertThrowsForbiddenException(
        () -> api.deleteIncomeById(ADMIN_ID, COMPANY1_ID, JOB1_ID, INCOME1_ID));
  }

  @Test
  void admin_cannot_create_income_with_negative_amount() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    CrupdateIncomeMoney invalidIncome = someCreatableIncome();
    invalidIncome.setAmount(BigDecimal.valueOf(-5000));

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Amount must be non-negative\"}",
        () -> api.crupdateIncomes(ADMIN_ID, COMPANY1_ID, JOB1_ID, List.of(invalidIncome)));
  }

  @Test
  void admin_cannot_create_income_without_organization() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    CrupdateIncomeMoney invalidIncome = someCreatableIncome();
    invalidIncome.setOrganizationId(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Organization is mandatory for income\"}",
        () -> api.crupdateIncomes(ADMIN_ID, COMPANY1_ID, JOB1_ID, List.of(invalidIncome)));
  }

  @Test
  void admin_can_filter_incomes_partially_received() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    PaginatedResponse resp = api.getIncomes(ADMIN_ID, COMPANY1_ID, JOB1_ID, 1, 100, null, null, null, null, null, false);


    List<IncomeMoney> incomes = extractData(resp, IncomeMoney.class);

    assertTrue(incomes.stream().anyMatch(i -> INCOME5_ID.equals(i.getId())));
    IncomeMoney income =
        incomes.stream().filter(i -> INCOME5_ID.equals(i.getId())).findFirst().get();
    assertEquals(0, BigDecimal.valueOf(100000).compareTo(income.getAmount()));
    assertEquals(0, BigDecimal.valueOf(40000).compareTo(income.getRemainingAmount()));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_income() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    api.deleteIncomeById(ADMIN_ID, COMPANY1_ID, JOB1_ID, INCOME3_ID);

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"Income with id " + INCOME3_ID + " not found\"}",
        () -> api.getIncomeById(ADMIN_ID, COMPANY1_ID, JOB1_ID, INCOME3_ID));
  }

  @Test
  void admin_can_get_income_with_multiple_receipts() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeApi api = new IncomeApi(adminClient);

    IncomeMoney actual = api.getIncomeById(ADMIN_ID, COMPANY1_ID, JOB1_ID, INCOME7_ID);
    IncomeMoney expected = income7();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());
    expected.getIncomeType().setCreatedAt(actual.getIncomeType().getCreatedAt());
    expected.getIncomeType().setUpdatedAt(actual.getIncomeType().getUpdatedAt());
    expected.getIncomeType().setCreatedBy(actual.getIncomeType().getCreatedBy());
    expected.getIncomeType().setUpdatedBy(actual.getIncomeType().getUpdatedBy());
    expected.getIncomeType().setComment(actual.getIncomeType().getComment());
    syncReceiptsTimestamps(actual, expected);

    assertEquals(expected, actual);
  }

  private void syncReceiptsTimestamps(IncomeMoney actual, IncomeMoney expected) {
    if (actual.getReceipts() != null && expected.getReceipts() != null) {
      for (int i = 0;
          i < Math.min(actual.getReceipts().size(), expected.getReceipts().size());
          i++) {
        var actualReceipt = actual.getReceipts().get(i);
        var expectedReceipt = expected.getReceipts().get(i);
        expectedReceipt.setCreatedAt(actualReceipt.getCreatedAt());
        expectedReceipt.setUpdatedAt(actualReceipt.getUpdatedAt());
        expectedReceipt.setCreatedBy(actualReceipt.getCreatedBy());
        expectedReceipt.setUpdatedBy(actualReceipt.getUpdatedBy());
        expectedReceipt.setComment(actualReceipt.getComment());
      }
    }
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
