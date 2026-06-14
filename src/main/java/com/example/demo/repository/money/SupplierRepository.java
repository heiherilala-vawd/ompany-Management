package com.example.demo.repository.money;

import com.example.demo.model.money.Supplier;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository
    extends JpaRepository<Supplier, String>, JpaSpecificationExecutor<Supplier> {
  List<Supplier> findByCompany_Id(String companyId);

  Page<Supplier> findByCompany_Id(String companyId, Pageable pageable);
}
