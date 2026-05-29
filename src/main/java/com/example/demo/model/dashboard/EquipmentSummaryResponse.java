package com.example.demo.model.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
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
public class EquipmentSummaryResponse {
  @JsonProperty("total_equipment")
  private Integer totalEquipment;

  @JsonProperty("available_count")
  private Integer availableCount;

  @JsonProperty("broken_count")
  private Integer brokenCount;

  @JsonProperty("lost_count")
  private Integer lostCount;

  @JsonProperty("avg_age_years")
  private BigDecimal avgAgeYears;

  @JsonProperty("total_usage_hours")
  private BigDecimal totalUsageHours;
}
