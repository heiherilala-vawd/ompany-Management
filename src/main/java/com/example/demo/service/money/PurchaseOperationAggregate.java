package com.example.demo.service.money;

import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.money.Purchase;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Material;
import com.example.demo.model.movement.MaterialWarehouse;
import com.example.demo.model.movement.TravelEquipment;
import com.example.demo.model.movement.TravelMaterials;
import com.example.demo.model.movement.Warehouse;
import java.util.List;

public record PurchaseOperationAggregate(
    List<Equipment> equipment,
    List<TravelEquipment> travelEquipment,
    List<ExpenseMoney> expenses,
    List<Purchase> purchases,
    List<Material> materials,
    List<TravelMaterials> travelMaterials,
    List<MaterialWarehouse> materialWarehouses,
    Warehouse departureWarehouse,
    Warehouse arrivalWarehouse,
    ExpenseMoney travelExpenseMoney,
    TravelExpense travelExpense) {}
