package com.example.demo.repository.movement;

import com.example.demo.model.movement.Maintenance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRepository
    extends JpaRepository<Maintenance, String>, JpaSpecificationExecutor<Maintenance> {
  Optional<Maintenance> findByExpenseId(String expenseId);

  Page<Maintenance> findByEquipmentId(String equipmentId, Pageable pageable);

  Page<Maintenance> findByEquipmentIdAndDescriptionContainingIgnoreCase(
      String equipmentId, String description, Pageable pageable);

  @Query(
      "SELECT m.equipment.id, m.equipment.name, "
          + "COALESCE(SUM(m.expense.amount), 0), COUNT(m.id) "
          + "FROM Maintenance m GROUP BY m.equipment.id, m.equipment.name")
  List<Object[]> findTotalCostByEquipment();
}
