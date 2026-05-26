package com.example.demo.endpoint.rest.controller.dashboard;

import com.example.demo.model.dashboard.EquipmentDashboardResponse;
import com.example.demo.model.dashboard.HrDashboardResponse;
import com.example.demo.model.dashboard.MaterialDashboardResponse;
import com.example.demo.model.dashboard.MonetaryDashboardResponse;
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

  @GetMapping("/companies/{comp_id}/dashboard/materials")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public MaterialDashboardResponse getMaterialDashboard(
      @PathVariable("comp_id") String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardMaterialService.getDashboard(jobId, dateFrom, dateTo);
  }

  @GetMapping("/companies/{comp_id}/dashboard/equipment")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public EquipmentDashboardResponse getEquipmentDashboard(
      @PathVariable("comp_id") String companyId,
      @RequestParam(name = "job_id", required = false) String jobId) {
    return dashboardEquipmentService.getDashboard(jobId);
  }

  @GetMapping("/companies/{comp_id}/dashboard/hr")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public HrDashboardResponse getHrDashboard(
      @PathVariable("comp_id") String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardHrService.getDashboard(companyId, jobId, dateFrom, dateTo);
  }

  @GetMapping("/companies/{comp_id}/dashboard/monetary")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public MonetaryDashboardResponse getMonetaryDashboard(
      @PathVariable("comp_id") String companyId,
      @RequestParam(name = "job_id", required = false) String jobId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    return dashboardMonetaryService.getDashboard(companyId, jobId, dateFrom, dateTo);
  }

  // ===================== Time Series Endpoints =====================

  @GetMapping("/companies/{comp_id}/dashboard/monetary/revenue")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getRevenueTimeSeries(
      @PathVariable("comp_id") String companyId,
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

  @GetMapping("/companies/{comp_id}/dashboard/monetary/expenses")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getExpensesTimeSeries(
      @PathVariable("comp_id") String companyId,
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

  @GetMapping("/companies/{comp_id}/dashboard/monetary/cashflow")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getCashFlowTimeSeries(
      @PathVariable("comp_id") String companyId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo,
      @RequestParam(name = "granularity", defaultValue = "month") String granularity) {
    return dashboardTimeSeriesService.cashflow(companyId, dateFrom, dateTo, granularity);
  }

  @GetMapping("/companies/{comp_id}/dashboard/monetary/profit")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getProfitTimeSeries(
      @PathVariable("comp_id") String companyId,
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

  @GetMapping("/companies/{comp_id}/dashboard/monetary/receivables")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getReceivablesTimeSeries(
      @PathVariable("comp_id") String companyId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo,
      @RequestParam(name = "granularity", defaultValue = "month") String granularity) {
    return dashboardTimeSeriesService.receivables(companyId, dateFrom, dateTo, granularity);
  }

  @GetMapping("/companies/{comp_id}/dashboard/monetary/budget")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getBudgetTimeSeries(
      @PathVariable("comp_id") String companyId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo,
      @RequestParam(name = "granularity", defaultValue = "month") String granularity) {
    return dashboardTimeSeriesService.budget(companyId, dateFrom, dateTo, granularity);
  }

  @GetMapping("/companies/{comp_id}/dashboard/monetary/expense-breakdown")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TimeSeriesResponse getExpenseBreakdownTimeSeries(
      @PathVariable("comp_id") String companyId,
      @RequestParam(name = "date_from", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(name = "date_to", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo,
      @RequestParam(name = "granularity", defaultValue = "month") String granularity) {
    return dashboardTimeSeriesService.expenseBreakdown(companyId, dateFrom, dateTo, granularity);
  }
}
