package com.example.demo.repository.hr;

import com.example.demo.model.hr.LeaveType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, String> {

  List<LeaveType> findByCompanyId(String companyId);
}
