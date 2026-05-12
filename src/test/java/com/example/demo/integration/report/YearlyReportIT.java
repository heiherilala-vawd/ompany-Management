package com.example.demo.integration.report;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.demo.SentryConf;
import com.example.demo.client.api.ReportApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.YearlyReport;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.endpoint.rest.security.service.CustomUserDetailsService;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestDataSqlLoader;
import com.example.demo.integration.conf.TestUtils;
import com.example.demo.model.User;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = YearlyReportIT.ContextInitializer.class)
class YearlyReportIT {
  @Autowired private DataSource dataSource;

  @MockitoBean private SentryConf sentryConf;
  @MockitoBean private JwtUtils jwtServiceMock;
  @MockitoBean private CustomUserDetailsService customUserDetailsServiceMock;

  private static ApiClient anApiClient(String token) {
    return TestUtils.anApiClient(token, ContextInitializer.SERVER_PORT);
  }

  @BeforeEach
  void setUp() throws Exception {
    TestUtils.setUpJwtService(jwtServiceMock);

    // Mock CustomUserDetailsService to return proper UserDetails with authorities
    when(customUserDetailsServiceMock.loadUserByUsername(anyString()))
        .thenAnswer(
            invocation -> {
              String email = invocation.getArgument(0);
              User user = new User();
              if (TestUtils.ADMIN_EMAIL.equals(email)) {
                user.setRole(User.Role.ADMIN);
              } else if (TestUtils.ADMINISTRATION_EMAIL.equals(email)) {
                user.setRole(User.Role.ADMINISTRATION);
              } else if (TestUtils.EMPLOYEE_EMAIL.equals(email)) {
                user.setRole(User.Role.EMPLOYEE);
              } else if (TestUtils.WAREHOUSE_EMAIL.equals(email)) {
                user.setRole(User.Role.WAREHOUSE_WORKER);
              }
              user.setEmail(email);
              return user;
            });

    TestDataSqlLoader.executeAllSqlScripts(dataSource);
  }

  @Test
  void admin_can_get_yearly_report() throws Exception {
    ReportApi api = new ReportApi(anApiClient(ADMIN_TOKEN));

    YearlyReport report = api.companiesCompIdYearlyReportGet(COMPANY1_ID, 2024, 1, 100);

    assertNotNull(report);
    assertEquals(2024, report.getYear());
    assertNotNull(report.getJobsWithFinancials());
    assertNotNull(report.getSummary());
    assertTrue(report.getJobsWithFinancials().size() > 0);
  }

  @Test
  void admin_can_get_yearly_report_with_pagination() throws Exception {
    ReportApi api = new ReportApi(anApiClient(ADMIN_TOKEN));

    YearlyReport report = api.companiesCompIdYearlyReportGet(COMPANY1_ID, 2024, 1, 2);

    assertNotNull(report);
    assertEquals(2024, report.getYear());
    assertTrue(report.getJobsWithFinancials().size() <= 2);
  }

  @Test
  void administration_can_get_yearly_report() throws Exception {
    ReportApi api = new ReportApi(anApiClient(ADMINISTRATION_TOKEN));

    YearlyReport report = api.companiesCompIdYearlyReportGet(COMPANY1_ID, 2024, null, null);

    assertNotNull(report);
    assertEquals(2024, report.getYear());
  }

  @Test
  void employee_cannot_get_yearly_report() {
    ReportApi api = new ReportApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () -> api.companiesCompIdYearlyReportGet(COMPANY1_ID, 2024, null, null));
  }

  @Test
  void warehouse_worker_cannot_get_yearly_report() {
    ReportApi api = new ReportApi(anApiClient(WAREHOUSE_TOKEN));

    assertThrowsForbiddenException(
        () -> api.companiesCompIdYearlyReportGet(COMPANY1_ID, 2024, null, null));
  }

  @Test
  void report_contains_financial_data() throws Exception {
    ReportApi api = new ReportApi(anApiClient(ADMIN_TOKEN));

    YearlyReport report = api.companiesCompIdYearlyReportGet(COMPANY1_ID, 2024, null, null);

    assertNotNull(report);
    assertNotNull(report.getJobsWithFinancials());
    for (var jobWithFinancials : report.getJobsWithFinancials()) {
      assertNotNull(jobWithFinancials.getJob());
      assertNotNull(jobWithFinancials.getTotalIncome());
      assertNotNull(jobWithFinancials.getTotalExpense());
      assertNotNull(jobWithFinancials.getNetProfit());
    }
  }

  @Test
  void report_summary_is_correct() throws Exception {
    ReportApi api = new ReportApi(anApiClient(ADMIN_TOKEN));

    YearlyReport report = api.companiesCompIdYearlyReportGet(COMPANY1_ID, 2024, null, null);

    assertNotNull(report);
    assertNotNull(report.getSummary());
    assertNotNull(report.getSummary().getTotalIncome());
    assertNotNull(report.getSummary().getTotalExpense());
    assertNotNull(report.getSummary().getNetProfit());
    assertNotNull(report.getSummary().getJobCount());
    assertEquals(
        report.getSummary().getTotalIncome() - report.getSummary().getTotalExpense(),
        report.getSummary().getNetProfit());
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
