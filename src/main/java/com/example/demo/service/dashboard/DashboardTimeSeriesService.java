package com.example.demo.service.dashboard;

import com.example.demo.model.dashboard.TimeSeriesResponse;
import com.example.demo.model.dashboard.TimeSeriesResponse.Interval;
import com.example.demo.model.dashboard.TimeSeriesResponse.Period;
import com.example.demo.repository.money.CashTransactionRepository;
import com.example.demo.repository.money.ExpenseMoneyRepository;
import com.example.demo.repository.money.IncomeMoneyRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardTimeSeriesService {

  private final IncomeMoneyRepository incomeMoneyRepository;
  private final ExpenseMoneyRepository expenseMoneyRepository;
  private final CashTransactionRepository cashTransactionRepository;

  public TimeSeriesResponse revenue(
      String companyId, String jobId, LocalDate dateFrom, LocalDate dateTo, String granularity) {
    Instant from = toInstant(dateFrom);
    Instant to = toInstantEnd(dateTo);
    String format = toSqlFormat(granularity);

    List<Object[]> raw = incomeMoneyRepository.findIncomesByPeriod(jobId, from, to, format);
    return buildResponse("revenue", dateFrom, dateTo, granularity, raw, jobId);
  }

  public TimeSeriesResponse expenses(
      String companyId, String jobId, LocalDate dateFrom, LocalDate dateTo, String granularity) {
    Instant from = toInstant(dateFrom);
    Instant to = toInstantEnd(dateTo);
    String format = toSqlFormat(granularity);

    List<Object[]> raw = expenseMoneyRepository.findExpensesByPeriod(jobId, from, to, format);
    return buildResponse("expenses", dateFrom, dateTo, granularity, raw, jobId);
  }

  public TimeSeriesResponse cashflow(
      String companyId, LocalDate dateFrom, LocalDate dateTo, String granularity) {
    String format = toSqlFormat(granularity);
    List<Object[]> raw = cashTransactionRepository.findCashFlowByPeriod(dateFrom, dateTo, format);

    List<Interval> intervals = new ArrayList<>();
    BigDecimal cumulative = BigDecimal.ZERO;
    List<BigDecimal> cumList = new ArrayList<>();
    for (Object[] row : raw) {
      BigDecimal credits = (BigDecimal) row[1];
      BigDecimal debits = (BigDecimal) row[2];
      BigDecimal net = credits.subtract(debits);
      cumulative = cumulative.add(net);
      intervals.add(Interval.builder().label((String) row[0]).value(net).build());
      cumList.add(cumulative);
    }
    BigDecimal total =
        intervals.stream().map(Interval::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);

    return TimeSeriesResponse.builder()
        .schema("cashflow")
        .period(Period.builder().from(fromStr(dateFrom)).to(fromStr(dateTo)).build())
        .granularity(granularity)
        .intervals(intervals)
        .cumulative(cumList)
        .total(total)
        .build();
  }

  public TimeSeriesResponse profit(
      String companyId, String jobId, LocalDate dateFrom, LocalDate dateTo, String granularity) {
    Instant from = toInstant(dateFrom);
    Instant to = toInstantEnd(dateTo);
    String format = toSqlFormat(granularity);

    List<Object[]> incomes = incomeMoneyRepository.findIncomesByPeriod(jobId, from, to, format);
    List<Object[]> expens = expenseMoneyRepository.findExpensesByPeriod(jobId, from, to, format);

    var incomeMap =
        incomes.stream().collect(Collectors.toMap(r -> (String) r[0], r -> (BigDecimal) r[1]));
    var expenseMap =
        expens.stream().collect(Collectors.toMap(r -> (String) r[0], r -> (BigDecimal) r[1]));

    List<Interval> intervals = new ArrayList<>();
    BigDecimal cumulative = BigDecimal.ZERO;
    List<BigDecimal> cumList = new ArrayList<>();
    for (String label : incomeMap.keySet()) {
      BigDecimal rev = incomeMap.getOrDefault(label, BigDecimal.ZERO);
      BigDecimal exp = expenseMap.getOrDefault(label, BigDecimal.ZERO);
      BigDecimal net = rev.subtract(exp);
      cumulative = cumulative.add(net);
      intervals.add(Interval.builder().label(label).value(net).build());
      cumList.add(cumulative);
    }
    BigDecimal total =
        intervals.stream().map(Interval::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);

    return TimeSeriesResponse.builder()
        .schema("profit")
        .period(Period.builder().from(fromStr(dateFrom)).to(fromStr(dateTo)).build())
        .granularity(granularity)
        .intervals(intervals)
        .cumulative(cumList)
        .total(total)
        .build();
  }

  public TimeSeriesResponse receivables(
      String companyId, LocalDate dateFrom, LocalDate dateTo, String granularity) {
    return TimeSeriesResponse.builder()
        .schema("receivables")
        .period(Period.builder().from(fromStr(dateFrom)).to(fromStr(dateTo)).build())
        .granularity(granularity)
        .intervals(List.of())
        .total(BigDecimal.ZERO)
        .build();
  }

  public TimeSeriesResponse budget(
      String companyId, LocalDate dateFrom, LocalDate dateTo, String granularity) {
    return TimeSeriesResponse.builder()
        .schema("budget")
        .period(Period.builder().from(fromStr(dateFrom)).to(fromStr(dateTo)).build())
        .granularity(granularity)
        .intervals(List.of())
        .total(BigDecimal.ZERO)
        .build();
  }

  public TimeSeriesResponse expenseBreakdown(
      String companyId, LocalDate dateFrom, LocalDate dateTo, String granularity) {
    return TimeSeriesResponse.builder()
        .schema("expense_breakdown")
        .period(Period.builder().from(fromStr(dateFrom)).to(fromStr(dateTo)).build())
        .granularity(granularity)
        .intervals(List.of())
        .total(BigDecimal.ZERO)
        .build();
  }

  private TimeSeriesResponse buildResponse(
      String schema,
      LocalDate dateFrom,
      LocalDate dateTo,
      String granularity,
      List<Object[]> raw,
      String jobId) {
    List<Interval> intervals = new ArrayList<>();
    BigDecimal cumulative = BigDecimal.ZERO;
    List<BigDecimal> cumList = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;
    for (Object[] row : raw) {
      BigDecimal val = (BigDecimal) row[1];
      cumulative = cumulative.add(val);
      total = total.add(val);
      intervals.add(Interval.builder().label((String) row[0]).value(val).build());
      cumList.add(cumulative);
    }
    return TimeSeriesResponse.builder()
        .schema(schema)
        .period(Period.builder().from(fromStr(dateFrom)).to(fromStr(dateTo)).build())
        .granularity(granularity)
        .intervals(intervals)
        .cumulative(cumList)
        .total(total)
        .filteredByJob(jobId)
        .build();
  }

  private static Instant toInstant(LocalDate date) {
    return date != null ? date.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
  }

  private static Instant toInstantEnd(LocalDate date) {
    return date != null ? date.atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant() : null;
  }

  private static String fromStr(LocalDate d) {
    return d != null ? d.toString() : null;
  }

  private static String toSqlFormat(String granularity) {
    if (granularity == null) return "YYYY-MM";
    return switch (granularity) {
      case "day", "1day" -> "YYYY-MM-DD";
      case "2day" -> "YYYY-MM-DD";
      case "week" -> "YYYY-IW";
      case "month" -> "YYYY-MM";
      case "quarter" -> "YYYY-Q";
      case "year" -> "YYYY";
      default -> "YYYY-MM";
    };
  }
}
