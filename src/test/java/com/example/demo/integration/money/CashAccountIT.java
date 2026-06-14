package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.CashAccountApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CashAccount;
import com.example.demo.client.model.CrupdateCashAccount;
import com.example.demo.client.model.PaginatedResponse;
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
@ContextConfiguration(initializers = CashAccountIT.ContextInitializer.class)
class CashAccountIT {
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
  void administration_can_get_cash_account_by_id() throws Exception {
    CashAccountApi api = new CashAccountApi(anApiClient(ADMINISTRATION_TOKEN));

    CashAccount actual = api.getCashAccountById(ADMIN_ID, COMPANY1_ID, CASH_ACCOUNT1_ID);
    CashAccount expected = cashAccount1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_cash_account_by_id() {
    CashAccountApi api = new CashAccountApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getCashAccountById(ADMIN_ID, COMPANY1_ID, CASH_ACCOUNT1_ID));
  }

  @Test
  void administration_can_get_all_cash_accounts() throws Exception {
    CashAccountApi api = new CashAccountApi(anApiClient(ADMINISTRATION_TOKEN));

    PaginatedResponse resp = api.getCashAccounts(ADMIN_ID, COMPANY1_ID, 1, 100);

    List<CashAccount> accounts = extractData(resp, CashAccount.class);

    assertEquals(2, accounts.size());
    assertTrue(accounts.stream().anyMatch(ca -> CASH_ACCOUNT1_ID.equals(ca.getId())));
    assertTrue(accounts.stream().anyMatch(ca -> CASH_ACCOUNT2_ID.equals(ca.getId())));
  }

  @Test
  void employee_cannot_get_all_cash_accounts() {
    CashAccountApi api = new CashAccountApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(() -> api.getCashAccounts(EMPLOYEE_ID, COMPANY1_ID, 1, 100));
  }

  @Test
  @DirtiesContext
  void admin_can_update_cash_account() throws Exception {
    CashAccountApi api = new CashAccountApi(anApiClient(ADMIN_TOKEN));

    CrupdateCashAccount toUpdate = cashAccountToCrupdateCashAccount(cashAccount1());
    toUpdate.setName("Compte bancaire principal - mis à jour");

    List<CashAccount> updated = api.crupdateCashAccounts(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(CASH_ACCOUNT1_ID, updated.get(0).getId());
    assertEquals("Compte bancaire principal - mis à jour", updated.get(0).getName());
  }

  @Test
  void employee_cannot_create_cash_account() {
    CashAccountApi api = new CashAccountApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateCashAccounts(
                EMPLOYEE_ID, COMPANY1_ID, List.of(someCreatableCashAccount())));
  }

  @Test
  void administration_cannot_delete_cash_account() {
    CashAccountApi api = new CashAccountApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () -> api.deleteCashAccountById(ADMIN_ID, COMPANY1_ID, CASH_ACCOUNT1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_cash_account_without_transactions() throws Exception {
    CashAccountApi api = new CashAccountApi(anApiClient(ADMIN_TOKEN));

    api.deleteCashAccountById(ADMIN_ID, COMPANY1_ID, CASH_ACCOUNT2_ID);

    PaginatedResponse resp = api.getCashAccounts(ADMIN_ID, COMPANY1_ID, 1, 100);

    List<CashAccount> accounts = extractData(resp, CashAccount.class);
    assertEquals(1, accounts.size());
    assertEquals(CASH_ACCOUNT1_ID, accounts.get(0).getId());
  }

  @Test
  void admin_gets_not_found_when_cash_account_does_not_exist() {
    CashAccountApi api = new CashAccountApi(anApiClient(ADMIN_TOKEN));

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"CashAccount with id nonexistent_ca not found\"}",
        () -> api.getCashAccountById(ADMIN_ID, COMPANY1_ID, "nonexistent_ca"));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
