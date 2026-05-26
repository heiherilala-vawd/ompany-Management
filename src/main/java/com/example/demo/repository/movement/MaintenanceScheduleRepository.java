package com.example.demo.repository.movement;

import com.example.demo.model.movement.MaintenanceSchedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceScheduleRepository
    extends JpaRepository<MaintenanceSchedule, String>,
        JpaSpecificationExecutor<MaintenanceSchedule> {
  List<MaintenanceSchedule> findByEquipmentId(String equipmentId);

  List<MaintenanceSchedule> findByCompanyIdAndScheduledDateAfter(String companyId, LocalDate date);

  List<MaintenanceSchedule> findByCompanyIdAndStatus(
      String companyId, MaintenanceSchedule.MaintenanceScheduleStatus status);
}
