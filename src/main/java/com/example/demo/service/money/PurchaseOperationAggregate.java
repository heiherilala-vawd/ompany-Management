package com.example.demo.service.money;

import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.money.Purchase;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.TravelPeople;
import java.util.List;

public record PurchaseOperationAggregate(
    List<ExpenseMoney> purchaseExpenses,
    List<Purchase> purchases,
    List<Equipment> equipmentToUpdate,
    List<MaterialWarehouse> materialWarehouses,
    ExpenseMoney travelExpenseMoney,
    TravelExpense travelExpense,
    List<TravelPeople> travelPeople,
    List<TravelMaterials> travelMaterials,
    List<TravelEquipment> travelEquipment) {}
