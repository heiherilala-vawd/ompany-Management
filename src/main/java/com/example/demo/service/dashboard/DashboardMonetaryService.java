package com.example.demo.service.dashboard;

import com.example.demo.model.Job;
import com.example.demo.model.dashboard.MonetaryBreakdownResponse;
import com.example.demo.model.dashboard.MonetaryDashboardResponse;
import com.example.demo.model.dashboard.MonetaryDashboardResponse.ActiveLoanItem;
import com.example.demo.model.dashboard.MonetaryDashboardResponse.BudgetVariance;
import com.example.demo.model.dashboard.MonetaryDashboardResponse.CashAccountItem;
import com.example.demo.model.dashboard.MonetaryDashboardResponse.CashFlowSummary;
import com.example.demo.model.dashboard.MonetaryDashboardResponse.ExpectedVsActual;
import com.example.demo.model.dashboard.MonetaryDashboardResponse.ExpenseByType;
import com.example.demo.model.dashboard.MonetaryDashboardResponse.JobExpense;
import com.example.demo.model.dashboard.MonetaryDashboardResponse.JobProfitability;
import com.example.demo.model.dashboard.MonetaryDashboardResponse.JobRevenue;
import com.example.demo.model.dashboard.MonetarySummaryResponse;
import com.example.demo.model.money.Loan;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.money.BudgetLineRepository;
import com.example.demo.repository.money.CashAccountRepository;
import com.example.demo.repository.money.CashTransactionRepository;
import com.example.demo.repository.money.CompanyFixedCostRepository;
import com.example.demo.repository.money.ExpenseMoneyRepository;
import com.example.demo.repository.money.IncomeMoneyRepository;
import com.example.demo.repository.money.IncomeReceiptRepository;
import com.example.demo.repository.money.LoanRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardMonetaryService {

  private final IncomeMoneyRepository incomeMoneyRepository;
  private final ExpenseMoneyRepository expenseMoneyRepository;
  private final IncomeReceiptRepository incomeReceiptRepository;
  private final CashAccountRepository cashAccountRepository;
  private final CashTransactionRepository cashTransactionRepository;
  private final BudgetLineRepository budgetLineRepository;
  private final LoanRepository loanRepository;
  private final CompanyFixedCostRepository companyFixedCostRepository;
  private final JobRepository jobRepository;

  public MonetaryDashboardResponse getDashboard(
      String companyId, String jobId, LocalDate dateFrom, LocalDate dateTo) {

    Instant from =
        dateFrom != null ? dateFrom.atStartOfDay(java.time.ZoneOffset.UTC).toInstant() : null;
    Instant to =
        dateTo != null
            ? dateTo.atTime(23, 59, 59).atZone(java.time.ZoneOffset.UTC).toInstant()
            : null;

    BigDecimal totalRevenue = incomeMoneyRepository.sumIncomes(jobId, from, to);
    BigDecimal totalExpenses = expenseMoneyRepository.sumExpenses(jobId, from, to);
    BigDecimal grossMargin = totalRevenue.subtract(totalExpenses);

    BigDecimal expectedRevenue = BigDecimal.ZERO;
    List<ExpectedVsActual> expectedVsActual = new ArrayList<>();
    List<Job> jobs = jobRepository.findByCompanyId(companyId);
    for (Job j : jobs) {
      if (j.getExpectedPrice() != null) {
        expectedRevenue = expectedRevenue.add(j.getExpectedPrice());
        BigDecimal actual = incomeMoneyRepository.sumByJobId(j.getId());
        expectedVsActual.add(
            ExpectedVsActual.builder()
                .jobId(j.getId())
                .jobDescription(j.getDescription())
                .expected(j.getExpectedPrice())
                .actual(actual)
                .gap(j.getExpectedPrice().subtract(actual))
                .build());
      }
    }

    BigDecimal totalIncomeAmount =
        incomeMoneyRepository.sumIncomes(
            null, null, Instant.now().plus(365, java.time.temporal.ChronoUnit.DAYS));
    BigDecimal totalReceipts = incomeReceiptRepository.sumAllReceipts();
    BigDecimal receivables = totalIncomeAmount.subtract(totalReceipts);
    if (receivables.compareTo(BigDecimal.ZERO) < 0) receivables = BigDecimal.ZERO;

    List<CashAccountItem> cashAccounts =
        cashAccountRepository.findByCompanyId(companyId).stream()
            .map(
                ca ->
                    CashAccountItem.builder()
                        .accountId(ca.getId())
                        .accountName(ca.getName())
                        .balance(ca.getBalance() != null ? ca.getBalance() : BigDecimal.ZERO)
                        .build())
            .collect(Collectors.toList());

    BigDecimal totalCredits = cashTransactionRepository.sumCredits(dateFrom, dateTo);
    BigDecimal totalDebits = cashTransactionRepository.sumDebits(dateFrom, dateTo);
    CashFlowSummary cashFlow =
        CashFlowSummary.builder()
            .totalCredits(totalCredits)
            .totalDebits(totalDebits)
            .net(totalCredits.subtract(totalDebits))
            .build();

    List<ExpenseByType> expensesByType = buildExpensesByType(jobId, from, to);

    List<BudgetVariance> budgetVsActual =
        budgetLineRepository.findBudgetVsActualByCompany(companyId).stream()
            .map(
                r ->
                    BudgetVariance.builder()
                        .category((String) r[0])
                        .planned((BigDecimal) r[1])
                        .actual((BigDecimal) r[2])
                        .variance(((BigDecimal) r[1]).subtract((BigDecimal) r[2]))
                        .build())
            .collect(Collectors.toList());

    List<ActiveLoanItem> activeLoans =
        loanRepository.findByStatus(Loan.LoanStatus.ACTIVE).stream()
            .map(
                l ->
                    ActiveLoanItem.builder()
                        .loanId(l.getId())
                        .lender(l.getLender())
                        .remainingAmount(l.getAmount())
                        .nextPaymentDate(l.getDueDate() != null ? l.getDueDate().toString() : null)
                        .build())
            .collect(Collectors.toList());

    BigDecimal fixedCostsTotal = companyFixedCostRepository.sumActiveCosts();

    List<JobProfitability> profitabilityByJob = buildJobProfitability(jobs);
    List<JobRevenue> revenueByJob = buildRevenueByJob(jobs);
    List<JobExpense> expenseByJob = buildExpenseByJob(jobs);

    return MonetaryDashboardResponse.builder()
        .totalRevenue(totalRevenue)
        .totalExpenses(totalExpenses)
        .grossMargin(grossMargin)
        .expectedRevenue(expectedRevenue)
        .expectedVsActual(expectedVsActual)
        .receivables(receivables)
        .cashAccounts(cashAccounts)
        .cashFlow(cashFlow)
        .expensesByType(expensesByType)
        .budgetVsActual(budgetVsActual)
        .activeLoans(activeLoans)
        .fixedCostsTotal(fixedCostsTotal)
        .profitabilityByJob(profitabilityByJob)
        .revenueByJob(revenueByJob)
        .expenseByJob(expenseByJob)
        .build();
  }

  public MonetarySummaryResponse getSummary(
      String companyId, String jobId, LocalDate dateFrom, LocalDate dateTo) {
    Instant from =
        dateFrom != null ? dateFrom.atStartOfDay(java.time.ZoneOffset.UTC).toInstant() : null;
    Instant to =
        dateTo != null
            ? dateTo.atTime(23, 59, 59).atZone(java.time.ZoneOffset.UTC).toInstant()
            : null;

    BigDecimal totalRevenue = incomeMoneyRepository.sumIncomes(jobId, from, to);
    BigDecimal totalExpenses = expenseMoneyRepository.sumExpenses(jobId, from, to);
    BigDecimal grossMargin = totalRevenue.subtract(totalExpenses);

    BigDecimal expectedRevenue = BigDecimal.ZERO;
    List<Job> jobs = jobRepository.findByCompanyId(companyId);
    for (Job j : jobs) {
      if (j.getExpectedPrice() != null) {
        expectedRevenue = expectedRevenue.add(j.getExpectedPrice());
      }
    }

    BigDecimal totalIncomeAmount =
        incomeMoneyRepository.sumIncomes(
            null, null, Instant.now().plus(365, java.time.temporal.ChronoUnit.DAYS));
    BigDecimal totalReceipts = incomeReceiptRepository.sumAllReceipts();
    BigDecimal receivables = totalIncomeAmount.subtract(totalReceipts);
    if (receivables.compareTo(BigDecimal.ZERO) < 0) receivables = BigDecimal.ZERO;

    List<MonetarySummaryResponse.CashAccountItem> cashAccounts =
        cashAccountRepository.findByCompanyId(companyId).stream()
            .map(
                ca ->
                    MonetarySummaryResponse.CashAccountItem.builder()
                        .accountId(ca.getId())
                        .accountName(ca.getName())
                        .balance(ca.getBalance() != null ? ca.getBalance() : BigDecimal.ZERO)
                        .build())
            .collect(Collectors.toList());

    BigDecimal totalCredits = cashTransactionRepository.sumCredits(dateFrom, dateTo);
    BigDecimal totalDebits = cashTransactionRepository.sumDebits(dateFrom, dateTo);
    MonetarySummaryResponse.CashFlowSummary cashFlow =
        MonetarySummaryResponse.CashFlowSummary.builder()
            .totalCredits(totalCredits)
            .totalDebits(totalDebits)
            .net(totalCredits.subtract(totalDebits))
            .build();

    BigDecimal fixedCostsTotal = companyFixedCostRepository.sumActiveCosts();

    long activeLoansCount = loanRepository.findByStatus(Loan.LoanStatus.ACTIVE).size();

    return MonetarySummaryResponse.builder()
        .totalRevenue(totalRevenue)
        .totalExpenses(totalExpenses)
        .grossMargin(grossMargin)
        .expectedRevenue(expectedRevenue)
        .receivables(receivables)
        .fixedCostsTotal(fixedCostsTotal)
        .cashFlow(cashFlow)
        .cashAccounts(cashAccounts)
        .activeLoansCount((int) activeLoansCount)
        .build();
  }

  public MonetaryBreakdownResponse getBreakdown(
      String companyId, String jobId, LocalDate dateFrom, LocalDate dateTo) {
    Instant from =
        dateFrom != null ? dateFrom.atStartOfDay(java.time.ZoneOffset.UTC).toInstant() : null;
    Instant to =
        dateTo != null
            ? dateTo.atTime(23, 59, 59).atZone(java.time.ZoneOffset.UTC).toInstant()
            : null;

    List<MonetaryBreakdownResponse.ExpenseByType> expensesByType =
        buildBreakdownExpensesByType(jobId, from, to);

    List<MonetaryBreakdownResponse.BudgetVariance> budgetVsActual =
        budgetLineRepository.findBudgetVsActualByCompany(companyId).stream()
            .map(
                r ->
                    MonetaryBreakdownResponse.BudgetVariance.builder()
                        .category((String) r[0])
                        .planned((BigDecimal) r[1])
                        .actual((BigDecimal) r[2])
                        .variance(((BigDecimal) r[1]).subtract((BigDecimal) r[2]))
                        .build())
            .collect(Collectors.toList());

    List<MonetaryBreakdownResponse.ExpectedVsActual> expectedVsActual = new ArrayList<>();
    List<Job> jobs = jobRepository.findByCompanyId(companyId);
    for (Job j : jobs) {
      if (j.getExpectedPrice() != null) {
        BigDecimal actual = incomeMoneyRepository.sumByJobId(j.getId());
        expectedVsActual.add(
            MonetaryBreakdownResponse.ExpectedVsActual.builder()
                .jobId(j.getId())
                .jobDescription(j.getDescription())
                .expected(j.getExpectedPrice())
                .actual(actual)
                .gap(j.getExpectedPrice().subtract(actual))
                .build());
      }
    }

    List<MonetaryBreakdownResponse.JobRevenue> revenueByJob =
        jobs.stream()
            .map(
                j ->
                    MonetaryBreakdownResponse.JobRevenue.builder()
                        .jobId(j.getId())
                        .jobDescription(j.getDescription())
                        .total(incomeMoneyRepository.sumByJobId(j.getId()))
                        .build())
            .collect(Collectors.toList());

    List<MonetaryBreakdownResponse.JobExpense> expenseByJob =
        jobs.stream()
            .map(
                j ->
                    MonetaryBreakdownResponse.JobExpense.builder()
                        .jobId(j.getId())
                        .jobDescription(j.getDescription())
                        .total(expenseMoneyRepository.sumByJobId(j.getId()))
                        .byType(List.of())
                        .build())
            .collect(Collectors.toList());

    List<MonetaryBreakdownResponse.JobProfitability> profitabilityByJob =
        jobs.stream()
            .map(
                j -> {
                  BigDecimal rev = incomeMoneyRepository.sumByJobId(j.getId());
                  BigDecimal exp = expenseMoneyRepository.sumByJobId(j.getId());
                  BigDecimal profit = rev.subtract(exp);
                  BigDecimal margin =
                      rev.compareTo(BigDecimal.ZERO) > 0
                          ? profit
                              .divide(rev, 4, RoundingMode.HALF_UP)
                              .multiply(BigDecimal.valueOf(100))
                          : BigDecimal.ZERO;
                  return MonetaryBreakdownResponse.JobProfitability.builder()
                      .jobId(j.getId())
                      .jobDescription(j.getDescription())
                      .revenue(rev)
                      .expense(exp)
                      .profit(profit)
                      .margin(margin)
                      .build();
                })
            .collect(Collectors.toList());

    List<MonetaryBreakdownResponse.ActiveLoanItem> activeLoans =
        loanRepository.findByStatus(Loan.LoanStatus.ACTIVE).stream()
            .map(
                l ->
                    MonetaryBreakdownResponse.ActiveLoanItem.builder()
                        .loanId(l.getId())
                        .lender(l.getLender())
                        .remainingAmount(l.getAmount())
                        .nextPaymentDate(l.getDueDate() != null ? l.getDueDate().toString() : null)
                        .build())
            .collect(Collectors.toList());

    return MonetaryBreakdownResponse.builder()
        .expensesByType(expensesByType)
        .budgetVsActual(budgetVsActual)
        .expectedVsActual(expectedVsActual)
        .revenueByJob(revenueByJob)
        .expenseByJob(expenseByJob)
        .profitabilityByJob(profitabilityByJob)
        .activeLoans(activeLoans)
        .build();
  }

  private List<MonetaryBreakdownResponse.ExpenseByType> buildBreakdownExpensesByType(
      String jobId, Instant dateFrom, Instant dateTo) {
    List<MonetaryBreakdownResponse.ExpenseByType> result = new ArrayList<>();
    result.add(
        MonetaryBreakdownResponse.ExpenseByType.builder()
            .type("BANK")
            .total(expenseMoneyRepository.sumBankExpenses(jobId, dateFrom, dateTo))
            .build());
    result.add(
        MonetaryBreakdownResponse.ExpenseByType.builder()
            .type("PURCHASE")
            .total(expenseMoneyRepository.sumPurchaseExpenses(jobId, dateFrom, dateTo))
            .build());
    result.add(
        MonetaryBreakdownResponse.ExpenseByType.builder()
            .type("TRANSPORT")
            .total(expenseMoneyRepository.sumTravelExpenses(jobId, dateFrom, dateTo))
            .build());
    result.add(
        MonetaryBreakdownResponse.ExpenseByType.builder()
            .type("LABOR")
            .total(expenseMoneyRepository.sumLaborExpenses(jobId, dateFrom, dateTo))
            .build());
    return result;
  }

  private List<MonetaryDashboardResponse.ExpenseByType> buildExpensesByType(
      String jobId, Instant dateFrom, Instant dateTo) {
    List<ExpenseByType> result = new ArrayList<>();
    result.add(
        ExpenseByType.builder()
            .type("BANK")
            .total(expenseMoneyRepository.sumBankExpenses(jobId, dateFrom, dateTo))
            .build());
    result.add(
        ExpenseByType.builder()
            .type("PURCHASE")
            .total(expenseMoneyRepository.sumPurchaseExpenses(jobId, dateFrom, dateTo))
            .build());
    result.add(
        ExpenseByType.builder()
            .type("TRANSPORT")
            .total(expenseMoneyRepository.sumTravelExpenses(jobId, dateFrom, dateTo))
            .build());
    result.add(
        ExpenseByType.builder()
            .type("LABOR")
            .total(expenseMoneyRepository.sumLaborExpenses(jobId, dateFrom, dateTo))
            .build());
    return result;
  }

  private List<JobProfitability> buildJobProfitability(List<Job> jobs) {
    return jobs.stream()
        .map(
            j -> {
              BigDecimal rev = incomeMoneyRepository.sumByJobId(j.getId());
              BigDecimal exp = expenseMoneyRepository.sumByJobId(j.getId());
              BigDecimal profit = rev.subtract(exp);
              BigDecimal margin =
                  rev.compareTo(BigDecimal.ZERO) > 0
                      ? profit
                          .divide(rev, 4, RoundingMode.HALF_UP)
                          .multiply(BigDecimal.valueOf(100))
                      : BigDecimal.ZERO;
              return JobProfitability.builder()
                  .jobId(j.getId())
                  .jobDescription(j.getDescription())
                  .revenue(rev)
                  .expense(exp)
                  .profit(profit)
                  .margin(margin)
                  .build();
            })
        .collect(Collectors.toList());
  }

  private List<JobRevenue> buildRevenueByJob(List<Job> jobs) {
    return jobs.stream()
        .map(
            j ->
                JobRevenue.builder()
                    .jobId(j.getId())
                    .jobDescription(j.getDescription())
                    .total(incomeMoneyRepository.sumByJobId(j.getId()))
                    .build())
        .collect(Collectors.toList());
  }

  private List<JobExpense> buildExpenseByJob(List<Job> jobs) {
    return jobs.stream()
        .map(
            j ->
                JobExpense.builder()
                    .jobId(j.getId())
                    .jobDescription(j.getDescription())
                    .total(expenseMoneyRepository.sumByJobId(j.getId()))
                    .build())
        .collect(Collectors.toList());
  }
}
