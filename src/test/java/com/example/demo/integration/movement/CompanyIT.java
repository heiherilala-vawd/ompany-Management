package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.CompanyApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.Company;
import com.example.demo.client.model.CompanyType;
import com.example.demo.client.model.CrupdateCompany;
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
@ContextConfiguration(initializers = CompanyIT.ContextInitializer.class)
class CompanyIT {
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
  void employee_can_get_company_by_id() throws Exception {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    CompanyApi api = new CompanyApi(employeeClient);

    Company actual = api.getCompanyById(COMPANY1_ID);
    Company expected = company1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_company_by_id() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    CompanyApi api = new CompanyApi(badClient);

    assertThrowsNotAuthorizedException(() -> api.getCompanyById(COMPANY1_ID));
  }

  @Test
  void admin_can_get_all_companies() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    CompanyApi api = new CompanyApi(adminClient);

    List<Company> companies = api.getCompanies(1, 100, null, null, null, null);

    assertEquals(2, companies.size());
    assertTrue(companies.stream().anyMatch(company -> COMPANY1_ID.equals(company.getId())));
    assertTrue(companies.stream().anyMatch(company -> COMPANY2_ID.equals(company.getId())));
  }

  @Test
  void employee_cannot_get_all_companies() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    CompanyApi api = new CompanyApi(employeeClient);

    assertThrowsForbiddenException(() -> api.getCompanies(1, 100, null, null, null, null));
  }

  @Test
  void warehouse_can_filter_companies_by_name() throws Exception {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    CompanyApi api = new CompanyApi(warehouseClient);

    List<Company> companies = api.getCompanies(1, 100, "BTP", null, null, null);

    assertEquals(1, companies.size());
    assertEquals(COMPANY1_ID, companies.get(0).getId());
  }

  @Test
  void administration_can_filter_companies_by_type() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    CompanyApi api = new CompanyApi(administrationClient);

    List<Company> companies = api.getCompanies(1, 100, null, null, null, CompanyType.HOTEL);

    assertEquals(1, companies.size());
    assertEquals(COMPANY2_ID, companies.get(0).getId());
  }

  @Test
  void administration_can_filter_companies_by_rib() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    CompanyApi api = new CompanyApi(administrationClient);

    List<Company> companies =
        api.getCompanies(1, 100, null, "FR7612345678901234567890123", null, null);

    assertEquals(1, companies.size());
    assertEquals(COMPANY1_ID, companies.get(0).getId());
  }

  @Test
  void administration_can_filter_companies_by_description() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    CompanyApi api = new CompanyApi(administrationClient);

    List<Company> companies = api.getCompanies(1, 100, null, null, "luxe", null);

    assertEquals(1, companies.size());
    assertEquals(COMPANY2_ID, companies.get(0).getId());
  }

  @Test
  @DirtiesContext
  void admin_can_update_companies() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    CompanyApi api = new CompanyApi(adminClient);

    CrupdateCompany companyToUpdate = companyToCrupdateCompany(company1());
    companyToUpdate.setDescription("Entreprise de construction mise a jour");

    List<Company> updatedCompanies = api.crupdateCompanies(List.of(companyToUpdate));
    Company updatedCompany = updatedCompanies.get(0);

    assertEquals(1, updatedCompanies.size());
    assertEquals(COMPANY1_ID, updatedCompany.getId());
    assertEquals("Entreprise de construction mise a jour", updatedCompany.getDescription());
    assertEquals(company1().getName(), updatedCompany.getName());
  }

  @Test
  void employee_cannot_create_companies() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    CompanyApi api = new CompanyApi(employeeClient);

    assertThrowsForbiddenException(() -> api.crupdateCompanies(List.of(someCreatableCompany())));
  }

  @Test
  void administration_cannot_delete_company() {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    CompanyApi api = new CompanyApi(administrationClient);

    assertThrowsForbiddenException(() -> api.deleteCompanyById(COMPANY1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
