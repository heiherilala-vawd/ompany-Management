package com.example.demo.service.dashboard;

import com.example.demo.model.dashboard.EquipmentBreakdownResponse;
import com.example.demo.model.dashboard.EquipmentDashboardResponse;
import com.example.demo.model.dashboard.EquipmentDashboardResponse.CategoryCount;
import com.example.demo.model.dashboard.EquipmentDashboardResponse.EquipmentMaintenanceCost;
import com.example.demo.model.dashboard.EquipmentDashboardResponse.JobUsage;
import com.example.demo.model.dashboard.EquipmentDashboardResponse.LeasedEquipmentItem;
import com.example.demo.model.dashboard.EquipmentDashboardResponse.ScheduledMaintenanceItem;
import com.example.demo.model.dashboard.EquipmentSummaryResponse;
import com.example.demo.model.movement.Equipment;
import com.example.demo.repository.movement.EquipmentRepository;
import com.example.demo.repository.movement.EquipmentUsageRepository;
import com.example.demo.repository.movement.MaintenanceRepository;
import com.example.demo.repository.movement.MaintenanceScheduleRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    return buildDashboard(allEquipment, jobId, null, null);
  }

  public EquipmentDashboardResponse getDashboard(
      String jobId, LocalDate dateFrom, LocalDate dateTo) {
    List<Equipment> allEquipment = equipmentRepository.findAll();
    return buildDashboard(allEquipment, jobId, dateFrom, dateTo);
  }

  public EquipmentSummaryResponse getSummary(String jobId, LocalDate dateFrom, LocalDate dateTo) {
    List<Equipment> allEquipment = equipmentRepository.findAll();

    int totalEquipment = allEquipment.size();
    long brokenCount = allEquipment.stream().filter(Equipment::getIsDamaged).count();
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

    long lostCount =
        allEquipment.stream()
            .filter(
                e ->
                    e.getMaintenances().stream()
                        .anyMatch(
                            m ->
                                m.getDescription() != null && m.getDescription().contains("Perte")))
            .count();

    Instant from = dateFrom != null ? dateFrom.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
    Instant to =
        dateTo != null ? dateTo.atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant() : null;
    BigDecimal totalUsageHours = equipmentUsageRepository.sumUsageHours(jobId, from, to);

    return EquipmentSummaryResponse.builder()
        .totalEquipment(totalEquipment)
        .availableCount(availableCount)
        .brokenCount((int) brokenCount)
        .lostCount((int) lostCount)
        .avgAgeYears(avgAgeYears)
        .totalUsageHours(totalUsageHours)
        .build();
  }

  public EquipmentBreakdownResponse getBreakdown(
      String jobId, LocalDate dateFrom, LocalDate dateTo) {
    List<Equipment> allEquipment = equipmentRepository.findAll();

    List<EquipmentBreakdownResponse.CategoryCount> categories =
        allEquipment.stream()
            .filter(e -> e.getCategory() != null)
            .collect(Collectors.groupingBy(Equipment::getCategory, Collectors.counting()))
            .entrySet()
            .stream()
            .map(
                e ->
                    EquipmentBreakdownResponse.CategoryCount.builder()
                        .category(e.getKey())
                        .count(e.getValue().intValue())
                        .build())
            .collect(Collectors.toList());

    long brokenCount = allEquipment.stream().filter(Equipment::getIsDamaged).count();
    long lostCount =
        allEquipment.stream()
            .filter(
                e ->
                    e.getMaintenances().stream()
                        .anyMatch(
                            m ->
                                m.getDescription() != null && m.getDescription().contains("Perte")))
            .count();
    long leasedCount =
        allEquipment.stream().filter(e -> Boolean.TRUE.equals(e.getIsLeased())).count();
    long availableCount = allEquipment.size() - brokenCount - lostCount - leasedCount;
    List<EquipmentBreakdownResponse.StatusCount> statusBreakdown = new ArrayList<>();
    statusBreakdown.add(
        EquipmentBreakdownResponse.StatusCount.builder()
            .status("available")
            .count((int) availableCount)
            .build());
    statusBreakdown.add(
        EquipmentBreakdownResponse.StatusCount.builder()
            .status("broken")
            .count((int) brokenCount)
            .build());
    statusBreakdown.add(
        EquipmentBreakdownResponse.StatusCount.builder()
            .status("lost")
            .count((int) lostCount)
            .build());
    statusBreakdown.add(
        EquipmentBreakdownResponse.StatusCount.builder()
            .status("leased")
            .count((int) leasedCount)
            .build());

    List<Object[]> maintenanceCostRaw = maintenanceRepository.findTotalCostByEquipment();
    List<EquipmentBreakdownResponse.EquipmentMaintenanceCost> maintenanceCosts =
        maintenanceCostRaw.stream()
            .map(
                r ->
                    EquipmentBreakdownResponse.EquipmentMaintenanceCost.builder()
                        .equipmentId((String) r[0])
                        .equipmentName((String) r[1])
                        .totalCost((BigDecimal) r[2])
                        .count(((Number) r[3]).intValue())
                        .build())
            .collect(Collectors.toList());

    List<EquipmentBreakdownResponse.JobUsage> usageByJob =
        equipmentUsageRepository.findUsageHoursByJob().stream()
            .map(
                r ->
                    EquipmentBreakdownResponse.JobUsage.builder()
                        .jobId((String) r[0])
                        .jobDescription((String) r[1])
                        .hoursUsed((BigDecimal) r[2])
                        .build())
            .collect(Collectors.toList());

    List<EquipmentBreakdownResponse.ScheduledMaintenanceItem> scheduled =
        maintenanceScheduleRepository
            .findByCompanyIdAndScheduledDateAfter("", LocalDate.now())
            .stream()
            .map(
                ms ->
                    EquipmentBreakdownResponse.ScheduledMaintenanceItem.builder()
                        .equipmentId(ms.getEquipment().getId())
                        .equipmentName(ms.getEquipment().getName())
                        .description(ms.getDescription())
                        .scheduledDate(ms.getScheduledDate())
                        .build())
            .collect(Collectors.toList());

    List<EquipmentBreakdownResponse.LeasedEquipmentItem> leased =
        allEquipment.stream()
            .filter(e -> Boolean.TRUE.equals(e.getIsLeased()))
            .map(
                e ->
                    EquipmentBreakdownResponse.LeasedEquipmentItem.builder()
                        .equipmentId(e.getId())
                        .equipmentName(e.getName())
                        .leaseEndDate(e.getLeaseEndDate())
                        .build())
            .collect(Collectors.toList());

    return EquipmentBreakdownResponse.builder()
        .categoryDistribution(categories)
        .statusBreakdown(statusBreakdown)
        .usageByJob(usageByJob)
        .maintenanceCostByEquipment(maintenanceCosts)
        .leasedEquipment(leased)
        .scheduledMaintenances(scheduled)
        .build();
  }

  private EquipmentDashboardResponse buildDashboard(
      List<Equipment> allEquipment, String jobId, LocalDate dateFrom, LocalDate dateTo) {
    int totalEquipment = allEquipment.size();
    long brokenCount = allEquipment.stream().filter(Equipment::getIsDamaged).count();
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

    Instant from = dateFrom != null ? dateFrom.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
    Instant to =
        dateTo != null ? dateTo.atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant() : null;
    BigDecimal totalUsageHours = equipmentUsageRepository.sumUsageHours(jobId, from, to);
    BigDecimal usageRate = totalUsageHours;

    List<ScheduledMaintenanceItem> scheduled =
        maintenanceScheduleRepository
            .findByCompanyIdAndScheduledDateAfter("", LocalDate.now())
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
