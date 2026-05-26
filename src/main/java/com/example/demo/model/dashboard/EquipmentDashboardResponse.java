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
public class EquipmentDashboardResponse {
  @JsonProperty("total_equipment")
  private Integer totalEquipment;

  @JsonProperty("broken_count")
  private Integer brokenCount;

  @JsonProperty("available_count")
  private Integer availableCount;

  @JsonProperty("usage_rate")
  private BigDecimal usageRate;

  @JsonProperty("avg_age_years")
  private BigDecimal avgAgeYears;

  @JsonProperty("category_distribution")
  private List<CategoryCount> categoryDistribution;

  @JsonProperty("maintenance_cost_by_equipment")
  private List<EquipmentMaintenanceCost> maintenanceCostByEquipment;

  @JsonProperty("scheduled_maintenances")
  private List<ScheduledMaintenanceItem> scheduledMaintenances;

  @JsonProperty("leased_equipment")
  private List<LeasedEquipmentItem> leasedEquipment;

  @JsonProperty("lost_count")
  private Integer lostCount;

  @JsonProperty("usage_by_job")
  private List<JobUsage> usageByJob;

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class CategoryCount {
    @JsonProperty("category")
    private String category;

    @JsonProperty("count")
    private Integer count;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class EquipmentMaintenanceCost {
    @JsonProperty("equipment_id")
    private String equipmentId;

    @JsonProperty("equipment_name")
    private String equipmentName;

    @JsonProperty("total_cost")
    private BigDecimal totalCost;

    @JsonProperty("count")
    private Integer count;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ScheduledMaintenanceItem {
    @JsonProperty("equipment_id")
    private String equipmentId;

    @JsonProperty("equipment_name")
    private String equipmentName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("scheduled_date")
    private LocalDate scheduledDate;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class LeasedEquipmentItem {
    @JsonProperty("equipment_id")
    private String equipmentId;

    @JsonProperty("equipment_name")
    private String equipmentName;

    @JsonProperty("lease_end_date")
    private LocalDate leaseEndDate;
  }

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JobUsage {
    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("job_description")
    private String jobDescription;

    @JsonProperty("hours_used")
    private BigDecimal hoursUsed;
  }
}
