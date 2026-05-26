package com.example.demo.service.dashboard;

import com.example.demo.model.dashboard.EquipmentDashboardResponse;
import com.example.demo.model.dashboard.EquipmentDashboardResponse.CategoryCount;
import com.example.demo.model.dashboard.EquipmentDashboardResponse.EquipmentMaintenanceCost;
import com.example.demo.model.dashboard.EquipmentDashboardResponse.JobUsage;
import com.example.demo.model.dashboard.EquipmentDashboardResponse.LeasedEquipmentItem;
import com.example.demo.model.dashboard.EquipmentDashboardResponse.ScheduledMaintenanceItem;
import com.example.demo.model.movement.Equipment;
import com.example.demo.repository.movement.EquipmentRepository;
import com.example.demo.repository.movement.EquipmentUsageRepository;
import com.example.demo.repository.movement.MaintenanceRepository;
import com.example.demo.repository.movement.MaintenanceScheduleRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardEquipmentService {

  private final EquipmentRepository equipmentRepository;
  private final EquipmentUsageRepository equipmentUsageRepository;
  private final MaintenanceRepository maintenanceRepository;
  private final MaintenanceScheduleRepository maintenanceScheduleRepository;

  public EquipmentDashboardResponse getDashboard(String jobId) {
    List<Equipment> allEquipment = equipmentRepository.findAll();

    int totalEquipment = allEquipment.size();
    long brokenCount = allEquipment.stream().filter(Equipment::getEstEnPanne).count();
    int availableCount = totalEquipment - (int) brokenCount;

    BigDecimal avgAgeYears = BigDecimal.ZERO;
    if (!allEquipment.isEmpty()) {
      double avgDays =
          allEquipment.stream()
              .filter(e -> e.getPurchaseDate() != null)
              .mapToLong(e -> ChronoUnit.DAYS.between(e.getPurchaseDate(), LocalDate.now()))
              .average()
              .orElse(0);
      avgAgeYears = BigDecimal.valueOf(avgDays / 365.0).setScale(1, RoundingMode.HALF_UP);
    }

    List<CategoryCount> categories =
        allEquipment.stream()
            .filter(e -> e.getCategory() != null)
            .collect(Collectors.groupingBy(Equipment::getCategory, Collectors.counting()))
            .entrySet()
            .stream()
            .map(
                e ->
                    CategoryCount.builder()
                        .category(e.getKey())
                        .count(e.getValue().intValue())
                        .build())
            .collect(Collectors.toList());

    List<Object[]> maintenanceCostRaw = maintenanceRepository.findTotalCostByEquipment();
    List<EquipmentMaintenanceCost> maintenanceCosts =
        maintenanceCostRaw.stream()
            .map(
                r ->
                    EquipmentMaintenanceCost.builder()
                        .equipmentId((String) r[0])
                        .equipmentName((String) r[1])
                        .totalCost((BigDecimal) r[2])
                        .count(((Number) r[3]).intValue())
                        .build())
            .collect(Collectors.toList());

    BigDecimal totalUsageHours = equipmentUsageRepository.sumUsageHours(jobId);
    BigDecimal usageRate = totalUsageHours;

    List<ScheduledMaintenanceItem> scheduled =
        maintenanceScheduleRepository
            .findByCompanyIdAndScheduledDateAfter(
                "", LocalDate.now()) // filtered by company in controller
            .stream()
            .map(
                ms ->
                    ScheduledMaintenanceItem.builder()
                        .equipmentId(ms.getEquipment().getId())
                        .equipmentName(ms.getEquipment().getName())
                        .description(ms.getDescription())
                        .scheduledDate(ms.getScheduledDate())
                        .build())
            .collect(Collectors.toList());

    List<LeasedEquipmentItem> leased =
        allEquipment.stream()
            .filter(e -> Boolean.TRUE.equals(e.getIsLeased()))
            .map(
                e ->
                    LeasedEquipmentItem.builder()
                        .equipmentId(e.getId())
                        .equipmentName(e.getName())
                        .leaseEndDate(e.getLeaseEndDate())
                        .build())
            .collect(Collectors.toList());

    long lostCount =
        allEquipment.stream()
            .filter(
                e ->
                    e.getMaintenances().stream()
                        .anyMatch(
                            m ->
                                m.getDescription() != null && m.getDescription().contains("Perte")))
            .count();

    List<JobUsage> usageByJob =
        equipmentUsageRepository.findUsageHoursByJob().stream()
            .map(
                r ->
                    JobUsage.builder()
                        .jobId((String) r[0])
                        .jobDescription((String) r[1])
                        .hoursUsed((BigDecimal) r[2])
                        .build())
            .collect(Collectors.toList());

    return EquipmentDashboardResponse.builder()
        .totalEquipment(totalEquipment)
        .brokenCount((int) brokenCount)
        .availableCount(availableCount)
        .usageRate(usageRate)
        .avgAgeYears(avgAgeYears)
        .categoryDistribution(categories)
        .maintenanceCostByEquipment(maintenanceCosts)
        .scheduledMaintenances(scheduled)
        .leasedEquipment(leased)
        .lostCount((int) lostCount)
        .usageByJob(usageByJob)
        .build();
  }
}
