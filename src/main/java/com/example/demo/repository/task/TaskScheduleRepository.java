package com.example.demo.repository.task;

import com.example.demo.model.task.ScheduleStatus;
import com.example.demo.model.task.TaskSchedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskScheduleRepository extends JpaRepository<TaskSchedule, String> {

  List<TaskSchedule> findByCompanyId(String companyId);

  List<TaskSchedule> findByStatusAndScheduledDateLessThanEqual(
      ScheduleStatus status, LocalDate scheduledDate);
}
