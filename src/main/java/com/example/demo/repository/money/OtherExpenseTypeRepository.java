package com.example.demo.repository.money;

import com.example.demo.model.money.OtherExpenseType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtherExpenseTypeRepository extends JpaRepository<OtherExpenseType, String> {
  List<OtherExpenseType> findByCompanyIdOrderByName(String companyId);

  Page<OtherExpenseType> findByCompanyIdOrderByName(String companyId, Pageable pageable);
}
