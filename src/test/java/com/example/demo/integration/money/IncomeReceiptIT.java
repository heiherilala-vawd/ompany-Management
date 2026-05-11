package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.IncomeReceiptApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateIncomeReceipt;
import com.example.demo.client.model.IncomeReceipt;
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
@ContextConfiguration(initializers = IncomeReceiptIT.ContextInitializer.class)
class IncomeReceiptIT {
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
  void administration_can_get_receipt_by_id() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    IncomeReceiptApi api = new IncomeReceiptApi(administrationClient);

    IncomeReceipt actual =
        api.getIncomeReceiptById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, INCOME1_ID, RECEIPT1_ID);
    IncomeReceipt expected = receipt1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());
    if (actual.getIncome() != null) {
      expected.setIncome(actual.getIncome());
    }

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_receipt_by_id() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    IncomeReceiptApi api = new IncomeReceiptApi(badClient);

    assertThrowsNotAuthorizedException(
        () -> api.getIncomeReceiptById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, INCOME1_ID, RECEIPT1_ID));
  }

  @Test
  void administration_can_get_all_receipts_for_income() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    IncomeReceiptApi api = new IncomeReceiptApi(administrationClient);

    List<IncomeReceipt> receipts =
        api.getIncomeReceipts(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, INCOME1_ID, 1, 100);

    assertEquals(1, receipts.size());
    assertEquals(RECEIPT1_ID, receipts.get(0).getId());
  }

  @Test
  void employee_cannot_get_receipts() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    IncomeReceiptApi api = new IncomeReceiptApi(employeeClient);

    assertThrowsForbiddenException(
        () -> api.getIncomeReceipts(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, INCOME1_ID, 1, 100));
  }

  @Test
  @DirtiesContext
  void administration_can_create_receipt() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    IncomeReceiptApi api = new IncomeReceiptApi(administrationClient);

    CrupdateIncomeReceipt newReceipt = someCreatableReceipt();

    List<IncomeReceipt> created =
        api.crupdateIncomeReceipts(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, INCOME1_ID, List.of(newReceipt));

    assertEquals(1, created.size());
    IncomeReceipt saved = created.get(0);
    assertEquals(newReceipt.getId(), saved.getId());
    assertEquals(newReceipt.getPaymentDate(), saved.getPaymentDate());
    assertEquals(newReceipt.getAmount(), saved.getAmount());
  }

  @Test
  @DirtiesContext
  void administration_can_update_receipt() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    IncomeReceiptApi api = new IncomeReceiptApi(administrationClient);

    CrupdateIncomeReceipt receiptToUpdate = receiptToCrupdateReceipt(receipt1());
    receiptToUpdate.setAmount(200000);

    List<IncomeReceipt> updated =
        api.crupdateIncomeReceipts(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, INCOME1_ID, List.of(receiptToUpdate));

    assertEquals(1, updated.size());
    IncomeReceipt saved = updated.get(0);
    assertEquals(RECEIPT1_ID, saved.getId());
    assertEquals(Integer.valueOf(200000), saved.getAmount());
  }

  @Test
  void employee_cannot_create_receipts() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    IncomeReceiptApi api = new IncomeReceiptApi(employeeClient);

    assertThrowsForbiddenException(
        () ->
            api.crupdateIncomeReceipts(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, INCOME1_ID, List.of(someCreatableReceipt())));
  }

  @Test
  void administration_cannot_delete_receipt() {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    IncomeReceiptApi api = new IncomeReceiptApi(administrationClient);

    assertThrowsForbiddenException(
        () ->
            api.deleteIncomeReceiptById(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, INCOME1_ID, RECEIPT1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_receipt() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    IncomeReceiptApi api = new IncomeReceiptApi(adminClient);

    api.deleteIncomeReceiptById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, INCOME1_ID, RECEIPT1_ID);

    List<IncomeReceipt> receipts =
        api.getIncomeReceipts(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, INCOME1_ID, 1, 100);

    assertEquals(0, receipts.size());
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
