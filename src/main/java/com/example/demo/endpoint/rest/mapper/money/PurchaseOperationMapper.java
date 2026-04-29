package com.example.demo.endpoint.rest.mapper.money;

import static com.example.demo.service.utils.SpecialWarehouseUtils.atSellerWarehouseId;

import com.example.demo.client.model.CrupdateEquipment;
import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.PurchaseOperationEquipmentLine;
import com.example.demo.client.model.PurchaseOperationMaterialLine;
import com.example.demo.client.model.PurchaseOperationRequest;
import com.example.demo.client.model.PurchaseOperationTravel;
import com.example.demo.endpoint.rest.mapper.movement.WarehouseMapper;
import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.money.Purchase;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Material;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.service.money.PurchaseOperationAggregate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurchaseOperationMapper {
  private final WarehouseMapper warehouseMapper;

  public PurchaseOperationAggregate toAggregate(
      String jobId, String userId, PurchaseOperationRequest request) {
    List<PurchaseOperationEquipmentLine> equipmentLines =
        request.getEquipmentLines() != null ? request.getEquipmentLines() : List.of();
    List<PurchaseOperationMaterialLine> materialLines =
        request.getMaterialLines() != null ? request.getMaterialLines() : List.of();

    if (equipmentLines.isEmpty() && materialLines.isEmpty()) {
      throw new com.example.demo.model.exception.BadRequestException(
          "At least one equipment or material line is required");
    }

    Job job = Job.builder().id(jobId).build();
    User buyer = User.builder().id(userId).build();
    Warehouse warehouseInStore = Warehouse.builder().id(atSellerWarehouseId()).build();
    Warehouse supplier =
        request.getTravel() != null
            ? request.getTravel().getDepartureLocation() != null
                ? warehouseMapper.toDomain(request.getTravel().getDepartureLocation())
                : warehouseInStore
            : warehouseInStore;

    List<ExpenseMoney> purchaseExpenses = new ArrayList<>();
    List<Purchase> purchases = new ArrayList<>();

    List<Equipment> equipmentToUpdate = new ArrayList<>();
    List<Material> materials = new ArrayList<>();
    List<MaterialWarehouse> materialWarehouses = new ArrayList<>();

    ExpenseMoney travelExpenseMoney = null;
    TravelExpense travelExpense = null;
    List<TravelMaterials> travelMaterials = List.of();
    List<TravelEquipment> travelEquipment = List.of();
    Warehouse departureWarehouse = null;
    Warehouse arrivalWarehouse = null;

    for (PurchaseOperationEquipmentLine equipmentLine : equipmentLines) {
      CrupdateEquipment crupdateEquipment = equipmentLine.getEquipment();
      Equipment equipment = toEquipment(crupdateEquipment);
      int unitPrice = requirePositive(equipmentLine.getUnitPrice(), "equipment unit price");

      ExpenseMoney expense =
          ExpenseMoney.builder()
              .id(equipmentLine.getExpenseId())
              .job(job)
              .amount(unitPrice)
              .description("Purchase of equipment " + equipment.getName())
              .comment(request.getComment())
              .build();
      purchaseExpenses.add(expense);

      purchases.add(
          Purchase.builder()
              .id(equipmentLine.getPurchaseId())
              .expense(expense)
              .supplier(supplier)
              .equipment(equipment)
              .material(null)
              .quantity(1)
              .isEquipment(true)
              .build());

      equipmentToUpdate.add(equipment);
    }

    for (PurchaseOperationMaterialLine materialLine : materialLines) {
      CrupdateMaterial crupdateMaterial = materialLine.getMaterial();
      Material material = toMaterial(crupdateMaterial);
      int quantity = requirePositive(materialLine.getQuantity(), "material quantity");
      int unitPrice = requirePositive(materialLine.getUnitPrice(), "material unit price");

      ExpenseMoney expense =
          ExpenseMoney.builder()
              .id(materialLine.getExpenseId())
              .job(job)
              .amount(quantity * unitPrice)
              .description("Purchase of material " + material.getName())
              .comment(request.getComment())
              .build();
      purchaseExpenses.add(expense);

      purchases.add(
          Purchase.builder()
              .id(materialLine.getPurchaseId())
              .expense(expense)
              .supplier(supplier)
              .equipment(null)
              .material(material)
              .quantity(quantity)
              .isEquipment(false)
              .build());

      materials.add(material);
      materialWarehouses.add(
          MaterialWarehouse.builder()
              .material(material)
              .warehouse(null)
              .quantity(quantity)
              .build());
    }

    if (hasTravel(request.getTravel())) {
      PurchaseOperationTravel travel = request.getTravel();
      departureWarehouse = toWarehouse(travel.getDepartureLocation());
      arrivalWarehouse = toWarehouse(travel.getArrivalLocation());

      travelExpenseMoney =
          ExpenseMoney.builder()
              .id(travel.getExpenseId())
              .job(job)
              .amount(travel.getFee() != null ? travel.getFee() : 0)
              .description("Travel expense for purchase operation")
              .comment(request.getComment())
              .build();

      travelExpense =
          TravelExpense.builder()
              .id(travel.getId())
              .expense(travelExpenseMoney)
              .departureLocation(departureWarehouse)
              .arrivalLocation(arrivalWarehouse)
              .departureDate(travel.getDepartureDate())
              .arrivalDate(travel.getArrivalDate())
              .build();
    }

    boolean hasTravel = hasTravel(request.getTravel());
    boolean createTravelMaterials = !materialLines.isEmpty() && hasTravel;
    boolean createTravelEquipment = !equipmentLines.isEmpty() && hasTravel;

    if (createTravelMaterials) {
      List<TravelMaterials> mappedTravelMaterials = new ArrayList<>();
      for (PurchaseOperationMaterialLine materialLine : materialLines) {
        mappedTravelMaterials.add(
            TravelMaterials.builder()
                .id(materialLine.getTravelMaterialId())
                .travel(travelExpense)
                .material(toMaterial(materialLine.getMaterial()))
                .quantity(requirePositive(materialLine.getQuantity(), "material quantity"))
                .quantityReceived(0)
                .comment(request.getComment())
                .build());
      }
      travelMaterials = mappedTravelMaterials;
    }

    if (createTravelEquipment) {
      List<TravelEquipment> mappedTravelEquipment = new ArrayList<>();
      for (PurchaseOperationEquipmentLine equipmentLine : equipmentLines) {
        mappedTravelEquipment.add(
            TravelEquipment.builder()
                .id(equipmentLine.getTravelEquipmentId())
                .travel(travelExpense)
                .equipment(toEquipment(equipmentLine.getEquipment()))
                .quantity(1)
                .status(TravelEquipment.TransportStatus.IN_PROGRESS)
                .comment(request.getComment())
                .build());
      }
      travelEquipment = mappedTravelEquipment;
    }

    return new PurchaseOperationAggregate(
        equipmentToUpdate,
        travelEquipment,
        purchaseExpenses,
        purchases,
        materials,
        travelMaterials,
        materialWarehouses,
        departureWarehouse,
        arrivalWarehouse,
        travelExpenseMoney,
        travelExpense);
  }

  private Equipment toEquipment(CrupdateEquipment c) {
    if (c == null) return null;
    return Equipment.builder()
        .id(c.getId())
        .name(c.getName())
        .description(c.getDescription())
        .warehouse(null)
        .floorNumber(c.getFloorNumber() != null ? c.getFloorNumber() : 0)
        .storageNumber(c.getStorageNumber() != null ? c.getStorageNumber() : 0)
        .comment(c.getComment())
        .build();
  }

  private Material toMaterial(CrupdateMaterial c) {
    if (c == null) return null;
    return Material.builder()
        .id(c.getId())
        .name(c.getName())
        .description(c.getDescription())
        .comment(c.getComment())
        .build();
  }

  private Warehouse toWarehouse(CrupdateWarehouse c) {
    if (c == null) return null;
    return Warehouse.builder()
        .id(c.getId())
        .name(c.getName())
        .description(c.getDescription())
        .comment(c.getComment())
        .build();
  }

  private boolean hasTravel(PurchaseOperationTravel travel) {
    return travel != null
        && (travel.getFee() != null
            || travel.getDepartureLocation() != null
            || travel.getArrivalLocation() != null
            || travel.getDepartureDate() != null
            || travel.getArrivalDate() != null);
  }

  private int requirePositive(Integer value, String fieldName) {
    if (value == null || value <= 0) {
      throw new com.example.demo.model.exception.BadRequestException(
          fieldName + " must be greater than zero");
    }
    return value;
  }
}
