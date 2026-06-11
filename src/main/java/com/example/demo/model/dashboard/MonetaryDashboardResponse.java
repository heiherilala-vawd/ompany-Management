package com.example.demo.model.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonetaryDashboardResponse {
  @JsonProperty("total_revenue")
  private BigDecimal totalRevenue;

  @JsonProperty("total_expenses")
  private BigDecimal totalExpenses;

  @JsonProperty("gross_margin")
  private BigDecimal grossMargin;

  @JsonProperty("expected_revenue")
  private BigDecimal expectedRevenue;

  @JsonProperty("expected_vs_actual")
  private List<ExpectedVsActual> expectedVsActual;

  @JsonProperty("receivables")
  private BigDecimal receivables;

  @JsonProperty("cash_accounts")
  private List<CashAccountItem> cashAccounts;

  @JsonProperty("cash_flow")
  private CashFlowSummary cashFlow;

  @JsonProperty("expenses_by_type")
  private List<ExpenseByType> expensesByType;

  @JsonProperty("budget_vs_actual")
  private List<BudgetVariance> budgetVsActual;

  @JsonProperty("active_loans")
  private List<ActiveLoanItem> activeLoans;

  @JsonProperty("fixed_costs_total")
  private BigDecimal fixedCostsTotal;

  @JsonProperty("profitability_by_job")
  private List<JobProfitability> profitabilityByJob;

  @JsonProperty("revenue_by_job")
  private List<JobRevenue> revenueByJob;

  @JsonProperty("expense_by_job")
  private List<JobExpense> expenseByJob;

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ExpectedVsActual {
    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("job_description")
    private String jobDescription;

    @JsonProperty("expected")
    private BigDecimal expected;

    @JsonProperty("actual")
    private BigDecimal actual;

    @JsonProperty("gap")
    private BigDecimal gap;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class CashAccountItem {
    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("account_name")
    private String accountName;

    @JsonProperty("balance")
    private BigDecimal balance;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class CashFlowSummary {
    @JsonProperty("total_credits")
    private BigDecimal totalCredits;

    @JsonProperty("total_debits")
    private BigDecimal totalDebits;

    @JsonProperty("net")
    private BigDecimal net;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ExpenseByType {
    @JsonProperty("type")
    private String type;

    @JsonProperty("total")
    private BigDecimal total;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class BudgetVariance {
    @JsonProperty("category")
    private String category;

    @JsonProperty("planned")
    private BigDecimal planned;

    @JsonProperty("actual")
    private BigDecimal actual;

    @JsonProperty("variance")
    private BigDecimal variance;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ActiveLoanItem {
    @JsonProperty("loan_id")
    private String loanId;

    @JsonProperty("organization_name")
    private String organizationName;

    @JsonProperty("remaining_amount")
    private BigDecimal remainingAmount;

    @JsonProperty("next_payment_date")
    private String nextPaymentDate;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JobProfitability {
    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("job_description")
    private String jobDescription;

    @JsonProperty("revenue")
    private BigDecimal revenue;

    @JsonProperty("expense")
    private BigDecimal expense;

    @JsonProperty("profit")
    private BigDecimal profit;

    @JsonProperty("margin")
    private BigDecimal margin;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JobRevenue {
    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("job_description")
    private String jobDescription;

    @JsonProperty("total")
    private BigDecimal total;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JobExpense {
    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("job_description")
    private String jobDescription;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("by_type")
    private List<ExpenseByType> byType;
  }
}
