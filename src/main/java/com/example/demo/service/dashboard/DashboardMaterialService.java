package com.example.demo.service.dashboard;

import com.example.demo.model.dashboard.MaterialDashboardResponse;
import com.example.demo.model.dashboard.MaterialDashboardResponse.ExpiringMaterialItem;
import com.example.demo.model.dashboard.MaterialDashboardResponse.MaterialConsumptionItem;
import com.example.demo.model.dashboard.MaterialDashboardResponse.MaterialStockItem;
import com.example.demo.model.dashboard.MaterialDashboardResponse.MaterialValueItem;
import com.example.demo.repository.movement.MaterialConsumptionRepository;
import com.example.demo.repository.movement.MaterialRepository;
import com.example.demo.repository.movement.MaterialWarehouseRepository;
import com.example.demo.service.utils.SpecialWarehouseUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardMaterialService {

  private final MaterialWarehouseRepository materialWarehouseRepository;
  private final MaterialConsumptionRepository materialConsumptionRepository;
  private final MaterialRepository materialRepository;

  public MaterialDashboardResponse getDashboard(
      String jobId, LocalDate dateFrom, LocalDate dateTo) {

    List<String> excludeIds = specialWarehouseIds();

    BigDecimal stockValueTotal =
        materialWarehouseRepository.sumStockValueExcludingWarehouses(excludeIds);

    BigDecimal consumptionCostTotal =
        materialConsumptionRepository.sumConsumptionCost(jobId, dateFrom, dateTo);

    List<MaterialValueItem> top5Stock = buildTop5Stock(excludeIds);

    List<Object[]> topConsumptionRaw =
        materialConsumptionRepository.findTopConsumptionCost(jobId, dateFrom, dateTo);
    List<MaterialValueItem> top5Consumption = buildTopConsumption(topConsumptionRaw);

    List<MaterialStockItem> stockByMat = buildStockByMaterial(excludeIds);

    List<Object[]> consumptionRaw =
        materialConsumptionRepository.findConsumptionByMaterial(jobId, dateFrom, dateTo);
    List<MaterialConsumptionItem> consumptionByMat = buildConsumptionByMaterial(consumptionRaw);

    List<ExpiringMaterialItem> expiring = buildExpiringMaterials();

    return MaterialDashboardResponse.builder()
        .stockValueTotal(stockValueTotal)
        .consumptionCostTotal(consumptionCostTotal)
        .top5StockValue(top5Stock)
        .top5ConsumptionCost(top5Consumption)
        .stockByMaterial(stockByMat)
        .consumptionByMaterial(consumptionByMat)
        .expiringMaterials(expiring)
        .build();
  }

  private List<String> specialWarehouseIds() {
    return List.of(
        SpecialWarehouseUtils.routeWarehouseId(),
        SpecialWarehouseUtils.atSellerWarehouseId(),
        SpecialWarehouseUtils.unfindableWarehouseId(),
        SpecialWarehouseUtils.usedWarehouseId());
  }

  private List<MaterialValueItem> buildTop5Stock(List<String> excludeIds) {
    List<Object[]> raw = materialWarehouseRepository.findTopStockValue(excludeIds);
    return raw.stream()
        .limit(5)
        .map(
            r ->
                MaterialValueItem.builder()
                    .materialId((String) r[0])
                    .materialName((String) r[1])
                    .unit(r[2] != null ? r[2].toString() : null)
                    .quantity(((Number) r[3]).intValue())
                    .value((BigDecimal) r[4])
                    .build())
        .collect(Collectors.toList());
  }

  private List<MaterialValueItem> buildTopConsumption(List<Object[]> raw) {
    return raw.stream()
        .limit(5)
        .map(
            r ->
                MaterialValueItem.builder()
                    .materialId((String) r[0])
                    .materialName((String) r[1])
                    .unit(r[2] != null ? r[2].toString() : null)
                    .quantity(((Number) r[3]).intValue())
                    .value((BigDecimal) r[4])
                    .build())
        .collect(Collectors.toList());
  }

  private List<MaterialStockItem> buildStockByMaterial(List<String> excludeIds) {
    List<Object[]> raw = materialWarehouseRepository.findStockByMaterial(excludeIds);
    return raw.stream()
        .map(
            r ->
                MaterialStockItem.builder()
                    .materialId((String) r[0])
                    .materialName((String) r[1])
                    .unit(r[2] != null ? r[2].toString() : null)
                    .quantity(((Number) r[5]).intValue())
                    .stockValue((BigDecimal) r[6])
                    .warehouse((String) r[4])
                    .build())
        .collect(Collectors.toList());
  }

  private List<MaterialConsumptionItem> buildConsumptionByMaterial(List<Object[]> raw) {
    return raw.stream()
        .map(
            r ->
                MaterialConsumptionItem.builder()
                    .materialId((String) r[0])
                    .materialName((String) r[1])
                    .unit(r[2] != null ? r[2].toString() : null)
                    .quantity(((Number) r[3]).intValue())
                    .cost((BigDecimal) r[4])
                    .period((String) r[5])
                    .build())
        .collect(Collectors.toList());
  }

  private List<ExpiringMaterialItem> buildExpiringMaterials() {
    LocalDate threshold = LocalDate.now().plusDays(30);
    return materialRepository.findByExpiryDateBefore(threshold).stream()
        .flatMap(
            m ->
                m.getMaterialWarehouses().stream()
                    .filter(
                        mw ->
                            mw.getQuantity() > 0
                                && !specialWarehouseIds().contains(mw.getWarehouse().getId()))
                    .map(
                        mw ->
                            ExpiringMaterialItem.builder()
                                .materialId(m.getId())
                                .materialName(m.getName())
                                .expiryDate(m.getExpiryDate())
                                .stockQuantity(mw.getQuantity())
                                .warehouse(mw.getWarehouse().getName())
                                .build()))
        .collect(Collectors.toList());
  }
}
