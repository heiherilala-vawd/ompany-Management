package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.CashTransactionApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.PaginatedResponse;
import com.example.demo.client.model.CashTransaction;
import com.example.demo.client.model.PaginatedResponse;
import com.example.demo.client.model.CrupdateCashTransaction;
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
@ContextConfiguration(initializers = CashTransactionIT.ContextInitializer.class)
class CashTransactionIT {
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
  void administration_can_get_cash_transaction_by_id() throws Exception {
    CashTransactionApi api = new CashTransactionApi(anApiClient(ADMINISTRATION_TOKEN));

    CashTransaction actual =
        api.getCashTransactionById(ADMIN_ID, COMPANY1_ID, CASH_ACCOUNT1_ID, CASH_TXN1_ID);
    CashTransaction expected = cashTransaction1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_cash_transaction_by_id() {
    CashTransactionApi api = new CashTransactionApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getCashTransactionById(ADMIN_ID, COMPANY1_ID, CASH_ACCOUNT1_ID, CASH_TXN1_ID));
  }

  @Test
  void administration_can_get_all_cash_transactions() throws Exception {
    CashTransactionApi api = new CashTransactionApi(anApiClient(ADMINISTRATION_TOKEN));

    PaginatedResponse resp = api.getCashTransactions(ADMIN_ID, COMPANY1_ID, CASH_ACCOUNT1_ID, 1, 100);


    List<CashTransaction> transactions = extractData(resp, CashTransaction.class);

    assertEquals(2, transactions.size());
    assertTrue(transactions.stream().anyMatch(ct -> CASH_TXN1_ID.equals(ct.getId())));
    assertTrue(transactions.stream().anyMatch(ct -> CASH_TXN2_ID.equals(ct.getId())));
  }

  @Test
  void employee_cannot_get_all_cash_transactions() {
    CashTransactionApi api = new CashTransactionApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () -> api.getCashTransactions(EMPLOYEE_ID, COMPANY1_ID, CASH_ACCOUNT1_ID, 1, 100));
  }

  @Test
  @DirtiesContext
  void admin_can_update_cash_transaction() throws Exception {
    CashTransactionApi api = new CashTransactionApi(anApiClient(ADMIN_TOKEN));

    CrupdateCashTransaction toUpdate = cashTransactionToCrupdateCashTransaction(cashTransaction1());
    toUpdate.setDescription("Achat matériel bureau - mis à jour");

    List<CashTransaction> updated =
        api.crupdateCashTransactions(ADMIN_ID, COMPANY1_ID, CASH_ACCOUNT1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(CASH_TXN1_ID, updated.get(0).getId());
    assertEquals("Achat matériel bureau - mis à jour", updated.get(0).getDescription());
  }

  @Test
  void employee_cannot_create_cash_transaction() {
    CashTransactionApi api = new CashTransactionApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateCashTransactions(
                EMPLOYEE_ID,
                COMPANY1_ID,
                CASH_ACCOUNT1_ID,
                List.of(someCreatableCashTransaction())));
  }

  @Test
  void administration_cannot_delete_cash_transaction() {
    CashTransactionApi api = new CashTransactionApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () -> api.deleteCashTransactionById(ADMIN_ID, COMPANY1_ID, CASH_ACCOUNT1_ID, CASH_TXN1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_cash_transaction() throws Exception {
    CashTransactionApi api = new CashTransactionApi(anApiClient(ADMIN_TOKEN));

    api.deleteCashTransactionById(ADMIN_ID, COMPANY1_ID, CASH_ACCOUNT1_ID, CASH_TXN1_ID);

    PaginatedResponse resp = api.getCashTransactions(ADMIN_ID, COMPANY1_ID, CASH_ACCOUNT1_ID, 1, 100);


    List<CashTransaction> transactions = extractData(resp, CashTransaction.class);
    assertEquals(1, transactions.size());
    assertEquals(CASH_TXN2_ID, transactions.get(0).getId());
  }

  @Test
  void admin_gets_not_found_when_cash_transaction_does_not_exist() {
    CashTransactionApi api = new CashTransactionApi(anApiClient(ADMIN_TOKEN));

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"CashTransaction with id nonexistent_txn not found\"}",
        () ->
            api.getCashTransactionById(ADMIN_ID, COMPANY1_ID, CASH_ACCOUNT1_ID, "nonexistent_txn"));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
