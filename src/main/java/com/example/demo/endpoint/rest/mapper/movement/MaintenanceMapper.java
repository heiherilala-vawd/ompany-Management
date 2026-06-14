package com.example.demo.endpoint.rest.mapper.movement;

import com.example.demo.client.model.CrupdateMaintenance;
import com.example.demo.client.model.Maintenance;
import com.example.demo.endpoint.rest.mapper.money.ExpenseMoneyMapper;
import com.example.demo.service.money.ExpenseMoneyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MaintenanceMapper {

  private final ExpenseMoneyService expenseMoneyService;
  private final ExpenseMoneyMapper expenseMoneyMapper;

  public com.example.demo.model.movement.Maintenance toDomain(Maintenance restMaintenance) {
    if (restMaintenance == null) return null;

    return com.example.demo.model.movement.Maintenance.builder()
        .id(restMaintenance.getId())
        .expense(
            restMaintenance.getExpense() != null && restMaintenance.getExpense().getId() != null
                ? expenseMoneyService.findById(restMaintenance.getExpense().getId()).orElse(null)
                : null)
        .description(restMaintenance.getDescription())
        .build();
  }

  public com.example.demo.model.movement.Maintenance toDomain(CrupdateMaintenance restMaintenance) {
    if (restMaintenance == null) return null;

    return com.example.demo.model.movement.Maintenance.builder()
        .id(restMaintenance.getId())
        .expense(
            restMaintenance.getExpense() != null
                ? expenseMoneyMapper.toDomain(restMaintenance.getExpense())
                : null)
        .description(restMaintenance.getDescription())
        .build();
  }

  public Maintenance toRestMaintenance(
      com.example.demo.model.movement.Maintenance domainMaintenance) {
    if (domainMaintenance == null) return null;

    Maintenance restMaintenance = new Maintenance();
    restMaintenance.setId(domainMaintenance.getId());
    restMaintenance.setExpense(
        expenseMoneyMapper.toRestCrupdateExpense(domainMaintenance.getExpense()));
    restMaintenance.setEquipmentId(
        domainMaintenance.getEquipment() != null ? domainMaintenance.getEquipment().getId() : null);
    restMaintenance.setDescription(domainMaintenance.getDescription());

    return restMaintenance;
  }

  public List<Maintenance> toRestMaintenances(
      List<com.example.demo.model.movement.Maintenance> domainMaintenances) {
    return domainMaintenances.stream().map(this::toRestMaintenance).toList();
  }
}
