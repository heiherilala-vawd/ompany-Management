package com.example.demo.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.demo.model.dashboard.MaterialBreakdownResponse;
import com.example.demo.model.dashboard.MaterialSummaryResponse;
import com.example.demo.model.movement.Material;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.movement.MaterialConsumptionRepository;
import com.example.demo.repository.movement.MaterialRepository;
import com.example.demo.repository.movement.MaterialWarehouseRepository;
import com.example.demo.service.dashboard.DashboardMaterialService;
import com.example.demo.service.utils.SpecialWarehouseUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardMaterialServiceTest {

  @Mock private MaterialWarehouseRepository materialWarehouseRepository;
  @Mock private MaterialConsumptionRepository materialConsumptionRepository;
  @Mock private MaterialRepository materialRepository;

  @InjectMocks private DashboardMaterialService service;

  @Test
  void getSummary_should_return_zero_when_no_data() {
    try (MockedStatic<SpecialWarehouseUtils> utils = mockStatic(SpecialWarehouseUtils.class)) {
      utils.when(SpecialWarehouseUtils::routeWarehouseId).thenReturn("r");
      utils.when(SpecialWarehouseUtils::atSellerWarehouseId).thenReturn("s");
      utils.when(SpecialWarehouseUtils::unfindableWarehouseId).thenReturn("u");
      utils.when(SpecialWarehouseUtils::usedWarehouseId).thenReturn("x");

      when(materialWarehouseRepository.sumStockValueExcludingWarehouses(anyList()))
          .thenReturn(BigDecimal.ZERO);
      when(materialConsumptionRepository.sumConsumptionCost(any(), any(), any()))
          .thenReturn(BigDecimal.ZERO);
      when(materialRepository.count()).thenReturn(0L);
      when(materialRepository.findByExpiryDateBefore(any())).thenReturn(List.of());

      MaterialSummaryResponse result = service.getSummary(null, null, null);

      assertEquals(BigDecimal.ZERO, result.getStockValueTotal());
      assertEquals(BigDecimal.ZERO, result.getConsumptionCostTotal());
      assertEquals(0, result.getTotalMaterialsCount().intValue());
      assertEquals(0, result.getExpiringCount().intValue());
    }
  }

  @Test
  void getSummary_should_count_expiring_materials() {
    try (MockedStatic<SpecialWarehouseUtils> utils = mockStatic(SpecialWarehouseUtils.class)) {
      utils.when(SpecialWarehouseUtils::routeWarehouseId).thenReturn("r");
      utils.when(SpecialWarehouseUtils::atSellerWarehouseId).thenReturn("s");
      utils.when(SpecialWarehouseUtils::unfindableWarehouseId).thenReturn("u");
      utils.when(SpecialWarehouseUtils::usedWarehouseId).thenReturn("x");

      when(materialWarehouseRepository.sumStockValueExcludingWarehouses(anyList()))
          .thenReturn(BigDecimal.ZERO);
      when(materialConsumptionRepository.sumConsumptionCost(any(), any(), any()))
          .thenReturn(BigDecimal.ZERO);
      when(materialRepository.count()).thenReturn(1L);

      Warehouse w = Warehouse.builder().id("w1").name("Warehouse 1").build();
      MaterialWarehouse mw = MaterialWarehouse.builder().warehouse(w).quantity(10).build();
      Material m =
          Material.builder()
              .id("m1")
              .name("Ciment")
              .expiryDate(LocalDate.now().plusDays(15))
              .materialWarehouses(List.of(mw))
              .build();

      when(materialRepository.findByExpiryDateBefore(any())).thenReturn(List.of(m));

      MaterialSummaryResponse result = service.getSummary(null, null, null);

      assertEquals(1, result.getTotalMaterialsCount().intValue());
      assertEquals(1, result.getExpiringCount().intValue());
    }
  }

  @Test
  void getSummary_should_exclude_special_warehouses_from_expiring() {
    try (MockedStatic<SpecialWarehouseUtils> utils = mockStatic(SpecialWarehouseUtils.class)) {
      utils.when(SpecialWarehouseUtils::routeWarehouseId).thenReturn("r");
      utils.when(SpecialWarehouseUtils::atSellerWarehouseId).thenReturn("s");
      utils.when(SpecialWarehouseUtils::unfindableWarehouseId).thenReturn("u");
      utils.when(SpecialWarehouseUtils::usedWarehouseId).thenReturn("x");

      when(materialWarehouseRepository.sumStockValueExcludingWarehouses(anyList()))
          .thenReturn(BigDecimal.ZERO);
      when(materialConsumptionRepository.sumConsumptionCost(any(), any(), any()))
          .thenReturn(BigDecimal.ZERO);
      when(materialRepository.count()).thenReturn(1L);

      Warehouse route = Warehouse.builder().id("r").name("En route").build();
      MaterialWarehouse mw = MaterialWarehouse.builder().warehouse(route).quantity(10).build();
      Material m =
          Material.builder()
              .id("m1")
              .name("Ciment")
              .expiryDate(LocalDate.now().plusDays(15))
              .materialWarehouses(List.of(mw))
              .build();

      when(materialRepository.findByExpiryDateBefore(any())).thenReturn(List.of(m));

      MaterialSummaryResponse result = service.getSummary(null, null, null);

      assertEquals(1, result.getTotalMaterialsCount().intValue());
      assertEquals(0, result.getExpiringCount().intValue());
    }
  }

  @Test
  void getBreakdown_should_return_structured_data() {
    try (MockedStatic<SpecialWarehouseUtils> utils = mockStatic(SpecialWarehouseUtils.class)) {
      utils.when(SpecialWarehouseUtils::routeWarehouseId).thenReturn("r");
      utils.when(SpecialWarehouseUtils::atSellerWarehouseId).thenReturn("s");
      utils.when(SpecialWarehouseUtils::unfindableWarehouseId).thenReturn("u");
      utils.when(SpecialWarehouseUtils::usedWarehouseId).thenReturn("x");

      when(materialWarehouseRepository.findTopStockValue(anyList()))
          .thenReturn(java.util.Collections.emptyList());
      when(materialConsumptionRepository.findTopConsumptionCost(any(), any(), any()))
          .thenReturn(java.util.Collections.emptyList());
      when(materialWarehouseRepository.findStockByMaterial(anyList()))
          .thenReturn(java.util.Collections.emptyList());
      when(materialConsumptionRepository.findConsumptionByMaterial(any(), any(), any()))
          .thenReturn(java.util.Collections.emptyList());
      when(materialRepository.findByExpiryDateBefore(any()))
          .thenReturn(java.util.Collections.emptyList());

      MaterialBreakdownResponse result = service.getBreakdown(null, null, null);

      assertNotNull(result.getTop5StockValue());
      assertNotNull(result.getTop5ConsumptionCost());
      assertNotNull(result.getStockByMaterial());
      assertNotNull(result.getConsumptionByMaterial());
      assertNotNull(result.getStockValueByWarehouse());
      assertNotNull(result.getExpiringMaterials());
    }
  }

  @Test
  void getBreakdown_should_aggregate_stock_by_warehouse() {
    try (MockedStatic<SpecialWarehouseUtils> utils = mockStatic(SpecialWarehouseUtils.class)) {
      utils.when(SpecialWarehouseUtils::routeWarehouseId).thenReturn("r");
      utils.when(SpecialWarehouseUtils::atSellerWarehouseId).thenReturn("s");
      utils.when(SpecialWarehouseUtils::unfindableWarehouseId).thenReturn("u");
      utils.when(SpecialWarehouseUtils::usedWarehouseId).thenReturn("x");

      when(materialWarehouseRepository.findTopStockValue(anyList())).thenReturn(List.of());
      when(materialConsumptionRepository.findTopConsumptionCost(any(), any(), any()))
          .thenReturn(List.of());
      when(materialRepository.findByExpiryDateBefore(any())).thenReturn(List.of());

      Object[] row1 =
          new Object[] {"m1", "Ciment", "SAC", "w1", "Nord", 100, BigDecimal.valueOf(500000)};
      when(materialWarehouseRepository.findStockByMaterial(anyList()))
          .thenReturn(java.util.Collections.singletonList(row1));
      when(materialConsumptionRepository.findConsumptionByMaterial(any(), any(), any()))
          .thenReturn(java.util.Collections.emptyList());

      MaterialBreakdownResponse result = service.getBreakdown(null, null, null);

      assertFalse(result.getStockByMaterial().isEmpty());
      assertFalse(result.getStockValueByWarehouse().isEmpty());
      assertEquals("Nord", result.getStockValueByWarehouse().get(0).getWarehouse());
      assertEquals(
          new BigDecimal("500000"), result.getStockValueByWarehouse().get(0).getTotalValue());
    }
  }
}
