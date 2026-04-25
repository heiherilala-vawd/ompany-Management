package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.PurchaseOperationEquipmentLine;
import com.example.demo.client.model.PurchaseOperationMaterialLine;
import com.example.demo.client.model.PurchaseOperationRequest;
import com.example.demo.client.model.PurchaseOperationTravel;
import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.money.Purchase;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Material;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.TravelPeople;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.service.JobService;
import com.example.demo.service.UserService;
import com.example.demo.service.money.PurchaseOperationAggregate;
import com.example.demo.service.movement.EquipmentService;
import com.example.demo.service.movement.MaterialService;
import com.example.demo.service.movement.WarehouseService;
import com.example.demo.service.utils.SpecialWarehouseUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurchaseOperationMapper {

  private final JobService jobService;
  private final UserService userService;
  private final WarehouseService warehouseService;
  private final EquipmentService equipmentService;
  private final MaterialService materialService;

  public PurchaseOperationAggregate toAggregate(
      String jobId, String userId, PurchaseOperationRequest request) {
    List<PurchaseOperationEquipmentLine> equipmentLines =
        request.getEquipmentLines() != null ? request.getEquipmentLines() : List.of();
    List<PurchaseOperationMaterialLine> materialLines =
        request.getMaterialLines() != null ? request.getMaterialLines() : List.of();

    if (equipmentLines.isEmpty() && materialLines.isEmpty()) {
      throw new BadRequestException("At least one equipment or material line is required");
    }

    Job job =
        jobService
            .findById(jobId)
            .orElseThrow(() -> new NotFoundException("Job with id " + jobId + " not found"));
    com.example.demo.model.User buyer = userService.getById(userId);
    User supplier = getUser(request.getSupplierId(), "supplier_id");

    Warehouse routeWarehouse = resolveWarehouse(SpecialWarehouseUtils.routeWarehouseId());
    Warehouse atSellerWarehouse = resolveWarehouse(SpecialWarehouseUtils.atSellerWarehouseId());

    boolean hasTravel = hasTravel(request.getTravel());
    boolean storeAtSeller =
        !hasTravel
            || SpecialWarehouseUtils.atSellerWarehouseId()
                .equals(request.getTravel().getDepartureLocationId());
    Warehouse targetWarehouse = storeAtSeller ? atSellerWarehouse : routeWarehouse;

    List<ExpenseMoney> purchaseExpenses = new ArrayList<>();
    List<Purchase> purchases = new ArrayList<>();
    List<Equipment> equipmentToUpdate = new ArrayList<>();
    List<MaterialWarehouse> materialWarehouses = new ArrayList<>();

    for (PurchaseOperationEquipmentLine equipmentLine : equipmentLines) {
      Equipment equipment = getEquipment(equipmentLine.getEquipmentId());
      int unitPrice = requirePositive(equipmentLine.getUnitPrice(), "equipment unit price");

      ExpenseMoney expense =
          ExpenseMoney.builder()
              .id(UUID.randomUUID().toString())
              .job(job)
              .amount(unitPrice)
              .description("Purchase of equipment " + equipment.getName())
              .comment(request.getComment())
              .build();
      purchaseExpenses.add(expense);

      purchases.add(
          Purchase.builder()
              .id(UUID.randomUUID().toString())
              .expense(expense)
              .supplier(supplier)
              .equipment(equipment)
              .material(null)
              .quantity(1)
              .isEquipment(true)
              .build());

      equipmentToUpdate.add(copyEquipmentWithWarehouse(equipment, targetWarehouse));
    }

    for (PurchaseOperationMaterialLine materialLine : materialLines) {
      Material material = getMaterial(materialLine.getMaterialId());
      int quantity = requirePositive(materialLine.getQuantity(), "material quantity");
      int unitPrice = requirePositive(materialLine.getUnitPrice(), "material unit price");

      ExpenseMoney expense =
          ExpenseMoney.builder()
              .id(UUID.randomUUID().toString())
              .job(job)
              .amount(quantity * unitPrice)
              .description("Purchase of material " + material.getName())
              .comment(request.getComment())
              .build();
      purchaseExpenses.add(expense);

      purchases.add(
          Purchase.builder()
              .id(UUID.randomUUID().toString())
              .expense(expense)
              .supplier(supplier)
              .equipment(null)
              .material(material)
              .quantity(quantity)
              .isEquipment(false)
              .build());

      materialWarehouses.add(
          MaterialWarehouse.builder()
              .material(material)
              .warehouse(targetWarehouse)
              .quantity(quantity)
              .build());
    }

    ExpenseMoney travelExpenseMoney = null;
    TravelExpense travelExpense = null;
    List<TravelPeople> travelPeople = List.of();
    List<TravelMaterials> travelMaterials = List.of();
    List<TravelEquipment> travelEquipment = List.of();

    if (hasTravel) {
      PurchaseOperationTravel travel = request.getTravel();
      travelExpenseMoney =
          ExpenseMoney.builder()
              .id(UUID.randomUUID().toString())
              .job(job)
              .amount(travel.getFee() != null ? travel.getFee() : 0)
              .description("Travel expense for purchase operation")
              .comment(request.getComment())
              .build();

      travelExpense =
          TravelExpense.builder()
              .id(UUID.randomUUID().toString())
              .expense(travelExpenseMoney)
              .departureLocation(travel.getDepartureLocationId())
              .arrivalLocation(travel.getArrivalLocationId())
              .departureDate(travel.getDepartureDate())
              .arrivalDate(travel.getArrivalDate())
              .build();

      travelPeople =
          List.of(
              TravelPeople.builder()
                  .id(UUID.randomUUID().toString())
                  .travel(travelExpense)
                  .user(buyer)
                  .comment(request.getComment())
                  .build());

      List<TravelMaterials> mappedTravelMaterials = new ArrayList<>();
      for (PurchaseOperationMaterialLine materialLine : materialLines) {
        mappedTravelMaterials.add(
            TravelMaterials.builder()
                .id(UUID.randomUUID().toString())
                .travel(travelExpense)
                .material(getMaterial(materialLine.getMaterialId()))
                .quantity(requirePositive(materialLine.getQuantity(), "material quantity"))
                .quantityReceived(0)
                .comment(request.getComment())
                .build());
      }
      travelMaterials = mappedTravelMaterials;

      List<TravelEquipment> mappedTravelEquipment = new ArrayList<>();
      for (PurchaseOperationEquipmentLine equipmentLine : equipmentLines) {
        mappedTravelEquipment.add(
            TravelEquipment.builder()
                .id(UUID.randomUUID().toString())
                .travel(travelExpense)
                .equipment(getEquipment(equipmentLine.getEquipmentId()))
                .quantity(1)
                .status(TravelEquipment.TransportStatus.IN_PROGRESS)
                .comment(request.getComment())
                .build());
      }
      travelEquipment = mappedTravelEquipment;
    }

    return new PurchaseOperationAggregate(
        purchaseExpenses,
        purchases,
        equipmentToUpdate,
        materialWarehouses,
        travelExpenseMoney,
        travelExpense,
        travelPeople,
        travelMaterials,
        travelEquipment);
  }

  private boolean hasTravel(PurchaseOperationTravel travel) {
    return travel != null
        && (travel.getFee() != null
            || travel.getDepartureLocationId() != null
            || travel.getArrivalLocationId() != null
            || travel.getDepartureDate() != null
            || travel.getArrivalDate() != null);
  }

  private int requirePositive(Integer value, String fieldName) {
    if (value == null || value <= 0) {
      throw new BadRequestException(fieldName + " must be greater than zero");
    }
    return value;
  }

  private Warehouse resolveWarehouse(String warehouseId) {
    return warehouseService
        .findById(warehouseId)
        .orElseThrow(
            () -> new NotFoundException("Warehouse with id " + warehouseId + " not found"));
  }

  private Equipment getEquipment(String equipmentId) {
    if (equipmentId == null) {
      throw new BadRequestException("equipment_id is required");
    }
    return equipmentService
        .findById(equipmentId)
        .orElseThrow(
            () -> new NotFoundException("Equipment with id " + equipmentId + " not found"));
  }

  private Material getMaterial(String materialId) {
    if (materialId == null) {
      throw new BadRequestException("material_id is required");
    }
    return materialService
        .findById(materialId)
        .orElseThrow(() -> new NotFoundException("Material with id " + materialId + " not found"));
  }

  private User getUser(String userId, String fieldName) {
    if (userId == null) {
      throw new BadRequestException(fieldName + " is required");
    }
    return userService.getById(userId);
  }

  private Equipment copyEquipmentWithWarehouse(Equipment source, Warehouse warehouse) {
    return Equipment.builder()
        .id(source.getId())
        .name(source.getName())
        .description(source.getDescription())
        .warehouse(warehouse)
        .floorNumber(source.getFloorNumber())
        .storageNumber(source.getStorageNumber())
        .comment(source.getComment())
        .build();
  }
}
