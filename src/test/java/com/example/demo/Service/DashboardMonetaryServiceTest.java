package com.example.demo.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.demo.model.Job;
import com.example.demo.model.dashboard.MonetaryBreakdownResponse;
import com.example.demo.model.dashboard.MonetarySummaryResponse;
import com.example.demo.model.money.CashAccount;
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
import com.example.demo.service.dashboard.DashboardMonetaryService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardMonetaryServiceTest {

  @Mock private IncomeMoneyRepository incomeMoneyRepository;
  @Mock private ExpenseMoneyRepository expenseMoneyRepository;
  @Mock private IncomeReceiptRepository incomeReceiptRepository;
  @Mock private CashAccountRepository cashAccountRepository;
  @Mock private CashTransactionRepository cashTransactionRepository;
  @Mock private BudgetLineRepository budgetLineRepository;
  @Mock private LoanRepository loanRepository;
  @Mock private CompanyFixedCostRepository companyFixedCostRepository;
  @Mock private JobRepository jobRepository;

  @InjectMocks private DashboardMonetaryService service;

  @Test
  void getSummary_should_return_zero_when_no_data() {
    when(jobRepository.findByCompanyId("c1")).thenReturn(List.of());
    when(incomeMoneyRepository.sumIncomes(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(expenseMoneyRepository.sumExpenses(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(incomeMoneyRepository.sumIncomes(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(incomeReceiptRepository.sumAllReceipts()).thenReturn(BigDecimal.ZERO);
    when(cashAccountRepository.findByCompanyId("c1")).thenReturn(List.of());
    when(cashTransactionRepository.sumCredits(any(), any())).thenReturn(BigDecimal.ZERO);
    when(cashTransactionRepository.sumDebits(any(), any())).thenReturn(BigDecimal.ZERO);
    when(companyFixedCostRepository.sumActiveCosts()).thenReturn(BigDecimal.ZERO);
    when(loanRepository.findByStatus(Loan.LoanStatus.ACTIVE)).thenReturn(List.of());

    MonetarySummaryResponse result = service.getSummary("c1", null, null, null);

    assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
    assertEquals(BigDecimal.ZERO, result.getTotalExpenses());
    assertEquals(BigDecimal.ZERO, result.getGrossMargin());
    assertEquals(BigDecimal.ZERO, result.getExpectedRevenue());
    assertEquals(BigDecimal.ZERO, result.getReceivables());
    assertEquals(BigDecimal.ZERO, result.getFixedCostsTotal());
    assertEquals(0, result.getActiveLoansCount().intValue());
    assertNotNull(result.getCashFlow());
  }

  @Test
  void getSummary_should_compute_revenue_expenses_and_margin() {
    when(jobRepository.findByCompanyId("c1")).thenReturn(List.of());
    when(incomeMoneyRepository.sumIncomes(any(), any(), any()))
        .thenReturn(new BigDecimal("100000"));
    when(expenseMoneyRepository.sumExpenses(any(), any(), any()))
        .thenReturn(new BigDecimal("40000"));
    when(incomeMoneyRepository.sumIncomes(any(), any(), any()))
        .thenReturn(new BigDecimal("100000"));
    when(incomeReceiptRepository.sumAllReceipts()).thenReturn(BigDecimal.ZERO);
    when(cashAccountRepository.findByCompanyId("c1")).thenReturn(List.of());
    when(cashTransactionRepository.sumCredits(any(), any())).thenReturn(BigDecimal.ZERO);
    when(cashTransactionRepository.sumDebits(any(), any())).thenReturn(BigDecimal.ZERO);
    when(companyFixedCostRepository.sumActiveCosts()).thenReturn(BigDecimal.ZERO);
    when(loanRepository.findByStatus(Loan.LoanStatus.ACTIVE)).thenReturn(List.of());

    MonetarySummaryResponse result = service.getSummary("c1", null, null, null);

    assertEquals(0, new BigDecimal("100000").compareTo(result.getTotalRevenue()));
    assertEquals(0, new BigDecimal("40000").compareTo(result.getTotalExpenses()));
    assertEquals(0, new BigDecimal("60000").compareTo(result.getGrossMargin()));
  }

  @Test
  void getSummary_should_compute_expected_revenue_from_jobs() {
    Job job1 =
        Job.builder().id("j1").description("Job 1").expectedPrice(new BigDecimal("500000")).build();
    Job job2 = Job.builder().id("j2").description("Job 2").expectedPrice(null).build();

    when(jobRepository.findByCompanyId("c1")).thenReturn(List.of(job1, job2));
    when(incomeMoneyRepository.sumIncomes(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(expenseMoneyRepository.sumExpenses(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(incomeMoneyRepository.sumIncomes(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(incomeReceiptRepository.sumAllReceipts()).thenReturn(BigDecimal.ZERO);
    when(cashAccountRepository.findByCompanyId("c1")).thenReturn(List.of());
    when(cashTransactionRepository.sumCredits(any(), any())).thenReturn(BigDecimal.ZERO);
    when(cashTransactionRepository.sumDebits(any(), any())).thenReturn(BigDecimal.ZERO);
    when(companyFixedCostRepository.sumActiveCosts()).thenReturn(BigDecimal.ZERO);
    when(loanRepository.findByStatus(Loan.LoanStatus.ACTIVE)).thenReturn(List.of());

    MonetarySummaryResponse result = service.getSummary("c1", null, null, null);

    assertEquals(0, new BigDecimal("500000").compareTo(result.getExpectedRevenue()));
  }

  @Test
  void getSummary_should_not_go_negative_on_receivables() {
    when(jobRepository.findByCompanyId("c1")).thenReturn(List.of());
    when(incomeMoneyRepository.sumIncomes(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(expenseMoneyRepository.sumExpenses(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(incomeMoneyRepository.sumIncomes(any(), any(), any())).thenReturn(new BigDecimal("1000"));
    when(incomeReceiptRepository.sumAllReceipts()).thenReturn(new BigDecimal("2000"));
    when(cashAccountRepository.findByCompanyId("c1")).thenReturn(List.of());
    when(cashTransactionRepository.sumCredits(any(), any())).thenReturn(BigDecimal.ZERO);
    when(cashTransactionRepository.sumDebits(any(), any())).thenReturn(BigDecimal.ZERO);
    when(companyFixedCostRepository.sumActiveCosts()).thenReturn(BigDecimal.ZERO);
    when(loanRepository.findByStatus(Loan.LoanStatus.ACTIVE)).thenReturn(List.of());

    MonetarySummaryResponse result = service.getSummary("c1", null, null, null);

    assertEquals(BigDecimal.ZERO, result.getReceivables());
  }

  @Test
  void getSummary_should_include_cash_accounts_and_fixed_costs() {
    CashAccount ca =
        CashAccount.builder()
            .id("ca1")
            .name("Compte courant")
            .balance(new BigDecimal("1000000"))
            .build();

    when(jobRepository.findByCompanyId("c1")).thenReturn(List.of());
    when(incomeMoneyRepository.sumIncomes(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(expenseMoneyRepository.sumExpenses(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(incomeMoneyRepository.sumIncomes(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(incomeReceiptRepository.sumAllReceipts()).thenReturn(BigDecimal.ZERO);
    when(cashAccountRepository.findByCompanyId("c1")).thenReturn(List.of(ca));
    when(cashTransactionRepository.sumCredits(any(), any())).thenReturn(new BigDecimal("200000"));
    when(cashTransactionRepository.sumDebits(any(), any())).thenReturn(new BigDecimal("50000"));
    when(companyFixedCostRepository.sumActiveCosts()).thenReturn(new BigDecimal("2500"));
    when(loanRepository.findByStatus(Loan.LoanStatus.ACTIVE))
        .thenReturn(List.of(Loan.builder().id("l1").build()));

    MonetarySummaryResponse result = service.getSummary("c1", null, null, null);

    assertEquals(1, result.getCashAccounts().size());
    assertEquals(0, new BigDecimal("2500").compareTo(result.getFixedCostsTotal()));
    assertEquals(1, result.getActiveLoansCount().intValue());
    assertEquals(0, new BigDecimal("150000").compareTo(result.getCashFlow().getNet()));
  }

  @Test
  void getBreakdown_should_return_all_sections() {
    when(jobRepository.findByCompanyId("c1")).thenReturn(List.of());
    when(expenseMoneyRepository.sumBankExpenses(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(expenseMoneyRepository.sumPurchaseExpenses(any(), any(), any()))
        .thenReturn(BigDecimal.ZERO);
    when(expenseMoneyRepository.sumTravelExpenses(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(expenseMoneyRepository.sumLaborExpenses(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(budgetLineRepository.findBudgetVsActualByCompany("c1")).thenReturn(List.of());
    when(loanRepository.findByStatus(Loan.LoanStatus.ACTIVE)).thenReturn(List.of());

    MonetaryBreakdownResponse result = service.getBreakdown("c1", null, null, null);

    assertEquals(4, result.getExpensesByType().size());
    assertNotNull(result.getBudgetVsActual());
    assertNotNull(result.getExpectedVsActual());
    assertNotNull(result.getRevenueByJob());
    assertNotNull(result.getExpenseByJob());
    assertNotNull(result.getProfitabilityByJob());
    assertNotNull(result.getActiveLoans());
  }

  @Test
  void getBreakdown_should_compute_job_profitability() {
    Job job1 =
        Job.builder().id("j1").description("Job 1").expectedPrice(new BigDecimal("500000")).build();

    when(jobRepository.findByCompanyId("c1")).thenReturn(List.of(job1));
    when(expenseMoneyRepository.sumBankExpenses(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(expenseMoneyRepository.sumPurchaseExpenses(any(), any(), any()))
        .thenReturn(BigDecimal.ZERO);
    when(expenseMoneyRepository.sumTravelExpenses(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(expenseMoneyRepository.sumLaborExpenses(any(), any(), any())).thenReturn(BigDecimal.ZERO);
    when(budgetLineRepository.findBudgetVsActualByCompany("c1")).thenReturn(List.of());
    when(loanRepository.findByStatus(Loan.LoanStatus.ACTIVE)).thenReturn(List.of());
    when(incomeMoneyRepository.sumByJobId("j1")).thenReturn(new BigDecimal("300000"));
    when(expenseMoneyRepository.sumByJobId("j1")).thenReturn(new BigDecimal("100000"));

    MonetaryBreakdownResponse result = service.getBreakdown("c1", null, null, null);

    assertEquals(1, result.getProfitabilityByJob().size());
    assertEquals(
        0, new BigDecimal("200000").compareTo(result.getProfitabilityByJob().get(0).getProfit()));
    assertTrue(result.getProfitabilityByJob().get(0).getMargin().compareTo(BigDecimal.ZERO) > 0);
  }
}
