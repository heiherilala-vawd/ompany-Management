package com.example.demo.endpoint.rest.controller.dashboard;

import com.example.demo.model.dashboard.EquipmentBreakdownResponse;
import com.example.demo.model.dashboard.EquipmentDashboardResponse;
import com.example.demo.model.dashboard.EquipmentSummaryResponse;
import com.example.demo.model.dashboard.HrBreakdownResponse;
import com.example.demo.model.dashboard.HrDashboardResponse;
import com.example.demo.model.dashboard.HrSummaryResponse;
import com.example.demo.model.dashboard.MaterialBreakdownResponse;
import com.example.demo.model.dashboard.MaterialDashboardResponse;
import com.example.demo.model.dashboard.MaterialSummaryResponse;
import com.example.demo.model.dashboard.MonetaryBreakdownResponse;
import com.example.demo.model.dashboard.MonetaryDashboardResponse;
import com.example.demo.model.dashboard.MonetarySummaryResponse;
import com.example.demo.model.dashboard.TimeSeriesResponse;
import com.example.demo.service.dashboard.DashboardEquipmentService;
import com.example.demo.service.dashboard.DashboardHrService;
import com.example.demo.service.dashboard.DashboardMaterialService;
import com.example.demo.service.dashboard.DashboardMonetaryService;
import com.example.demo.service.dashboard.DashboardTimeSeriesService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardMaterialService dashboardMaterialService;
  private final DashboardEquipmentService dashboardEquipmentService;
  private final DashboardHrService dashboardHrService;
  private final DashboardMonetaryService dashboardMonetaryService;
  private final DashboardTimeSeriesService dashboardTimeSeriesService;

  // ===================== Endpoints existants (compatibilité) =====================

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/materials")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public MaterialDashboardResponse getMaterialDashboard(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardMaterialService.getDashboard(jobId, dateFrom, dateTo);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/equipment")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public EquipmentDashboardResponse getEquipmentDashboard(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardEquipmentService.getDashboard(jobId, dateFrom, dateTo);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/hr")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public HrDashboardResponse getHrDashboard(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardHrService.getDashboard(companyId, jobId, dateFrom, dateTo);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/monetary")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public MonetaryDashboardResponse getMonetaryDashboard(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardMonetaryService.getDashboard(companyId, jobId, dateFrom, dateTo);
  }

  // ===================== HR Endpoints =====================

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/hr/summary")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public HrSummaryResponse getHrSummary(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardHrService.getSummary(companyId, jobId, dateFrom, dateTo);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/hr/breakdown")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public HrBreakdownResponse getHrBreakdown(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardHrService.getBreakdown(companyId, jobId, dateFrom, dateTo);
  }

  // ===================== Equipment Endpoints =====================

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/equipment/summary")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public EquipmentSummaryResponse getEquipmentSummary(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardEquipmentService.getSummary(jobId, dateFrom, dateTo);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/equipment/breakdown")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public EquipmentBreakdownResponse getEquipmentBreakdown(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardEquipmentService.getBreakdown(jobId, dateFrom, dateTo);
  }

  // ===================== Material Endpoints =====================

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/materials/summary")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public MaterialSummaryResponse getMaterialSummary(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardMaterialService.getSummary(jobId, dateFrom, dateTo);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/materials/breakdown")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public MaterialBreakdownResponse getMaterialBreakdown(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardMaterialService.getBreakdown(jobId, dateFrom, dateTo);
  }

  // ===================== Monetary Endpoints =====================

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/monetary/summary")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public MonetarySummaryResponse getMonetarySummary(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardMonetaryService.getSummary(companyId, jobId, dateFrom, dateTo);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/monetary/breakdown")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public MonetaryBreakdownResponse getMonetaryBreakdown(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardMonetaryService.getBreakdown(companyId, jobId, dateFrom, dateTo);
  }

  // ===================== Time Series Endpoints =====================

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/monetary/revenue")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getRevenueTimeSeries(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo,
      @RequestParam(name = "granularity", defaultValue = "month") String granularity) {
    return dashboardTimeSeriesService.revenue(companyId, jobId, dateFrom, dateTo, granularity);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/monetary/expenses")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getExpensesTimeSeries(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo,
      @RequestParam(name = "granularity", defaultValue = "month") String granularity) {
    return dashboardTimeSeriesService.expenses(companyId, jobId, dateFrom, dateTo, granularity);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/monetary/cashflow")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getCashFlowTimeSeries(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo,
      @RequestParam(name = "granularity", defaultValue = "month") String granularity) {
    return dashboardTimeSeriesService.cashflow(companyId, dateFrom, dateTo, granularity);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/monetary/profit")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getProfitTimeSeries(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo,
      @RequestParam(name = "granularity", defaultValue = "month") String granularity) {
    return dashboardTimeSeriesService.profit(companyId, jobId, dateFrom, dateTo, granularity);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/monetary/receivables")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getReceivablesTimeSeries(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo,
      @RequestParam(name = "granularity", defaultValue = "month") String granularity) {
    return dashboardTimeSeriesService.receivables(companyId, jobId, dateFrom, dateTo, granularity);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/monetary/budget")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getBudgetTimeSeries(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo,
      @RequestParam(name = "granularity", defaultValue = "month") String granularity) {
    return dashboardTimeSeriesService.budget(companyId, dateFrom, dateTo, granularity);
  }

  @GetMapping("/users/{userId}/companies/{companyId}/dashboard/monetary/expense_breakdown")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getExpenseBreakdownTimeSeries(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo,
      @RequestParam(name = "granularity", defaultValue = "month") String granularity) {
    return dashboardTimeSeriesService.expenseBreakdown(
        companyId, jobId, dateFrom, dateTo, granularity);
  }
}
