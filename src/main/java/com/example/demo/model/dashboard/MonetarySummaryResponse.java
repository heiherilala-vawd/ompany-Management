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
public class MonetarySummaryResponse {
  @JsonProperty("total_revenue")
  private BigDecimal totalRevenue;

  @JsonProperty("total_expenses")
  private BigDecimal totalExpenses;

  @JsonProperty("gross_margin")
  private BigDecimal grossMargin;

  @JsonProperty("expected_revenue")
  private BigDecimal expectedRevenue;

  @JsonProperty("receivables")
  private BigDecimal receivables;

  @JsonProperty("fixed_costs_total")
  private BigDecimal fixedCostsTotal;

  @JsonProperty("cash_flow")
  private CashFlowSummary cashFlow;

  @JsonProperty("cash_accounts")
  private List<CashAccountItem> cashAccounts;

  @JsonProperty("active_loans_count")
  private Integer activeLoansCount;

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
  public static class CashAccountItem {
    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("account_name")
    private String accountName;

    @JsonProperty("balance")
    private BigDecimal balance;
  }
}
