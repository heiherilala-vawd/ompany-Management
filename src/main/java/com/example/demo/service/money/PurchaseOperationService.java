package com.example.demo.service.money;

import com.example.demo.model.money.ExpenseMoney;
import com.example.demo.model.money.TravelExpense;
import com.example.demo.service.movement.EquipmentService;
import com.example.demo.service.movement.MaterialWarehouseService;
import com.example.demo.service.movement.TravelEquipmentService;
import com.example.demo.service.movement.TravelMaterialsService;
import com.example.demo.service.movement.TravelPeopleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOperationService {

  private final ExpenseMoneyService expenseMoneyService;
  private final PurchaseService purchaseService;
  private final TravelExpenseService travelExpenseService;
  private final TravelPeopleService travelPeopleService;
  private final TravelMaterialsService travelMaterialsService;
  private final TravelEquipmentService travelEquipmentService;
  private final EquipmentService equipmentService;
  private final MaterialWarehouseService materialWarehouseService;

  @Transactional
  public void create(PurchaseOperationAggregate aggregate) {
    expenseMoneyService.createOrUpdateAll(aggregate.purchaseExpenses());
    purchaseService.createOrUpdateAll(aggregate.purchases());
    equipmentService.createOrUpdateAll(aggregate.equipmentToUpdate());
    materialWarehouseService.incrementQuantities(aggregate.materialWarehouses());

    ExpenseMoney travelExpenseMoney = aggregate.travelExpenseMoney();
    TravelExpense travelExpense = aggregate.travelExpense();
    if (travelExpenseMoney == null || travelExpense == null) {
      return;
    }

    expenseMoneyService.createOrUpdateAll(java.util.List.of(travelExpenseMoney));
    travelExpenseService.createOrUpdateAll(java.util.List.of(travelExpense));

    if (!aggregate.travelPeople().isEmpty()) {
      travelPeopleService.createOrUpdateAll(aggregate.travelPeople());
    }
    if (!aggregate.travelMaterials().isEmpty()) {
      travelMaterialsService.createOrUpdateAll(aggregate.travelMaterials());
    }
    if (!aggregate.travelEquipment().isEmpty()) {
      travelEquipmentService.createOrUpdateAll(aggregate.travelEquipment());
    }
  }
}
