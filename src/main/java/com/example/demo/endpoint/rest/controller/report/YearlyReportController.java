package com.example.demo.endpoint.rest.controller.report;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.report.YearlyReport;
import com.example.demo.service.report.YearlyReportService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class YearlyReportController {

  private final YearlyReportService yearlyReportService;

  @GetMapping("/companies/{comp_id}/yearly-report")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public YearlyReport getYearlyReport(
      @PathVariable String comp_id,
      @RequestParam Integer year,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return yearlyReportService.generateReport(comp_id, year, page, pageSize);
  }
}
