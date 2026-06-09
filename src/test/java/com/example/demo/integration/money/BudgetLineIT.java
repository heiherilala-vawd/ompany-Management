package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.BudgetLineApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.BudgetLine;
import com.example.demo.client.model.CrupdateBudgetLine;
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
@ContextConfiguration(initializers = BudgetLineIT.ContextInitializer.class)
class BudgetLineIT {
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
  void administration_can_get_budget_line_by_id() throws Exception {
    BudgetLineApi api = new BudgetLineApi(anApiClient(ADMINISTRATION_TOKEN));

    BudgetLine actual = api.getBudgetLineById(COMPANY1_ID, BUDGET_LINE1_ID);
    BudgetLine expected = budgetLine1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_budget_line_by_id() {
    BudgetLineApi api = new BudgetLineApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(() -> api.getBudgetLineById(COMPANY1_ID, BUDGET_LINE1_ID));
  }

  @Test
  void administration_can_get_all_budget_lines() throws Exception {
    BudgetLineApi api = new BudgetLineApi(anApiClient(ADMINISTRATION_TOKEN));

    List<BudgetLine> lines = api.getBudgetLines(ADMIN_ID, COMPANY1_ID, 1, 100);

    assertEquals(2, lines.size());
    assertTrue(lines.stream().anyMatch(bl -> BUDGET_LINE1_ID.equals(bl.getId())));
    assertTrue(lines.stream().anyMatch(bl -> BUDGET_LINE2_ID.equals(bl.getId())));
  }

  @Test
  void employee_cannot_get_all_budget_lines() {
    BudgetLineApi api = new BudgetLineApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(() -> api.getBudgetLines(EMPLOYEE_ID, COMPANY1_ID, 1, 100));
  }

  @Test
  @DirtiesContext
  void admin_can_update_budget_line() throws Exception {
    BudgetLineApi api = new BudgetLineApi(anApiClient(ADMIN_TOKEN));

    CrupdateBudgetLine toUpdate = budgetLineToCrupdateBudgetLine(budgetLine1());
    toUpdate.setDescription("Budget matériaux construction - révisé");

    List<BudgetLine> updated = api.crupdateBudgetLines(COMPANY1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(BUDGET_LINE1_ID, updated.get(0).getId());
    assertEquals("Budget matériaux construction - révisé", updated.get(0).getDescription());
  }

  @Test
  void employee_cannot_create_budget_line() {
    BudgetLineApi api = new BudgetLineApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () -> api.crupdateBudgetLines(COMPANY1_ID, List.of(someCreatableBudgetLine())));
  }

  @Test
  void administration_cannot_delete_budget_line() {
    BudgetLineApi api = new BudgetLineApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(() -> api.deleteBudgetLineById(COMPANY1_ID, BUDGET_LINE1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_budget_line() throws Exception {
    BudgetLineApi api = new BudgetLineApi(anApiClient(ADMIN_TOKEN));

    api.deleteBudgetLineById(COMPANY1_ID, BUDGET_LINE1_ID);

    List<BudgetLine> lines = api.getBudgetLines(ADMIN_ID, COMPANY1_ID, 1, 100);
    assertEquals(1, lines.size());
    assertEquals(BUDGET_LINE2_ID, lines.get(0).getId());
  }

  @Test
  void admin_gets_not_found_when_budget_line_does_not_exist() {
    BudgetLineApi api = new BudgetLineApi(anApiClient(ADMIN_TOKEN));

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"BudgetLine with id nonexistent_bl not found\"}",
        () -> api.getBudgetLineById(COMPANY1_ID, "nonexistent_bl"));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
