package com.example.demo.service.report;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.Job;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.JobCriteria;
import com.example.demo.model.report.JobWithFinancials;
import com.example.demo.model.report.YearlyReport;
import com.example.demo.service.JobService;
import com.example.demo.service.money.ExpenseMoneyService;
import com.example.demo.service.money.IncomeMoneyService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class YearlyReportService {

  private final JobService jobService;
  private final IncomeMoneyService incomeMoneyService;
  private final ExpenseMoneyService expenseMoneyService;

  public YearlyReport generateReport(
      String companyId, Integer year, PageFromOne page, BoundedPageSize pageSize) {

    // 1. Get all jobs for the company
    JobCriteria criteria = new JobCriteria();
    criteria.setCompanyId(companyId);

    Page<Job> allJobsPage = jobService.findAll(page, pageSize, criteria);

    log.info("Fetched {} jobs for company {}", allJobsPage.getContent().size(), companyId);

    // 2. Filter jobs by year in Java
    List<Job> jobsForYear =
        allJobsPage.getContent().stream()
            .filter(
                job -> {
                  boolean matchesYear = false;
                  if (job.getStartDate() != null && job.getStartDate().getYear() == year) {
                    matchesYear = true;
                  }
                  if (job.getContractSignatureDate() != null
                      && job.getContractSignatureDate().getYear() == year) {
                    matchesYear = true;
                  }
                  if (job.getEndDate() != null && job.getEndDate().getYear() == year) {
                    matchesYear = true;
                  }
                  return matchesYear;
                })
            .collect(Collectors.toList());

    log.info("Found {} jobs matching year {}", jobsForYear.size(), year);

    // 3. Convert jobs to JobWithFinancials
    List<JobWithFinancials> jobsWithFinancials =
        jobsForYear.stream()
            .map(
                job -> {
                  Integer totalIncome = incomeMoneyService.sumByJobId(job.getId());
                  Integer totalExpense = expenseMoneyService.sumByJobId(job.getId());
                  JobWithFinancials jwf =
                      JobWithFinancials.fromJobAndAmounts(job.getId(), totalIncome, totalExpense);
                  jwf.setJob(job);
                  return jwf;
                })
            .collect(Collectors.toList());

    // 4. Also get IN_PROGRESS jobs (might span multiple years)
    JobCriteria inProgressCriteria = new JobCriteria();
    inProgressCriteria.setCompanyId(companyId);
    inProgressCriteria.setStatus(Job.JobStatus.IN_PROGRESS);

    List<Job> inProgressJobs = jobService.findAll(null, null, inProgressCriteria).getContent();

    log.info("Found {} IN_PROGRESS jobs", inProgressJobs.size());

    // Add IN_PROGRESS jobs not already in the list
    int addedInProgress = 0;
    for (Job inProgressJob : inProgressJobs) {
      String jobId = inProgressJob.getId();
      if (jobsWithFinancials.stream().noneMatch(jwf -> jobId.equals(jwf.getJobId()))) {
        Integer totalIncome = incomeMoneyService.sumByJobId(jobId);
        Integer totalExpense = expenseMoneyService.sumByJobId(jobId);
        JobWithFinancials jwf =
            JobWithFinancials.fromJobAndAmounts(jobId, totalIncome, totalExpense);
        jwf.setJob(inProgressJob);
        jobsWithFinancials.add(jwf);
        addedInProgress++;
      }
    }

    log.info("Added {} IN_PROGRESS jobs to the report", addedInProgress);

    // 5. Build summary
    int totalIncome =
        jobsWithFinancials.stream()
            .mapToInt(jwf -> jwf.getTotalIncome() != null ? jwf.getTotalIncome() : 0)
            .sum();
    int totalExpense =
        jobsWithFinancials.stream()
            .mapToInt(jwf -> jwf.getTotalExpense() != null ? jwf.getTotalExpense() : 0)
            .sum();

    YearlyReport.Summary summary =
        YearlyReport.Summary.builder()
            .totalIncome(totalIncome)
            .totalExpense(totalExpense)
            .jobCount(jobsWithFinancials.size())
            .inProgressJobCount(inProgressJobs.size())
            .build();
    summary.setNetProfit(totalIncome - totalExpense);

    log.info(
        "Report summary: income={}, expense={}, netProfit={}, jobCount={}",
        totalIncome,
        totalExpense,
        summary.getNetProfit(),
        jobsWithFinancials.size());

    return YearlyReport.builder()
        .year(year)
        .jobsWithFinancials(jobsWithFinancials)
        .summary(summary)
        .build();
  }
}
