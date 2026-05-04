package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdateTravelExpense;
import com.example.demo.client.model.TravelExpense;
import com.example.demo.endpoint.rest.mapper.movement.WarehouseMapper;
import com.example.demo.service.money.ExpenseMoneyService;
import com.example.demo.service.movement.WarehouseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TravelExpenseMapper {

  private final ExpenseMoneyService expenseMoneyService;
  private final ExpenseMoneyMapper expenseMoneyMapper;
  private final WarehouseService warehouseService;
  private final WarehouseMapper warehouseMapper;

  public com.example.demo.model.money.TravelExpense toDomain(TravelExpense restTravelExpense) {
    if (restTravelExpense == null) return null;

    return com.example.demo.model.money.TravelExpense.builder()
        .id(restTravelExpense.getId())
        .expense(
            restTravelExpense.getExpense() != null && restTravelExpense.getExpense().getId() != null
                ? expenseMoneyService.findById(restTravelExpense.getExpense().getId()).orElse(null)
                : null)
        .departureLocation(
            restTravelExpense.getDepartureLocation() != null
                    && restTravelExpense.getDepartureLocation().getId() != null
                ? warehouseService
                    .findById(restTravelExpense.getDepartureLocation().getId())
                    .orElse(null)
                : null)
        .arrivalLocation(
            restTravelExpense.getArrivalLocation() != null
                    && restTravelExpense.getArrivalLocation().getId() != null
                ? warehouseService
                    .findById(restTravelExpense.getArrivalLocation().getId())
                    .orElse(null)
                : null)
        .departureDate(restTravelExpense.getDepartureDate())
        .arrivalDate(restTravelExpense.getArrivalDate())
        .build();
  }

  public com.example.demo.model.money.TravelExpense toDomain(
      CrupdateTravelExpense restTravelExpense) {
    if (restTravelExpense == null) return null;

    return com.example.demo.model.money.TravelExpense.builder()
        .id(restTravelExpense.getId())
        .expense(expenseMoneyMapper.toDomain(restTravelExpense.getExpense()))
        .departureLocation(
            restTravelExpense.getDepartureLocation() != null
                    && restTravelExpense.getDepartureLocation().getId() != null
                ? warehouseService
                    .findById(restTravelExpense.getDepartureLocation().getId())
                    .orElse(null)
                : null)
        .arrivalLocation(
            restTravelExpense.getArrivalLocation() != null
                    && restTravelExpense.getArrivalLocation().getId() != null
                ? warehouseService
                    .findById(restTravelExpense.getArrivalLocation().getId())
                    .orElse(null)
                : null)
        .departureDate(restTravelExpense.getDepartureDate())
        .arrivalDate(restTravelExpense.getArrivalDate())
        .build();
  }

  public TravelExpense toRestTravelExpense(
      com.example.demo.model.money.TravelExpense domainTravelExpense) {
    if (domainTravelExpense == null) return null;

    TravelExpense restTravelExpense = new TravelExpense();
    restTravelExpense.setId(domainTravelExpense.getId());
    restTravelExpense.setExpense(
        expenseMoneyMapper.toRestCrupdateExpense(domainTravelExpense.getExpense()));
    restTravelExpense.setDepartureLocation(
        warehouseMapper.toRestCrupdateWarehouse(domainTravelExpense.getDepartureLocation()));
    restTravelExpense.setArrivalLocation(
        warehouseMapper.toRestCrupdateWarehouse(domainTravelExpense.getArrivalLocation()));
    restTravelExpense.setDepartureDate(domainTravelExpense.getDepartureDate());
    restTravelExpense.setArrivalDate(domainTravelExpense.getArrivalDate());

    return restTravelExpense;
  }

  public CrupdateTravelExpense toRestCrupdateTravelExpense(
      com.example.demo.model.money.TravelExpense domainTravelExpense) {
    if (domainTravelExpense == null) return null;

    return new CrupdateTravelExpense()
        .id(domainTravelExpense.getId())
        .expense(expenseMoneyMapper.toRestCrupdateExpense(domainTravelExpense.getExpense()))
        .departureLocation(
            warehouseMapper.toRestCrupdateWarehouse(domainTravelExpense.getDepartureLocation()))
        .arrivalLocation(
            warehouseMapper.toRestCrupdateWarehouse(domainTravelExpense.getArrivalLocation()))
        .departureDate(domainTravelExpense.getDepartureDate())
        .arrivalDate(domainTravelExpense.getArrivalDate());
  }
}
