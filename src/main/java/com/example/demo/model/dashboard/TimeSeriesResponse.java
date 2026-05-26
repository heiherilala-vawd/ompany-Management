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
public class TimeSeriesResponse {
  @JsonProperty("schema")
  private String schema;

  @JsonProperty("period")
  private Period period;

  @JsonProperty("granularity")
  private String granularity;

  @JsonProperty("intervals")
  private List<Interval> intervals;

  @JsonProperty("cumulative")
  private List<BigDecimal> cumulative;

  @JsonProperty("total")
  private BigDecimal total;

  @JsonProperty("filtered_by_job")
  private String filteredByJob;

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Period {
    @JsonProperty("from")
    private String from;

    @JsonProperty("to")
    private String to;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Interval {
    @JsonProperty("label")
    private String label;

    @JsonProperty("value")
    private BigDecimal value;
  }
}
