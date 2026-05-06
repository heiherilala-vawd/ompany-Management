package com.example.demo.repository.money;

import com.example.demo.model.money.IncomeType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeTypeRepository extends JpaRepository<IncomeType, String> {
  List<IncomeType> findByCompanyIdOrderByName(String companyId);
}
