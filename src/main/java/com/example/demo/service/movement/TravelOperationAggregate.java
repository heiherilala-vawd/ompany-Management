package com.example.demo.service.movement;

import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.TravelPeople;
import com.example.demo.model.movement.Warehouse;
import java.util.List;

public record TravelOperationAggregate(
    TravelExpense travel,
    ExpenseMoney travelExpenseMoney,
    Warehouse departureWarehouse,
    Warehouse arrivalWarehouse,
    List<TravelEquipment> travelEquipment,
    List<TravelMaterials> travelMaterials,
    List<TravelPeople> travelPeople) {}
