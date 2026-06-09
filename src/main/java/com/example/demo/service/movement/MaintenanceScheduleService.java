package com.example.demo.service.movement;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.movement.MaintenanceSchedule;
import com.example.demo.repository.movement.MaintenanceScheduleRepository;
import com.example.demo.repository.task.TaskRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaintenanceScheduleService {

  private final MaintenanceScheduleRepository maintenanceScheduleRepository;
  private final ModificationUtils modificationUtils;
  private final TaskRepository taskRepository;

  public Optional<MaintenanceSchedule> findById(String id) {
    return maintenanceScheduleRepository.findById(id);
  }

  public Page<MaintenanceSchedule> findAll(
      PageFromOne page, BoundedPageSize pageSize, String companyId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    List<MaintenanceSchedule> schedules = maintenanceScheduleRepository.findByCompanyId(companyId);
    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), schedules.size());
    return new PageImpl<>(schedules.subList(start, end), pageable, schedules.size());
  }

  @Transactional
  public List<MaintenanceSchedule> createOrUpdateAll(List<MaintenanceSchedule> schedules) {
    for (MaintenanceSchedule schedule : schedules) {
      MaintenanceSchedule existing =
          maintenanceScheduleRepository.findById(schedule.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          schedule, existing, schedule.getId(), modificationUtils.takePrimaryUser());
    }
    var saved = maintenanceScheduleRepository.saveAll(schedules);
    for (MaintenanceSchedule schedule : saved) {
      if (schedule.getEquipment() != null) {
        var tasks =
            taskRepository.findByFeatureNameAndFeatureId(
                "equipment", schedule.getEquipment().getId());
        for (var task : tasks) {
          if (task.getMaintenanceSchedule() == null) {
            task.setMaintenanceSchedule(schedule);
            taskRepository.save(task);
          }
        }
      }
    }
    return saved;
  }

  @Transactional
  public void deleteById(String id) {
    maintenanceScheduleRepository.deleteById(id);
  }
}
