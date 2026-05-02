package com.example.demo.service.movement;

import com.example.demo.model.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Material;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.TravelPeople;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.money.ExpenseMoneyService;
import com.example.demo.service.money.TravelExpenseService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TravelOperationService {

  private final ExpenseMoneyService expenseMoneyService;
  private final TravelExpenseService travelExpenseService;
  private final TravelEquipmentService travelEquipmentService;
  private final TravelMaterialsService travelMaterialsService;
  private final TravelPeopleService travelPeopleService;
  private final EquipmentService equipmentService;
  private final MaterialService materialService;
  private final MaterialWarehouseService materialWarehouseService;
  private final WarehouseService warehouseService;
  private final UserRepository userRepository;

  @Transactional
  public TravelExpense create(TravelOperationAggregate aggregate) {
    if (aggregate.travel() == null) {
      throw new BadRequestException("travel is required");
    }
    validateLines(aggregate);

    Warehouse departure = resolveWarehouse(aggregate.departureWarehouse(), "departure_location");
    Warehouse arrival = resolveWarehouse(aggregate.arrivalWarehouse(), "arrival_location");

    boolean movesStock =
        !aggregate.travelEquipment().isEmpty() || !aggregate.travelMaterials().isEmpty();
    if (movesStock) {
      if (departure == null || arrival == null) {
        throw new BadRequestException(
            "departure_location and arrival_location are required for equipment or material moves");
      }
      if (departure.getId().equals(arrival.getId())) {
        throw new BadRequestException("departure_location and arrival_location must be different");
      }
    }

    ExpenseMoney savedExpenseMoney =
        saveTravelExpenseMoneyIfPresent(aggregate.travelExpenseMoney());
    TravelExpense savedTravel =
        saveTravel(aggregate.travel(), savedExpenseMoney, departure, arrival);

    moveEquipment(aggregate.travelEquipment(), savedTravel, departure, arrival);
    moveMaterials(aggregate.travelMaterials(), savedTravel, departure, arrival);
    saveTravelPeople(aggregate.travelPeople(), savedTravel);
    return savedTravel;
  }

  private void validateLines(TravelOperationAggregate aggregate) {
    if (aggregate.travelEquipment().isEmpty()
        && aggregate.travelMaterials().isEmpty()
        && aggregate.travelPeople().isEmpty()) {
      throw new BadRequestException("At least one equipment, material or people line is required");
    }
  }

  private Warehouse resolveWarehouse(Warehouse warehouse, String fieldName) {
    if (warehouse == null) {
      return null;
    }

    if (warehouse.getId() == null || warehouse.getId().isBlank()) {
      throw new BadRequestException(fieldName + ".id is required");
    }

    return warehouseService
        .findById(warehouse.getId())
        .orElseGet(() -> warehouseService.createOrUpdateAll(List.of(warehouse)).get(0));
  }

  private ExpenseMoney saveTravelExpenseMoneyIfPresent(ExpenseMoney travelExpenseMoney) {
    if (travelExpenseMoney == null) {
      return null;
    }
    return expenseMoneyService.createOrUpdateAll(List.of(travelExpenseMoney)).get(0);
  }

  private TravelExpense saveTravel(
      TravelExpense travel, ExpenseMoney expenseMoney, Warehouse departure, Warehouse arrival) {
    TravelExpense toSave =
        TravelExpense.builder()
            .id(requireId(travel.getId(), "travel.id"))
            .expense(expenseMoney)
            .departureLocation(departure)
            .arrivalLocation(arrival)
            .departureDate(travel.getDepartureDate())
            .arrivalDate(travel.getArrivalDate())
            .build();
    return travelExpenseService.createOrUpdateAll(List.of(toSave)).get(0);
  }

  private void moveEquipment(
      List<TravelEquipment> travelEquipmentList,
      TravelExpense travel,
      Warehouse departure,
      Warehouse arrival) {
    if (travelEquipmentList.isEmpty()) {
      return;
    }

    List<TravelEquipment> toSave = new ArrayList<>();
    List<Equipment> movedEquipment = new ArrayList<>();

    for (TravelEquipment travelEquipment : travelEquipmentList) {
      if (travelEquipmentService.findById(travelEquipment.getId()).isPresent()) {
        throw new BadRequestException(
            "travel equipment with id " + travelEquipment.getId() + " already exists");
      }

      String equipmentId =
          requireId(
              travelEquipment.getEquipment() != null
                  ? travelEquipment.getEquipment().getId()
                  : null,
              "equipment.id");
      Equipment existingEquipment =
          equipmentService
              .findById(equipmentId)
              .orElseThrow(
                  () -> new NotFoundException("Equipment with id " + equipmentId + " not found"));

      if (existingEquipment.getWarehouse() == null
          || existingEquipment.getWarehouse().getId() == null) {
        throw new BadRequestException("equipment " + equipmentId + " has no current warehouse");
      }
      if (!existingEquipment.getWarehouse().getId().equals(departure.getId())) {
        throw new BadRequestException(
            "equipment " + equipmentId + " is not located in warehouse " + departure.getId());
      }

      movedEquipment.add(
          Equipment.builder()
              .id(existingEquipment.getId())
              .name(existingEquipment.getName())
              .description(existingEquipment.getDescription())
              .warehouse(arrival)
              .floorNumber(existingEquipment.getFloorNumber())
              .storageNumber(existingEquipment.getStorageNumber())
              .comment(existingEquipment.getComment())
              .build());

      toSave.add(
          TravelEquipment.builder()
              .id(requireId(travelEquipment.getId(), "travel_equipment.id"))
              .travel(travel)
              .equipment(existingEquipment)
              .quantity(1)
              .status(TravelEquipment.TransportStatus.ARRIVED)
              .comment(travelEquipment.getComment())
              .build());
    }

    equipmentService.createOrUpdateAll(movedEquipment);
    travelEquipmentService.createOrUpdateAll(toSave);
  }

  private void moveMaterials(
      List<TravelMaterials> travelMaterialsList,
      TravelExpense travel,
      Warehouse departure,
      Warehouse arrival) {
    if (travelMaterialsList.isEmpty()) {
      return;
    }

    List<TravelMaterials> toSave = new ArrayList<>();

    for (TravelMaterials travelMaterials : travelMaterialsList) {
      if (travelMaterialsService.findById(travelMaterials.getId()).isPresent()) {
        throw new BadRequestException(
            "travel material with id " + travelMaterials.getId() + " already exists");
      }

      String materialId =
          requireId(
              travelMaterials.getMaterial() != null ? travelMaterials.getMaterial().getId() : null,
              "material.id");
      int quantity = requirePositive(travelMaterials.getQuantity(), "material quantity");

      Material material =
          materialService
              .findById(materialId)
              .orElseThrow(
                  () -> new NotFoundException("Material with id " + materialId + " not found"));

      MaterialWarehouse sourceStock =
          materialWarehouseService
              .findByMaterialAndWarehouse(materialId, departure.getId())
              .orElseThrow(
                  () ->
                      new BadRequestException(
                          "material "
                              + materialId
                              + " is not available in warehouse "
                              + departure.getId()));

      if (sourceStock.getQuantity() == null || sourceStock.getQuantity() < quantity) {
        throw new BadRequestException(
            "insufficient quantity for material "
                + materialId
                + " in warehouse "
                + departure.getId());
      }

      materialWarehouseService.decrementQuantity(
          MaterialWarehouse.builder()
              .material(material)
              .warehouse(departure)
              .quantity(quantity)
              .build());
      materialWarehouseService.incrementQuantity(
          MaterialWarehouse.builder()
              .material(material)
              .warehouse(arrival)
              .quantity(quantity)
              .build());

      toSave.add(
          TravelMaterials.builder()
              .id(requireId(travelMaterials.getId(), "travel_material.id"))
              .travel(travel)
              .material(material)
              .quantity(quantity)
              .quantityReceived(quantity)
              .comment(travelMaterials.getComment())
              .build());
    }

    travelMaterialsService.createOrUpdateAll(toSave);
  }

  private void saveTravelPeople(List<TravelPeople> travelPeopleList, TravelExpense travel) {
    if (travelPeopleList.isEmpty()) {
      return;
    }

    List<TravelPeople> toSave = new ArrayList<>();

    for (TravelPeople travelPeople : travelPeopleList) {
      if (travelPeopleService.findById(travelPeople.getId()).isPresent()) {
        throw new BadRequestException(
            "travel people with id " + travelPeople.getId() + " already exists");
      }

      String userId =
          requireId(
              travelPeople.getUser() != null ? travelPeople.getUser().getId() : null, "user_id");
      User user =
          userRepository
              .findById(userId)
              .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

      toSave.add(
          TravelPeople.builder()
              .id(requireId(travelPeople.getId(), "travel_people.id"))
              .travel(travel)
              .user(user)
              .comment(travelPeople.getComment())
              .build());
    }

    travelPeopleService.createOrUpdateAll(toSave);
  }

  private int requirePositive(Integer value, String fieldName) {
    if (value == null || value <= 0) {
      throw new BadRequestException(fieldName + " must be greater than zero");
    }
    return value;
  }

  private String requireId(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new BadRequestException(fieldName + " is required");
    }
    return value;
  }
}
