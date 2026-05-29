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

  // ==================== SUMMARY ENDPOINTS ====================

  @Test
  void admin_can_get_hr_summary() throws Exception {
    HrSummaryResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/hr/summary",
            HrSummaryResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertEquals(7, result.getTotalEmployees().intValue());
    assertEquals(0, result.getNewHires().intValue());
    assertEquals(BigDecimal.ZERO, result.getPayrollTotal());
    assertEquals(0, result.getTasksCompleted().intValue());
    assertNotNull(result.getLeaveDaysApproved());
    assertNotNull(result.getAbsenceRate());
  }

  @Test
  void admin_can_get_hr_summary_with_filters() throws Exception {
    HrSummaryResponse result =
        get(
            "/companies/"
                + COMPANY1_ID
                + "/dashboard/hr/summary?job_id="
                + JOB1_ID
                + "&date_from=2024-01-01&date_to=2026-12-31",
            HrSummaryResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertTrue(result.getTotalEmployees() > 0);
    assertEquals(0, new BigDecimal("125000").compareTo(result.getPayrollTotal()));
  }

  @Test
  void employee_cannot_get_hr_summary() {
    assertThrows(
        Exception.class,
        () ->
            get(
                "/companies/" + COMPANY1_ID + "/dashboard/hr/summary",
                HrSummaryResponse.class,
                EMPLOYEE_TOKEN));
  }

  @Test
  void admin_can_get_equipment_summary() throws Exception {
    EquipmentSummaryResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/equipment/summary",
            EquipmentSummaryResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertEquals(5, result.getTotalEquipment().intValue());
    assertEquals(5, result.getAvailableCount().intValue());
    assertEquals(0, result.getBrokenCount().intValue());
    assertEquals(0, result.getLostCount().intValue());
    assertEquals(0, new BigDecimal("18").compareTo(result.getTotalUsageHours()));
    assertEquals(0, BigDecimal.ZERO.compareTo(result.getAvgAgeYears()));
  }

  @Test
  void admin_can_get_equipment_summary_with_job_filter() throws Exception {
    EquipmentSummaryResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/equipment/summary?job_id=" + JOB1_ID,
            EquipmentSummaryResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertEquals(5, result.getTotalEquipment().intValue());
    assertEquals(0, new BigDecimal("18").compareTo(result.getTotalUsageHours()));
  }

  @Test
  void warehouse_worker_can_get_equipment_summary() throws Exception {
    EquipmentSummaryResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/equipment/summary",
            EquipmentSummaryResponse.class,
            WAREHOUSE_TOKEN);
    assertNotNull(result);
  }

  @Test
  void employee_cannot_get_equipment_summary() {
    assertThrows(
        Exception.class,
        () ->
            get(
                "/companies/" + COMPANY1_ID + "/dashboard/equipment/summary",
                EquipmentSummaryResponse.class,
                EMPLOYEE_TOKEN));
  }

  @Test
  void admin_can_get_material_summary() throws Exception {
    MaterialSummaryResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/materials/summary",
            MaterialSummaryResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertEquals(0, new BigDecimal("500000").compareTo(result.getStockValueTotal()));
    assertEquals(0, new BigDecimal("75000").compareTo(result.getConsumptionCostTotal()));
    assertEquals(3, result.getTotalMaterialsCount().intValue());
    assertTrue(result.getExpiringCount() >= 0);
  }

  @Test
  void warehouse_worker_can_get_material_summary() throws Exception {
    MaterialSummaryResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/materials/summary",
            MaterialSummaryResponse.class,
            WAREHOUSE_TOKEN);
    assertNotNull(result);
  }

  @Test
  void employee_cannot_get_material_summary() {
    assertThrows(
        Exception.class,
        () ->
            get(
                "/companies/" + COMPANY1_ID + "/dashboard/materials/summary",
                MaterialSummaryResponse.class,
                EMPLOYEE_TOKEN));
  }

  @Test
  void admin_can_get_monetary_summary() throws Exception {
    MonetarySummaryResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/monetary/summary",
            MonetarySummaryResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertEquals(0, new BigDecimal("875000").compareTo(result.getTotalRevenue()));
    assertEquals(0, new BigDecimal("125000").compareTo(result.getTotalExpenses()));
    assertEquals(0, new BigDecimal("750000").compareTo(result.getGrossMargin()));
    assertEquals(0, BigDecimal.ZERO.compareTo(result.getExpectedRevenue()));
    assertEquals(0, new BigDecimal("120000").compareTo(result.getReceivables()));
    assertEquals(0, new BigDecimal("2500").compareTo(result.getFixedCostsTotal()));
    assertNotNull(result.getActiveLoansCount());
    assertNotNull(result.getCashFlow());
    assertNotNull(result.getCashAccounts());
  }

  @Test
  void admin_can_get_monetary_summary_with_job_filter() throws Exception {
    MonetarySummaryResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/monetary/summary?job_id=" + JOB1_ID,
            MonetarySummaryResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertEquals(0, new BigDecimal("875000").compareTo(result.getTotalRevenue()));
    assertEquals(0, new BigDecimal("125000").compareTo(result.getTotalExpenses()));
  }

  @Test
  void employee_cannot_get_monetary_summary() {
    assertThrows(
        Exception.class,
        () ->
            get(
                "/companies/" + COMPANY1_ID + "/dashboard/monetary/summary",
                MonetarySummaryResponse.class,
                EMPLOYEE_TOKEN));
  }

  // ==================== BREAKDOWN ENDPOINTS ====================

  @Test
  void admin_can_get_hr_breakdown() throws Exception {
    HrBreakdownResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/hr/breakdown",
            HrBreakdownResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertNotNull(result.getEmployeesByDepartment());
    assertNotNull(result.getLeaveDaysByType());
    assertNotNull(result.getLeaveByStatus());
    assertNotNull(result.getEmployeesByJob());
    assertNotNull(result.getLaborCostByJob());
    assertNotNull(result.getPayrollByType());
    assertNotNull(result.getLeaveBalances());

    assertTrue(result.getLaborCostByJob().stream().anyMatch(j -> JOB1_ID.equals(j.getJobId())));
  }

  @Test
  void admin_can_get_hr_breakdown_with_filters() throws Exception {
    HrBreakdownResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/hr/breakdown?job_id=" + JOB1_ID,
            HrBreakdownResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertFalse(result.getPayrollByType().isEmpty());
  }

  @Test
  void administration_can_get_hr_breakdown() throws Exception {
    HrBreakdownResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/hr/breakdown",
            HrBreakdownResponse.class,
            ADMINISTRATION_TOKEN);
    assertNotNull(result);
  }

  @Test
  void warehouse_worker_cannot_get_hr_breakdown() {
    assertThrows(
        Exception.class,
        () ->
            get(
                "/companies/" + COMPANY1_ID + "/dashboard/hr/breakdown",
                HrBreakdownResponse.class,
                WAREHOUSE_TOKEN));
  }

  @Test
  void admin_can_get_equipment_breakdown() throws Exception {
    EquipmentBreakdownResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/equipment/breakdown",
            EquipmentBreakdownResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertNotNull(result.getCategoryDistribution());
    assertNotNull(result.getStatusBreakdown());
    assertNotNull(result.getUsageByJob());
    assertNotNull(result.getMaintenanceCostByEquipment());
    assertNotNull(result.getLeasedEquipment());
    assertNotNull(result.getScheduledMaintenances());

    assertEquals(4, result.getStatusBreakdown().size());
    assertEquals(
        5,
        result.getStatusBreakdown().stream()
            .mapToInt(EquipmentBreakdownResponse.StatusCount::getCount)
            .sum());

    assertTrue(result.getUsageByJob().stream().anyMatch(u -> JOB1_ID.equals(u.getJobId())));
  }

  @Test
  void warehouse_worker_can_get_equipment_breakdown() throws Exception {
    EquipmentBreakdownResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/equipment/breakdown",
            EquipmentBreakdownResponse.class,
            WAREHOUSE_TOKEN);
    assertNotNull(result);
  }

  @Test
  void admin_can_get_material_breakdown() throws Exception {
    MaterialBreakdownResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/materials/breakdown",
            MaterialBreakdownResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertNotNull(result.getTop5StockValue());
    assertNotNull(result.getTop5ConsumptionCost());
    assertNotNull(result.getStockByMaterial());
    assertNotNull(result.getConsumptionByMaterial());
    assertNotNull(result.getStockValueByWarehouse());
    assertNotNull(result.getExpiringMaterials());

    assertFalse(result.getTop5StockValue().isEmpty());
    assertEquals("Ciment", result.getTop5StockValue().get(0).getMaterialName());

    assertTrue(result.getStockByMaterial().stream().anyMatch(s -> s.getWarehouse() != null));
  }

  @Test
  void warehouse_worker_can_get_material_breakdown() throws Exception {
    MaterialBreakdownResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/materials/breakdown",
            MaterialBreakdownResponse.class,
            WAREHOUSE_TOKEN);
    assertNotNull(result);
  }

  @Test
  void admin_can_get_monetary_breakdown() throws Exception {
    MonetaryBreakdownResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/monetary/breakdown",
            MonetaryBreakdownResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertNotNull(result.getExpensesByType());
    assertNotNull(result.getBudgetVsActual());
    assertNotNull(result.getExpectedVsActual());
    assertNotNull(result.getRevenueByJob());
    assertNotNull(result.getExpenseByJob());
    assertNotNull(result.getProfitabilityByJob());
    assertNotNull(result.getActiveLoans());

    assertEquals(4, result.getExpensesByType().size());
    assertEquals(
        0,
        new BigDecimal("125000")
            .compareTo(
                result.getExpensesByType().stream()
                    .map(MonetaryBreakdownResponse.ExpenseByType::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(4))));

    assertTrue(result.getRevenueByJob().stream().anyMatch(r -> JOB1_ID.equals(r.getJobId())));
  }

  @Test
  void admin_can_get_monetary_breakdown_with_job_filter() throws Exception {
    MonetaryBreakdownResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/monetary/breakdown?job_id=" + JOB1_ID,
            MonetaryBreakdownResponse.class,
            ADMIN_TOKEN);
    assertNotNull(result);
    assertFalse(result.getExpensesByType().isEmpty());
    assertTrue(
        result.getRevenueByJob().stream()
            .anyMatch(r -> r.getTotal().compareTo(BigDecimal.ZERO) > 0));
  }

  @Test
  void administration_can_get_monetary_breakdown() throws Exception {
    MonetaryBreakdownResponse result =
        get(
            "/companies/" + COMPANY1_ID + "/dashboard/monetary/breakdown",
            MonetaryBreakdownResponse.class,
            ADMINISTRATION_TOKEN);
    assertNotNull(result);
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
