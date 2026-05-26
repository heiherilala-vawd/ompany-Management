package com.example.demo.integration.dashboard;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestDataSqlLoader;
import com.example.demo.integration.conf.TestUtils;
import com.example.demo.model.dashboard.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = DashboardIT.ContextInitializer.class)
class DashboardIT {
  @Autowired private DataSource dataSource;

  @MockitoBean private SentryConf sentryConf;
  @MockitoBean private AuthenticationManager authenticationManagerMock;
  @MockitoBean private JwtUtils jwtServiceMock;

  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  @BeforeEach
  void setUp() throws Exception {
    TestUtils.setUpJwtService(jwtServiceMock);
    TestUtils.setUpAuthenticationManager(authenticationManagerMock);
    TestDataSqlLoader.executeAllSqlScripts(dataSource);
  }

  private <T> T get(String path, Class<T> responseType, String token) throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + ContextInitializer.SERVER_PORT + path))
            .header("Authorization", "Bearer " + token)
            .GET()
            .build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() >= 400) {
      throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
    }
    return OBJECT_MAPPER.readValue(response.body(), responseType);
  }

  // ==================== MATERIAL DASHBOARD ====================

  @Test
  void admin_can_get_material_dashboard() throws Exception {
    MaterialDashboardResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/materials",
            MaterialDashboardResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertTrue(result.getStockValueTotal().compareTo(BigDecimal.ZERO) > 0);
    assertNotNull(result.getTop5StockValue());
    assertNotNull(result.getTop5ConsumptionCost());
    assertNotNull(result.getStockByMaterial());
    assertNotNull(result.getConsumptionByMaterial());
    assertNotNull(result.getExpiringMaterials());
  }

  @Test
  void employee_cannot_get_material_dashboard() {
    assertThrows(
        Exception.class,
        () ->
            get(
                "/companies/" + COMPANY1_ID + "/dashboard/materials",
                MaterialDashboardResponse.class,
                EMPLOYEE_TOKEN));
  }

  @Test
  void warehouse_worker_can_get_material_dashboard() throws Exception {
    MaterialDashboardResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/materials",
            MaterialDashboardResponse.class,
            WAREHOUSE_TOKEN);
    assertNotNull(result);
  }

  // ==================== EQUIPMENT DASHBOARD ====================

  @Test
  void admin_can_get_equipment_dashboard() throws Exception {
    EquipmentDashboardResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/equipment",
            EquipmentDashboardResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertNotNull(result.getTotalEquipment());
    assertNotNull(result.getAvailableCount());
    assertNotNull(result.getBrokenCount());
    assertNotNull(result.getScheduledMaintenances());
    assertNotNull(result.getLeasedEquipment());
  }

  @Test
  void employee_cannot_get_equipment_dashboard() {
    assertThrows(
        Exception.class,
        () ->
            get(
                "/companies/" + COMPANY1_ID + "/dashboard/equipment",
                EquipmentDashboardResponse.class,
                EMPLOYEE_TOKEN));
  }

  // ==================== HR DASHBOARD ====================

  @Test
  void admin_can_get_hr_dashboard() throws Exception {
    HrDashboardResponse result =
        get("/companies/" + COMPANY1_ID + "/dashboard/hr", HrDashboardResponse.class, ADMIN_TOKEN);
    assertNotNull(result);
    assertTrue(result.getTotalEmployees() > 0);
    assertNotNull(result.getEmployeesByDepartment());
    assertNotNull(result.getLeaveSummary());
  }

  @Test
  void employee_cannot_get_hr_dashboard() {
    assertThrows(
        Exception.class,
        () ->
            get(
                "/companies/" + COMPANY1_ID + "/dashboard/hr",
                HrDashboardResponse.class,
                EMPLOYEE_TOKEN));
  }

  // ==================== MONETARY DASHBOARD ====================

  @Test
  void admin_can_get_monetary_dashboard() throws Exception {
    MonetaryDashboardResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/monetary",
            MonetaryDashboardResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertNotNull(result.getTotalRevenue());
    assertNotNull(result.getTotalExpenses());
    assertNotNull(result.getCashFlow());
    assertNotNull(result.getBudgetVsActual());
    assertNotNull(result.getActiveLoans());
  }

  @Test
  void employee_cannot_get_monetary_dashboard() {
    assertThrows(
        Exception.class,
        () ->
            get(
                "/companies/" + COMPANY1_ID + "/dashboard/monetary",
                MonetaryDashboardResponse.class,
                EMPLOYEE_TOKEN));
  }

  // ==================== TIME SERIES ENDPOINTS ====================

  @Test
  void admin_can_get_revenue_time_series() throws Exception {
    TimeSeriesResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/monetary/revenue?granularity=month",
            TimeSeriesResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertEquals("month", result.getGranularity());
    assertNotNull(result.getIntervals());
    assertNotNull(result.getTotal());
  }

  @Test
  void admin_can_get_expenses_time_series() throws Exception {
    TimeSeriesResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/monetary/expenses?granularity=month",
            TimeSeriesResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertNotNull(result.getIntervals());
    assertNotNull(result.getTotal());
  }

  @Test
  void admin_can_get_cashflow_time_series() throws Exception {
    TimeSeriesResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/monetary/cashflow?granularity=month",
            TimeSeriesResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertNotNull(result.getIntervals());
  }

  @Test
  void admin_can_get_profit_time_series() throws Exception {
    TimeSeriesResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/monetary/profit?granularity=month",
            TimeSeriesResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertNotNull(result.getIntervals());
  }

  @Test
  void admin_can_get_receivables() throws Exception {
    TimeSeriesResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/monetary/receivables?granularity=month",
            TimeSeriesResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertNotNull(result.getIntervals());
  }

  @Test
  void admin_can_get_budget_time_series() throws Exception {
    TimeSeriesResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/monetary/budget?granularity=month",
            TimeSeriesResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertNotNull(result.getIntervals());
  }

  @Test
  void admin_can_get_expense_breakdown() throws Exception {
    TimeSeriesResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/monetary/expense-breakdown?granularity=month",
            TimeSeriesResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertNotNull(result.getIntervals());
  }

  @Test
  void time_series_with_job_id_filter() throws Exception {
    TimeSeriesResponse result =
        get(
            "/companies/"
                + COMPANY1_ID
                + "/dashboard/monetary/revenue?granularity=month&job_id="
                + JOB1_ID,
            TimeSeriesResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertEquals(JOB1_ID, result.getFilteredByJob());
  }

  @Test
  void all_granularities_work_for_revenue() throws Exception {
    for (String g : new String[] {"day", "2day", "week", "month", "quarter", "year"}) {
      TimeSeriesResponse result =
          get(
              "/companies/" + COMPANY1_ID + "/dashboard/monetary/revenue?granularity=" + g,
              TimeSeriesResponse.class,
              ADMIN_TOKEN);
      assertNotNull(result, "granularity=" + g + " failed");
      assertEquals(g, result.getGranularity());
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
