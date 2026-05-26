package com.example.demo.repository.money;

import com.example.demo.model.money.PurchaseOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository
    extends JpaRepository<PurchaseOrder, String>, JpaSpecificationExecutor<PurchaseOrder> {
  List<PurchaseOrder> findByCompany_Id(String companyId);

  List<PurchaseOrder> findBySupplier_Id(String supplierId);
}
