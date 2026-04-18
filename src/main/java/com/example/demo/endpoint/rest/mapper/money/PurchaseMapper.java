package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.client.model.CrupdatePurchase;
import com.example.demo.client.model.Purchase;
import com.example.demo.service.money.ExpenseMoneyService;
import com.example.demo.service.movement.EquipmentService;
import com.example.demo.service.movement.MaterialService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PurchaseMapper {

  private final ExpenseMoneyService expenseMoneyService;
  private final EquipmentService equipmentService;
  private final MaterialService materialService;

  public com.example.demo.model.money.Purchase toDomain(Purchase restPurchase) {
    if (restPurchase == null) return null;

    return com.example.demo.model.money.Purchase.builder()
        .id(restPurchase.getId())
        .expense(
            restPurchase.getExpenseId() != null
                ? expenseMoneyService.findById(restPurchase.getExpenseId()).orElse(null)
                : null)
        .supplier(restPurchase.getSupplier())
        .equipment(
            restPurchase.getEquipment() != null
                ? equipmentService.findById(restPurchase.getEquipment()).orElse(null)
                : null)
        .material(
            restPurchase.getMaterial() != null
                ? materialService.findById(restPurchase.getMaterial()).orElse(null)
                : null)
        .quantity(restPurchase.getQuantity())
        .isEquipment(restPurchase.getIsEquipment())
        .build();
  }

  public com.example.demo.model.money.Purchase toDomain(CrupdatePurchase restPurchase) {
    if (restPurchase == null) return null;

    return com.example.demo.model.money.Purchase.builder()
        .id(restPurchase.getId())
        .expense(
            restPurchase.getExpenseId() != null
                ? expenseMoneyService.findById(restPurchase.getExpenseId()).orElse(null)
                : null)
        .supplier(restPurchase.getSupplier())
        .equipment(
            restPurchase.getEquipment() != null
                ? equipmentService.findById(restPurchase.getEquipment()).orElse(null)
                : null)
        .material(
            restPurchase.getMaterial() != null
                ? materialService.findById(restPurchase.getMaterial()).orElse(null)
                : null)
        .quantity(restPurchase.getQuantity())
        .isEquipment(restPurchase.getIsEquipment())
        .build();
  }

  public Purchase toRestPurchase(com.example.demo.model.money.Purchase domainPurchase) {
    if (domainPurchase == null) return null;

    Purchase restPurchase = new Purchase();
    restPurchase.setId(domainPurchase.getId());
    restPurchase.setExpenseId(
        domainPurchase.getExpense() != null ? domainPurchase.getExpense().getId() : null);
    restPurchase.setSupplier(domainPurchase.getSupplier());
    restPurchase.setEquipment(
        domainPurchase.getEquipment() != null ? domainPurchase.getEquipment().getId() : null);
    restPurchase.setMaterial(
        domainPurchase.getMaterial() != null ? domainPurchase.getMaterial().getId() : null);
    restPurchase.setQuantity(domainPurchase.getQuantity());
    restPurchase.setIsEquipment(domainPurchase.getIsEquipment());

    return restPurchase;
  }

  public List<Purchase> toRestPurchases(
      List<com.example.demo.model.money.Purchase> domainPurchases) {
    return domainPurchases.stream().map(this::toRestPurchase).toList();
  }
}
