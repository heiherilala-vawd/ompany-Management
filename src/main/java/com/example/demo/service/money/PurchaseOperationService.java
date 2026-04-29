package com.example.demo.service.money;

import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Material;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.service.movement.EquipmentService;
import com.example.demo.service.movement.MaterialService;
import com.example.demo.service.movement.MaterialWarehouseService;
import com.example.demo.service.movement.TravelEquipmentService;
import com.example.demo.service.movement.TravelMaterialsService;
import com.example.demo.service.movement.WarehouseService;
import com.example.demo.service.utils.SpecialWarehouseUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOperationService {

  private final ExpenseMoneyService expenseMoneyService;
  private final PurchaseService purchaseService;
  private final TravelExpenseService travelExpenseService;
  private final TravelMaterialsService travelMaterialsService;
  private final TravelEquipmentService travelEquipmentService;
  private final EquipmentService equipmentService;
  private final MaterialService materialService;
  private final MaterialWarehouseService materialWarehouseService;
  private final WarehouseService warehouseService;

  @Transactional
  public void create(PurchaseOperationAggregate aggregate) {
    boolean hasTravel = aggregate.travelExpenseMoney() != null;
    Warehouse targetWarehouse = resolveTargetWarehouse(aggregate, hasTravel);

    saveNewMaterials(aggregate.materials());
    saveNewEquipment(aggregate.equipment(), targetWarehouse);

    expenseMoneyService.createOrUpdateAll(aggregate.expenses());
    purchaseService.createOrUpdateAll(aggregate.purchases());

    List<MaterialWarehouse> materialWarehouses = new ArrayList<>();
    for (MaterialWarehouse mw : aggregate.materialWarehouses()) {
      materialWarehouses.add(
          MaterialWarehouse.builder()
              .material(mw.getMaterial())
              .warehouse(targetWarehouse)
              .quantity(mw.getQuantity())
              .build());
    }
    if (!materialWarehouses.isEmpty()) {
      materialWarehouseService.incrementQuantities(materialWarehouses);
    }

    if (!hasTravel) {
      return;
    }

    saveNewWarehouses(aggregate.departureWarehouse(), aggregate.arrivalWarehouse());

    expenseMoneyService.createOrUpdateAll(List.of(aggregate.travelExpenseMoney()));
    travelExpenseService.createOrUpdateAll(List.of(aggregate.travelExpense()));

    linkAndSaveTravelEquipment(aggregate.travelEquipment(), aggregate.travelExpense());
    linkAndSaveTravelMaterials(aggregate.travelMaterials(), aggregate.travelExpense());
  }

  private Warehouse resolveTargetWarehouse(
      PurchaseOperationAggregate aggregate, boolean hasTravel) {
    if (!hasTravel) {
      return getOrCreateWarehouse(SpecialWarehouseUtils.atSellerWarehouseId());
    }

    Warehouse departure = aggregate.departureWarehouse();
    Warehouse arrival = aggregate.arrivalWarehouse();

    if (arrival == null) {
      return getOrCreateWarehouse(SpecialWarehouseUtils.routeWarehouseId());
    }
    if (departure == null) {
      return getOrCreateWarehouse(SpecialWarehouseUtils.atSellerWarehouseId());
    }

    boolean storeAtSeller = SpecialWarehouseUtils.atSellerWarehouseId().equals(departure.getId());
    return storeAtSeller
        ? getOrCreateWarehouse(SpecialWarehouseUtils.atSellerWarehouseId())
        : getOrCreateWarehouse(SpecialWarehouseUtils.routeWarehouseId());
  }

  private void saveNewMaterials(List<Material> materials) {
    List<Material> newMaterials = new ArrayList<>();
    for (Material material : materials) {
      Optional<Material> existing = materialService.findById(material.getId());
      if (existing.isEmpty()) {
        newMaterials.add(material);
      }
    }
    if (!newMaterials.isEmpty()) {
      materialService.createOrUpdateAll(newMaterials);
    }
  }

  private void saveNewEquipment(List<Equipment> equipmentList, Warehouse targetWarehouse) {
    List<Equipment> toUpdate = new ArrayList<>();
    for (Equipment equipment : equipmentList) {
      Optional<Equipment> existing = equipmentService.findById(equipment.getId());
      if (existing.isEmpty()) {
        toUpdate.add(
            Equipment.builder()
                .id(equipment.getId())
                .name(equipment.getName())
                .description(equipment.getDescription())
                .warehouse(targetWarehouse)
                .floorNumber(equipment.getFloorNumber())
                .storageNumber(equipment.getStorageNumber())
                .comment(equipment.getComment())
                .build());
        continue;
      }

      Equipment existingEquipment = existing.get();
      if (existingEquipment.getWarehouse() != null) {
        throw new BadRequestException("equipment already has a warehouse assigned");
      }

      toUpdate.add(
          Equipment.builder()
              .id(existingEquipment.getId())
              .name(
                  existingEquipment.getName() != null
                      ? existingEquipment.getName()
                      : equipment.getName())
              .description(
                  existingEquipment.getDescription() != null
                      ? existingEquipment.getDescription()
                      : equipment.getDescription())
              .warehouse(targetWarehouse)
              .floorNumber(existingEquipment.getFloorNumber())
              .storageNumber(existingEquipment.getStorageNumber())
              .comment(
                  existingEquipment.getComment() != null
                      ? existingEquipment.getComment()
                      : equipment.getComment())
              .build());
    }
    if (!toUpdate.isEmpty()) {
      equipmentService.createOrUpdateAll(toUpdate);
    }
  }

  private void saveNewWarehouses(Warehouse departure, Warehouse arrival) {
    if (departure != null) {
      Optional<Warehouse> existing = warehouseService.findById(departure.getId());
      if (existing.isEmpty()) {
        warehouseService.createOrUpdateAll(List.of(departure));
      }
    }
    if (arrival != null) {
      Optional<Warehouse> existing = warehouseService.findById(arrival.getId());
      if (existing.isEmpty()) {
        warehouseService.createOrUpdateAll(List.of(arrival));
      }
    }
  }

  private void linkAndSaveTravelEquipment(
      List<TravelEquipment> travelEquipmentList, TravelExpense travelExpense) {
    if (travelEquipmentList.isEmpty()) {
      return;
    }
    for (TravelEquipment te : travelEquipmentList) {
      Optional<TravelEquipment> existing = travelEquipmentService.findById(te.getId());
      if (existing.isPresent()) {
        throw new BadRequestException("on ne peut pas acheter un equipement qui existe deja");
      }
    }
    List<TravelEquipment> linked = new ArrayList<>();
    for (TravelEquipment te : travelEquipmentList) {
      linked.add(
          TravelEquipment.builder()
              .id(te.getId())
              .travel(travelExpense)
              .equipment(te.getEquipment())
              .quantity(te.getQuantity())
              .status(te.getStatus())
              .comment(te.getComment())
              .build());
    }
    travelEquipmentService.createOrUpdateAll(linked);
  }

  private void linkAndSaveTravelMaterials(
      List<TravelMaterials> travelMaterialsList, TravelExpense travelExpense) {
    if (travelMaterialsList.isEmpty()) {
      return;
    }
    List<TravelMaterials> linked = new ArrayList<>();
    for (TravelMaterials tm : travelMaterialsList) {
      Optional<TravelMaterials> existing = travelMaterialsService.findById(tm.getId());
      if (existing.isPresent()) {
        continue;
      }
      linked.add(
          TravelMaterials.builder()
              .id(tm.getId())
              .travel(travelExpense)
              .material(tm.getMaterial())
              .quantity(tm.getQuantity())
              .quantityReceived(tm.getQuantityReceived())
              .comment(tm.getComment())
              .build());
    }
    if (!linked.isEmpty()) {
      travelMaterialsService.createOrUpdateAll(linked);
    }
  }

  private Warehouse getOrCreateWarehouse(String warehouseId) {
    return warehouseService
        .findById(warehouseId)
        .orElseGet(
            () -> {
              Warehouse warehouse = Warehouse.builder().id(warehouseId).name(warehouseId).build();
              return warehouseService.createOrUpdateAll(List.of(warehouse)).get(0);
            });
  }
}
