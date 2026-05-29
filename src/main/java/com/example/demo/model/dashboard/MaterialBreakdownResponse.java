package com.example.demo.model.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class MaterialBreakdownResponse {
  @JsonProperty("top5_stock_value")
  private List<MaterialValueItem> top5StockValue;

  @JsonProperty("top5_consumption_cost")
  private List<MaterialValueItem> top5ConsumptionCost;

  @JsonProperty("stock_by_material")
  private List<MaterialStockItem> stockByMaterial;

  @JsonProperty("consumption_by_material")
  private List<MaterialConsumptionItem> consumptionByMaterial;

  @JsonProperty("stock_value_by_warehouse")
  private List<WarehouseStockValue> stockValueByWarehouse;

  @JsonProperty("expiring_materials")
  private List<ExpiringMaterialItem> expiringMaterials;

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class MaterialValueItem {
    @JsonProperty("material_id")
    private String materialId;

    @JsonProperty("material_name")
    private String materialName;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("value")
    private BigDecimal value;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class MaterialStockItem {
    @JsonProperty("material_id")
    private String materialId;

    @JsonProperty("material_name")
    private String materialName;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("stock_value")
    private BigDecimal stockValue;

    @JsonProperty("warehouse")
    private String warehouse;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class MaterialConsumptionItem {
    @JsonProperty("material_id")
    private String materialId;

    @JsonProperty("material_name")
    private String materialName;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("cost")
    private BigDecimal cost;

    @JsonProperty("period")
    private String period;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class WarehouseStockValue {
    @JsonProperty("warehouse")
    private String warehouse;

    @JsonProperty("total_value")
    private BigDecimal totalValue;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ExpiringMaterialItem {
    @JsonProperty("material_id")
    private String materialId;

    @JsonProperty("material_name")
    private String materialName;

    @JsonProperty("expiry_date")
    private LocalDate expiryDate;

    @JsonProperty("stock_quantity")
    private Integer stockQuantity;

    @JsonProperty("warehouse")
    private String warehouse;
  }
}
