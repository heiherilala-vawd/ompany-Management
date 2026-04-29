package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateEquipment;
import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.TravelOperationEquipmentLine;
import com.example.demo.client.model.TravelOperationMaterialLine;
import com.example.demo.client.model.TravelOperationPeopleLine;
import com.example.demo.client.model.TravelOperationRequest;
import com.example.demo.client.model.TravelOperationTravel;
import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Material;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.TravelPeople;
import com.example.demo.model.movement.Warehouse;
import com.example.demo.service.movement.TravelOperationAggregate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TravelOperationMapper {

  public TravelOperationAggregate toAggregate(
      String jobId, String userId, TravelOperationRequest request) {
    if (request == null || request.getTravel() == null) {
      throw new BadRequestException("travel is required");
    }

    Job job = Job.builder().id(jobId).build();
    TravelOperationTravel restTravel = request.getTravel();

    ExpenseMoney travelExpenseMoney =
        restTravel.getExpenseId() != null || restTravel.getFee() != null
            ? ExpenseMoney.builder()
                .id(restTravel.getExpenseId())
                .job(job)
                .amount(restTravel.getFee() != null ? restTravel.getFee() : 0)
                .description("Travel expense")
                .comment(request.getComment())
                .build()
            : null;

    TravelExpense travel =
        TravelExpense.builder()
            .id(restTravel.getId())
            .expense(travelExpenseMoney)
            .departureLocation(toWarehouse(restTravel.getDepartureLocation()))
            .arrivalLocation(toWarehouse(restTravel.getArrivalLocation()))
            .departureDate(restTravel.getDepartureDate())
            .arrivalDate(restTravel.getArrivalDate())
            .build();

    List<TravelEquipment> travelEquipment = new ArrayList<>();
    for (TravelOperationEquipmentLine line : equipmentLines(request)) {
      travelEquipment.add(
          TravelEquipment.builder()
              .id(line.getId())
              .travel(travel)
              .equipment(toEquipment(line.getEquipment()))
              .quantity(1)
              .comment(line.getComment() != null ? line.getComment() : request.getComment())
              .build());
    }

    List<TravelMaterials> travelMaterials = new ArrayList<>();
    for (TravelOperationMaterialLine line : materialLines(request)) {
      travelMaterials.add(
          TravelMaterials.builder()
              .id(line.getId())
              .travel(travel)
              .material(toMaterial(line.getMaterial()))
              .quantity(line.getQuantity())
              .quantityReceived(0)
              .comment(line.getComment() != null ? line.getComment() : request.getComment())
              .build());
    }

    List<TravelPeople> travelPeople = new ArrayList<>();
    for (TravelOperationPeopleLine line : peopleLines(request)) {
      travelPeople.add(
          TravelPeople.builder()
              .id(line.getId())
              .travel(travel)
              .user(User.builder().id(line.getUserId()).build())
              .comment(line.getComment() != null ? line.getComment() : request.getComment())
              .build());
    }

    return new TravelOperationAggregate(
        travel,
        travelExpenseMoney,
        toWarehouse(restTravel.getDepartureLocation()),
        toWarehouse(restTravel.getArrivalLocation()),
        travelEquipment,
        travelMaterials,
        travelPeople);
  }

  private List<TravelOperationEquipmentLine> equipmentLines(TravelOperationRequest request) {
    return request.getEquipmentLines() != null ? request.getEquipmentLines() : List.of();
  }

  private List<TravelOperationMaterialLine> materialLines(TravelOperationRequest request) {
    return request.getMaterialLines() != null ? request.getMaterialLines() : List.of();
  }

  private List<TravelOperationPeopleLine> peopleLines(TravelOperationRequest request) {
    return request.getPeopleLines() != null ? request.getPeopleLines() : List.of();
  }

  private Equipment toEquipment(CrupdateEquipment equipment) {
    if (equipment == null) {
      return null;
    }
    return Equipment.builder()
        .id(equipment.getId())
        .name(equipment.getName())
        .description(equipment.getDescription())
        .warehouse(
            equipment.getWarehouseId() != null
                ? Warehouse.builder().id(equipment.getWarehouseId()).build()
                : null)
        .floorNumber(equipment.getFloorNumber())
        .storageNumber(equipment.getStorageNumber())
        .comment(equipment.getComment())
        .build();
  }

  private Material toMaterial(CrupdateMaterial material) {
    if (material == null) {
      return null;
    }
    return Material.builder()
        .id(material.getId())
        .name(material.getName())
        .description(material.getDescription())
        .unit(
            material.getUnit() != null
                ? com.example.demo.model.movement.Material.Unit.valueOf(material.getUnit().name())
                : null)
        .comment(material.getComment())
        .build();
  }

  private Warehouse toWarehouse(CrupdateWarehouse warehouse) {
    if (warehouse == null) {
      return null;
    }
    return Warehouse.builder()
        .id(warehouse.getId())
        .name(warehouse.getName())
        .description(warehouse.getDescription())
        .job(warehouse.getJobId() != null ? Job.builder().id(warehouse.getJobId()).build() : null)
        .comment(warehouse.getComment())
        .build();
  }
}
