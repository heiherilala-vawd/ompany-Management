package com.example.demo.service.dashboard;

import com.example.demo.model.dashboard.MaterialBreakdownResponse;
import com.example.demo.model.dashboard.MaterialDashboardResponse;
import com.example.demo.model.dashboard.MaterialSummaryResponse;
import com.example.demo.repository.movement.MaterialConsumptionRepository;
import com.example.demo.repository.movement.MaterialRepository;
import com.example.demo.repository.movement.MaterialWarehouseRepository;
import com.example.demo.service.utils.SpecialWarehouseUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    return buildDashboard(jobId, dateFrom, dateTo);
  }

  public MaterialSummaryResponse getSummary(String jobId, LocalDate dateFrom, LocalDate dateTo) {
    List<String> excludeIds = specialWarehouseIds();

    BigDecimal stockValueTotal =
        materialWarehouseRepository.sumStockValueExcludingWarehouses(excludeIds);

    BigDecimal consumptionCostTotal =
        materialConsumptionRepository.sumConsumptionCost(jobId, dateFrom, dateTo);

    long totalMaterialsCount = materialRepository.count();

    LocalDate threshold = LocalDate.now().plusDays(30);
    long expiringCount =
        materialRepository.findByExpiryDateBefore(threshold).stream()
            .flatMap(m -> m.getMaterialWarehouses().stream())
            .filter(
                mw ->
                    mw.getQuantity() > 0
                        && !specialWarehouseIds().contains(mw.getWarehouse().getId()))
            .count();

    return MaterialSummaryResponse.builder()
        .stockValueTotal(stockValueTotal)
        .consumptionCostTotal(consumptionCostTotal)
        .totalMaterialsCount((int) totalMaterialsCount)
        .expiringCount((int) expiringCount)
        .build();
  }

  public MaterialBreakdownResponse getBreakdown(
      String jobId, LocalDate dateFrom, LocalDate dateTo) {
    List<String> excludeIds = specialWarehouseIds();

    List<MaterialBreakdownResponse.MaterialValueItem> top5Stock =
        buildBreakdownTop5Stock(excludeIds);

    List<Object[]> topConsumptionRaw =
        materialConsumptionRepository.findTopConsumptionCost(jobId, dateFrom, dateTo);
    List<MaterialBreakdownResponse.MaterialValueItem> top5Consumption =
        buildBreakdownTopConsumption(topConsumptionRaw);

    List<MaterialBreakdownResponse.MaterialStockItem> stockByMat =
        buildBreakdownStockByMaterial(excludeIds);

    List<MaterialBreakdownResponse.WarehouseStockValue> stockValueByWarehouse =
        buildStockValueByWarehouse(stockByMat);

    List<Object[]> consumptionRaw =
        materialConsumptionRepository.findConsumptionByMaterial(jobId, dateFrom, dateTo);
    List<MaterialBreakdownResponse.MaterialConsumptionItem> consumptionByMat =
        buildBreakdownConsumptionByMaterial(consumptionRaw);

    List<MaterialBreakdownResponse.ExpiringMaterialItem> expiring =
        buildBreakdownExpiringMaterials();

    return MaterialBreakdownResponse.builder()
        .top5StockValue(top5Stock)
        .top5ConsumptionCost(top5Consumption)
        .stockByMaterial(stockByMat)
        .consumptionByMaterial(consumptionByMat)
        .stockValueByWarehouse(stockValueByWarehouse)
        .expiringMaterials(expiring)
        .build();
  }

  private MaterialDashboardResponse buildDashboard(
      String jobId, LocalDate dateFrom, LocalDate dateTo) {
    List<String> excludeIds = specialWarehouseIds();

    BigDecimal stockValueTotal =
        materialWarehouseRepository.sumStockValueExcludingWarehouses(excludeIds);

    BigDecimal consumptionCostTotal =
        materialConsumptionRepository.sumConsumptionCost(jobId, dateFrom, dateTo);

    List<MaterialDashboardResponse.MaterialValueItem> top5Stock = buildTop5Stock(excludeIds);

    List<Object[]> topConsumptionRaw =
        materialConsumptionRepository.findTopConsumptionCost(jobId, dateFrom, dateTo);
    List<MaterialDashboardResponse.MaterialValueItem> top5Consumption =
        buildTopConsumption(topConsumptionRaw);

    List<MaterialDashboardResponse.MaterialStockItem> stockByMat = buildStockByMaterial(excludeIds);

    List<Object[]> consumptionRaw =
        materialConsumptionRepository.findConsumptionByMaterial(jobId, dateFrom, dateTo);
    List<MaterialDashboardResponse.MaterialConsumptionItem> consumptionByMat =
        buildConsumptionByMaterial(consumptionRaw);

    List<MaterialDashboardResponse.ExpiringMaterialItem> expiring = buildExpiringMaterials();

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
        SpecialWarehouseUtils.usedWarehouseId());
  }

  private List<MaterialDashboardResponse.MaterialValueItem> buildTop5Stock(
      List<String> excludeIds) {
    List<Object[]> raw = materialWarehouseRepository.findTopStockValue(excludeIds);
    return raw.stream()
        .limit(5)
        .map(
            r ->
                MaterialDashboardResponse.MaterialValueItem.builder()
                    .materialId((String) r[0])
                    .materialName((String) r[1])
                    .unit(r[2] != null ? r[2].toString() : null)
                    .quantity(((Number) r[3]).intValue())
                    .value((BigDecimal) r[4])
                    .build())
        .collect(Collectors.toList());
  }

  private List<MaterialDashboardResponse.MaterialValueItem> buildTopConsumption(
      List<Object[]> raw) {
    return raw.stream()
        .limit(5)
        .map(
            r ->
                MaterialDashboardResponse.MaterialValueItem.builder()
                    .materialId((String) r[0])
                    .materialName((String) r[1])
                    .unit(r[2] != null ? r[2].toString() : null)
                    .quantity(((Number) r[3]).intValue())
                    .value((BigDecimal) r[4])
                    .build())
        .collect(Collectors.toList());
  }

  private List<MaterialDashboardResponse.MaterialStockItem> buildStockByMaterial(
      List<String> excludeIds) {
    List<Object[]> raw = materialWarehouseRepository.findStockByMaterial(excludeIds);
    return raw.stream()
        .map(
            r ->
                MaterialDashboardResponse.MaterialStockItem.builder()
                    .materialId((String) r[0])
                    .materialName((String) r[1])
                    .unit(r[2] != null ? r[2].toString() : null)
                    .quantity(((Number) r[5]).intValue())
                    .stockValue((BigDecimal) r[6])
                    .warehouse((String) r[4])
                    .build())
        .collect(Collectors.toList());
  }

  private List<MaterialDashboardResponse.MaterialConsumptionItem> buildConsumptionByMaterial(
      List<Object[]> raw) {
    return raw.stream()
        .map(
            r ->
                MaterialDashboardResponse.MaterialConsumptionItem.builder()
                    .materialId((String) r[0])
                    .materialName((String) r[1])
                    .unit(r[2] != null ? r[2].toString() : null)
                    .quantity(((Number) r[3]).intValue())
                    .cost((BigDecimal) r[4])
                    .period((String) r[5])
                    .build())
        .collect(Collectors.toList());
  }

  private List<MaterialBreakdownResponse.MaterialValueItem> buildBreakdownTop5Stock(
      List<String> excludeIds) {
    List<Object[]> raw = materialWarehouseRepository.findTopStockValue(excludeIds);
    return raw.stream()
        .limit(5)
        .map(
            r ->
                MaterialBreakdownResponse.MaterialValueItem.builder()
                    .materialId((String) r[0])
                    .materialName((String) r[1])
                    .unit(r[2] != null ? r[2].toString() : null)
                    .quantity(((Number) r[3]).intValue())
                    .value((BigDecimal) r[4])
                    .build())
        .collect(Collectors.toList());
  }

  private List<MaterialBreakdownResponse.MaterialValueItem> buildBreakdownTopConsumption(
      List<Object[]> raw) {
    return raw.stream()
        .limit(5)
        .map(
            r ->
                MaterialBreakdownResponse.MaterialValueItem.builder()
                    .materialId((String) r[0])
                    .materialName((String) r[1])
                    .unit(r[2] != null ? r[2].toString() : null)
                    .quantity(((Number) r[3]).intValue())
                    .value((BigDecimal) r[4])
                    .build())
        .collect(Collectors.toList());
  }

  private List<MaterialBreakdownResponse.MaterialStockItem> buildBreakdownStockByMaterial(
      List<String> excludeIds) {
    List<Object[]> raw = materialWarehouseRepository.findStockByMaterial(excludeIds);
    return raw.stream()
        .map(
            r ->
                MaterialBreakdownResponse.MaterialStockItem.builder()
                    .materialId((String) r[0])
                    .materialName((String) r[1])
                    .unit(r[2] != null ? r[2].toString() : null)
                    .quantity(((Number) r[5]).intValue())
                    .stockValue((BigDecimal) r[6])
                    .warehouse((String) r[4])
                    .build())
        .collect(Collectors.toList());
  }

  private List<MaterialBreakdownResponse.MaterialConsumptionItem>
      buildBreakdownConsumptionByMaterial(List<Object[]> raw) {
    return raw.stream()
        .map(
            r ->
                MaterialBreakdownResponse.MaterialConsumptionItem.builder()
                    .materialId((String) r[0])
                    .materialName((String) r[1])
                    .unit(r[2] != null ? r[2].toString() : null)
                    .quantity(((Number) r[3]).intValue())
                    .cost((BigDecimal) r[4])
                    .period((String) r[5])
                    .build())
        .collect(Collectors.toList());
  }

  private List<MaterialBreakdownResponse.WarehouseStockValue> buildStockValueByWarehouse(
      List<MaterialBreakdownResponse.MaterialStockItem> stockItems) {
    Map<String, BigDecimal> byWarehouse =
        stockItems.stream()
            .collect(
                Collectors.groupingBy(
                    MaterialBreakdownResponse.MaterialStockItem::getWarehouse,
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        MaterialBreakdownResponse.MaterialStockItem::getStockValue,
                        BigDecimal::add)));
    return byWarehouse.entrySet().stream()
        .map(
            e ->
                MaterialBreakdownResponse.WarehouseStockValue.builder()
                    .warehouse(e.getKey())
                    .totalValue(e.getValue())
                    .build())
        .collect(Collectors.toList());
  }

  private List<MaterialBreakdownResponse.ExpiringMaterialItem> buildBreakdownExpiringMaterials() {
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
                            MaterialBreakdownResponse.ExpiringMaterialItem.builder()
                                .materialId(m.getId())
                                .materialName(m.getName())
                                .expiryDate(m.getExpiryDate())
                                .stockQuantity(mw.getQuantity())
                                .warehouse(mw.getWarehouse().getName())
                                .build()))
        .collect(Collectors.toList());
  }

  private List<MaterialDashboardResponse.ExpiringMaterialItem> buildExpiringMaterials() {
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
                            MaterialDashboardResponse.ExpiringMaterialItem.builder()
                                .materialId(m.getId())
                                .materialName(m.getName())
                                .expiryDate(m.getExpiryDate())
                                .stockQuantity(mw.getQuantity())
                                .warehouse(mw.getWarehouse().getName())
                                .build()))
        .collect(Collectors.toList());
  }
}
