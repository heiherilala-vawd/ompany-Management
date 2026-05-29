package com.example.demo.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.demo.model.dashboard.EquipmentBreakdownResponse;
import com.example.demo.model.dashboard.EquipmentSummaryResponse;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Maintenance;
import com.example.demo.repository.movement.EquipmentRepository;
import com.example.demo.repository.movement.EquipmentUsageRepository;
import com.example.demo.repository.movement.MaintenanceRepository;
import com.example.demo.repository.movement.MaintenanceScheduleRepository;
import com.example.demo.service.dashboard.DashboardEquipmentService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardEquipmentServiceTest {

  @Mock private EquipmentRepository equipmentRepository;
  @Mock private EquipmentUsageRepository equipmentUsageRepository;
  @Mock private MaintenanceRepository maintenanceRepository;
  @Mock private MaintenanceScheduleRepository maintenanceScheduleRepository;

  @InjectMocks private DashboardEquipmentService service;

  @Test
  void getSummary_should_return_zero_when_no_equipment() {
    when(equipmentRepository.findAll()).thenReturn(java.util.Collections.emptyList());
    when(equipmentUsageRepository.sumUsageHours(any(), any(), any())).thenReturn(BigDecimal.ZERO);

    EquipmentSummaryResponse result = service.getSummary(null, null, null);

    assertEquals(0, result.getTotalEquipment());
    assertEquals(0, result.getAvailableCount());
    assertEquals(0, result.getBrokenCount());
    assertEquals(0, result.getLostCount());
    assertEquals(BigDecimal.ZERO, result.getTotalUsageHours());
    assertEquals(0, BigDecimal.ZERO.compareTo(result.getAvgAgeYears()));
  }

  @Test
  void getSummary_should_count_broken_equipment() {
    Equipment broken = Equipment.builder().id("e1").estEnPanne(true).build();
    Equipment working = Equipment.builder().id("e2").estEnPanne(false).build();

    when(equipmentRepository.findAll()).thenReturn(List.of(broken, working));
    when(equipmentUsageRepository.sumUsageHours(any(), any(), any())).thenReturn(BigDecimal.ZERO);

    EquipmentSummaryResponse result = service.getSummary(null, null, null);

    assertEquals(2, result.getTotalEquipment());
    assertEquals(1, result.getAvailableCount());
    assertEquals(1, result.getBrokenCount());
  }

  @Test
  void getSummary_should_count_via_maintenance() {
    Equipment lost2 = mock(Equipment.class);
    Equipment normal2 = mock(Equipment.class);
    Maintenance perte2 = mock(Maintenance.class);

    when(perte2.getDescription()).thenReturn("Perte materiel");
    when(lost2.getMaintenances()).thenReturn(List.of(perte2));

    when(normal2.getMaintenances()).thenReturn(java.util.Collections.emptyList());

    when(equipmentRepository.findAll()).thenReturn(List.of(lost2, normal2));
    when(equipmentUsageRepository.sumUsageHours(any(), any(), any())).thenReturn(BigDecimal.ZERO);

    EquipmentSummaryResponse result = service.getSummary(null, null, null);

    assertEquals(2, result.getTotalEquipment());
    assertEquals(1, result.getLostCount());
    assertEquals(2, result.getAvailableCount());
    assertEquals(0, result.getBrokenCount());
  }

  @Test
  void getSummary_should_compute_avg_age_from_purchase_date() {
    Equipment old = Equipment.builder().id("e1").purchaseDate(LocalDate.of(2020, 1, 1)).build();

    when(equipmentRepository.findAll()).thenReturn(List.of(old));
    when(equipmentUsageRepository.sumUsageHours(any(), any(), any())).thenReturn(BigDecimal.ZERO);

    EquipmentSummaryResponse result = service.getSummary(null, null, null);

    assertEquals(1, result.getTotalEquipment());
    assertTrue(result.getAvgAgeYears().compareTo(BigDecimal.ZERO) > 0);
  }

  @Test
  void getSummary_should_handle_null_purchase_date() {
    Equipment noDate = Equipment.builder().id("e1").purchaseDate(null).build();

    when(equipmentRepository.findAll()).thenReturn(List.of(noDate));
    when(equipmentUsageRepository.sumUsageHours(any(), any(), any())).thenReturn(BigDecimal.ZERO);

    EquipmentSummaryResponse result = service.getSummary(null, null, null);

    assertEquals(new BigDecimal("0.0"), result.getAvgAgeYears());
  }

  @Test
  void getBreakdown_should_return_all_statuses() {
    Equipment e1 = Equipment.builder().id("e1").category("CHARGER").build();

    when(equipmentRepository.findAll()).thenReturn(List.of(e1));
    when(maintenanceRepository.findTotalCostByEquipment()).thenReturn(List.of());
    when(maintenanceScheduleRepository.findByCompanyIdAndScheduledDateAfter(anyString(), any()))
        .thenReturn(List.of());
    when(equipmentUsageRepository.findUsageHoursByJob()).thenReturn(List.of());

    EquipmentBreakdownResponse result = service.getBreakdown(null, null, null);

    assertEquals(4, result.getStatusBreakdown().size());
    assertEquals(
        1,
        result.getStatusBreakdown().stream()
            .filter(s -> "available".equals(s.getStatus()))
            .mapToInt(EquipmentBreakdownResponse.StatusCount::getCount)
            .sum());
  }

  @Test
  void getBreakdown_should_include_category_distribution() {
    Equipment e1 = Equipment.builder().id("e1").category("ENGIN").build();
    Equipment e2 = Equipment.builder().id("e2").category("ENGIN").build();

    when(equipmentRepository.findAll()).thenReturn(List.of(e1, e2));
    when(maintenanceRepository.findTotalCostByEquipment()).thenReturn(List.of());
    when(maintenanceScheduleRepository.findByCompanyIdAndScheduledDateAfter(anyString(), any()))
        .thenReturn(List.of());
    when(equipmentUsageRepository.findUsageHoursByJob()).thenReturn(List.of());

    EquipmentBreakdownResponse result = service.getBreakdown(null, null, null);

    assertEquals(1, result.getCategoryDistribution().size());
    assertEquals("ENGIN", result.getCategoryDistribution().get(0).getCategory());
    assertEquals(2, result.getCategoryDistribution().get(0).getCount());
  }
}
