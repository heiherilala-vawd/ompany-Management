package com.example.demo.service.task;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.task.TaskSchedule;
import com.example.demo.repository.task.TaskScheduleRepository;
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
public class TaskScheduleService {

  private final TaskScheduleRepository taskScheduleRepository;
  private final ModificationUtils modificationUtils;

  public Optional<TaskSchedule> findById(String id) {
    return taskScheduleRepository.findById(id);
  }

  public List<TaskSchedule> findByCompanyId(String companyId) {
    return taskScheduleRepository.findByCompanyId(companyId);
  }

  public Page<TaskSchedule> findAll(PageFromOne page, BoundedPageSize pageSize, String companyId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    List<TaskSchedule> schedules = taskScheduleRepository.findByCompanyId(companyId);
    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), schedules.size());
    return new PageImpl<>(schedules.subList(start, end), pageable, schedules.size());
  }

  @Transactional
  public List<TaskSchedule> createOrUpdateAll(List<TaskSchedule> schedules) {
    for (TaskSchedule schedule : schedules) {
      TaskSchedule existing = taskScheduleRepository.findById(schedule.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          schedule, existing, schedule.getId(), modificationUtils.takePrimaryUser());
    }
    return taskScheduleRepository.saveAll(schedules);
  }

  @Transactional
  public void deleteById(String id) {
    taskScheduleRepository.deleteById(id);
  }

  @Transactional
  public void syncAssignedUsers(TaskSchedule schedule, List<String> userIds) {
    schedule.getAssignedUsers().clear();
    for (String userId : userIds) {
      schedule.getAssignedUsers().add(User.builder().id(userId).build());
    }
    taskScheduleRepository.save(schedule);
  }
}
