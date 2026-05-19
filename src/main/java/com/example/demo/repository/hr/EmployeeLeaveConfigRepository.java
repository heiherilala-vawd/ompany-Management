package com.example.demo.repository.hr;

import com.example.demo.model.hr.EmployeeLeaveConfig;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeLeaveConfigRepository extends JpaRepository<EmployeeLeaveConfig, String> {

  List<EmployeeLeaveConfig> findByCompanyId(String companyId);
}
