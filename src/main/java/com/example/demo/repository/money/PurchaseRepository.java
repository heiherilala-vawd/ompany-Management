package com.example.demo.repository.money;

import com.example.demo.model.money.Purchase;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, String> {
  Page<Purchase> findByExpenseId(String expenseId, Pageable pageable);

  List<Purchase> findByExpenseId(String expenseId);

  Page<Purchase> findBySupplierContainingIgnoreCase(String supplier, Pageable pageable);

  List<Purchase> findByEquipmentId(String equipmentId);

  List<Purchase> findByMaterialId(String materialId);

  Page<Purchase> findByIsEquipment(Boolean isEquipment, Pageable pageable);
}
