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
public class MaterialSummaryResponse {
  @JsonProperty("stock_value_total")
  private BigDecimal stockValueTotal;

  @JsonProperty("consumption_cost_total")
  private BigDecimal consumptionCostTotal;

  @JsonProperty("total_materials_count")
  private Integer totalMaterialsCount;

  @JsonProperty("expiring_count")
  private Integer expiringCount;
}
