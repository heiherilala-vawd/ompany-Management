package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.SentryConf;
import com.example.demo.client.api.CompanyFixedCostApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CompanyFixedCost;
import com.example.demo.client.model.CrupdateCompanyFixedCost;
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
@ContextConfiguration(initializers = CompanyFixedCostIT.ContextInitializer.class)
class CompanyFixedCostIT {
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
  void administration_can_get_fixed_cost_by_id() throws Exception {
    CompanyFixedCostApi api = new CompanyFixedCostApi(anApiClient(ADMINISTRATION_TOKEN));

    CompanyFixedCost actual = api.getCompanyFixedCostById(COMPANY1_ID, FIXED_COST1_ID);
    CompanyFixedCost expected = companyFixedCost1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_fixed_cost_by_id() {
    CompanyFixedCostApi api = new CompanyFixedCostApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getCompanyFixedCostById(COMPANY1_ID, FIXED_COST1_ID));
  }

  @Test
  void administration_can_get_all_fixed_costs() throws Exception {
    CompanyFixedCostApi api = new CompanyFixedCostApi(anApiClient(ADMINISTRATION_TOKEN));

    List<CompanyFixedCost> costs = api.getCompanyFixedCosts(COMPANY1_ID);

    assertEquals(2, costs.size());
    assertTrue(costs.stream().anyMatch(c -> FIXED_COST1_ID.equals(c.getId())));
    assertTrue(costs.stream().anyMatch(c -> FIXED_COST2_ID.equals(c.getId())));
  }

  @Test
  @DirtiesContext
  void admin_can_update_fixed_costs() throws Exception {
    CompanyFixedCostApi api = new CompanyFixedCostApi(anApiClient(ADMIN_TOKEN));
    CrupdateCompanyFixedCost toUpdate =
        companyFixedCostToCrupdateCompanyFixedCost(companyFixedCost1());
    toUpdate.setDescription("Loyer mensuel ajuste");

    List<CompanyFixedCost> updated = api.crupdateCompanyFixedCosts(COMPANY1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(FIXED_COST1_ID, updated.get(0).getId());
    assertEquals("Loyer mensuel ajuste", updated.get(0).getDescription());
  }

  @Test
  void employee_cannot_create_fixed_costs() {
    CompanyFixedCostApi api = new CompanyFixedCostApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () -> api.crupdateCompanyFixedCosts(COMPANY1_ID, List.of(someCreatableCompanyFixedCost())));
  }

  @Test
  void administration_cannot_delete_fixed_cost() {
    CompanyFixedCostApi api = new CompanyFixedCostApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () -> api.deleteCompanyFixedCostById(COMPANY1_ID, FIXED_COST1_ID));
  }

  @Test
  void admin_cannot_create_fixed_cost_without_name() {
    CompanyFixedCostApi api = new CompanyFixedCostApi(anApiClient(ADMIN_TOKEN));
    CrupdateCompanyFixedCost invalid = someCreatableCompanyFixedCost();
    invalid.setName(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Company fixed cost name is mandatory\"}",
        () -> api.crupdateCompanyFixedCosts(COMPANY1_ID, List.of(invalid)));
  }

  @Test
  void admin_cannot_create_fixed_cost_without_amount() {
    CompanyFixedCostApi api = new CompanyFixedCostApi(anApiClient(ADMIN_TOKEN));
    CrupdateCompanyFixedCost invalid = someCreatableCompanyFixedCost();
    invalid.setAmount(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Company fixed cost amount must be non-negative\"}",
        () -> api.crupdateCompanyFixedCosts(COMPANY1_ID, List.of(invalid)));
  }

  @Test
  void admin_cannot_create_fixed_cost_without_start_date() {
    CompanyFixedCostApi api = new CompanyFixedCostApi(anApiClient(ADMIN_TOKEN));
    CrupdateCompanyFixedCost invalid = someCreatableCompanyFixedCost();
    invalid.setStartDate(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Company fixed cost start date is mandatory\"}",
        () -> api.crupdateCompanyFixedCosts(COMPANY1_ID, List.of(invalid)));
  }

  @Test
  void admin_cannot_create_fixed_cost_with_end_date_before_start_date() {
    CompanyFixedCostApi api = new CompanyFixedCostApi(anApiClient(ADMIN_TOKEN));
    CrupdateCompanyFixedCost invalid = someCreatableCompanyFixedCost();
    invalid.setStartDate(java.time.LocalDate.of(2024, 6, 1));
    invalid.setEndDate(java.time.LocalDate.of(2024, 5, 1));

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Company fixed cost end date cannot be before start date\"}",
        () -> api.crupdateCompanyFixedCosts(COMPANY1_ID, List.of(invalid)));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
