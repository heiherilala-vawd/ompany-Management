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
public class EquipmentBreakdownResponse {
  @JsonProperty("category_distribution")
  private List<CategoryCount> categoryDistribution;

  @JsonProperty("status_breakdown")
  private List<StatusCount> statusBreakdown;

  @JsonProperty("usage_by_job")
  private List<JobUsage> usageByJob;

  @JsonProperty("maintenance_cost_by_equipment")
  private List<EquipmentMaintenanceCost> maintenanceCostByEquipment;

  @JsonProperty("leased_equipment")
  private List<LeasedEquipmentItem> leasedEquipment;

  @JsonProperty("scheduled_maintenances")
  private List<ScheduledMaintenanceItem> scheduledMaintenances;

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
  public static class StatusCount {
    @JsonProperty("status")
    private String status;

    @JsonProperty("count")
    private Integer count;
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
}
