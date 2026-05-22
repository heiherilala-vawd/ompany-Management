package com.example.demo.service.task;

import com.example.demo.model.User;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.task.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.task.TaskAssignmentRepository;
import com.example.demo.repository.task.TaskRepository;
import com.example.demo.repository.task.TaskScheduleRepository;
import com.example.demo.service.utils.ModificationUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSchedulerService {

  private final TaskScheduleRepository taskScheduleRepository;
  private final TaskRepository taskRepository;
  private final TaskAssignmentRepository taskAssignmentRepository;
  private final ModificationUtils modificationUtils;
  private final UserRepository userRepository;

  @Transactional
  @Scheduled(cron = "${task.scheduler.cron:0 0 2 * * ?}")
  public void generateTasksFromSchedules() {
    User systemUser = resolveSystemUser();
    LocalDate today = LocalDate.now();
    List<TaskSchedule> due =
        taskScheduleRepository.findByStatusAndScheduledDateLessThanEqual(
            ScheduleStatus.PENDING, today);

    for (TaskSchedule schedule : due) {
      try {
        generateTaskInstance(schedule, today, systemUser);
      } catch (Exception e) {
        log.error("Failed to generate task from schedule {}", schedule.getId(), e);
      }
    }
  }

  private void generateTaskInstance(TaskSchedule schedule, LocalDate today, User systemUser) {
    Task task =
        Task.builder()
            .id(UUID.randomUUID().toString())
            .title(schedule.getTitle())
            .description(schedule.getDescription())
            .priority(schedule.getPriority())
            .dueDate(today)
            .completed(false)
            .company(schedule.getCompany())
            .build();
    modificationUtils.createOrUpdateModel(task, null, task.getId(), systemUser);
    taskRepository.save(task);

    List<TaskAssignment> assignments = new ArrayList<>();
    for (User user : schedule.getAssignedUsers()) {
      TaskAssignment assignment =
          TaskAssignment.builder().id(UUID.randomUUID().toString()).task(task).user(user).build();
      modificationUtils.createOrUpdateModel(assignment, null, assignment.getId(), systemUser);
      assignments.add(assignment);
    }
    taskAssignmentRepository.saveAll(assignments);

    schedule.setStatus(ScheduleStatus.GENERATED);
    taskScheduleRepository.save(schedule);

    TaskSchedule nextSchedule =
        TaskSchedule.builder()
            .id(UUID.randomUUID().toString())
            .title(schedule.getTitle())
            .description(schedule.getDescription())
            .priority(schedule.getPriority())
            .frequency(schedule.getFrequency())
            .scheduledDate(calculateNextDate(schedule.getFrequency(), today))
            .status(ScheduleStatus.PENDING)
            .company(schedule.getCompany())
            .assignedUsers(new ArrayList<>(schedule.getAssignedUsers()))
            .build();
    modificationUtils.createOrUpdateModel(nextSchedule, null, nextSchedule.getId(), systemUser);
    taskScheduleRepository.save(nextSchedule);
  }

  private User resolveSystemUser() {
    try {
      return modificationUtils.takePrimaryUser();
    } catch (Exception e) {
      return userRepository.findAll().stream()
          .filter(u -> u.getRole() == User.Role.ADMIN)
          .findFirst()
          .orElseThrow(() -> new NotFoundException("No admin user found for scheduled task"));
    }
  }

  private LocalDate calculateNextDate(String frequency, LocalDate from) {
    try {
      org.springframework.scheduling.support.CronExpression cron =
          org.springframework.scheduling.support.CronExpression.parse(frequency);
      java.time.LocalDateTime next =
          cron.next(
              java.time.LocalDateTime.of(
                  from.getYear(), from.getMonth(), from.getDayOfMonth(), 0, 0));
      if (next != null) {
        return next.toLocalDate();
      }
    } catch (Exception e) {
      log.warn("Failed to parse cron expression: {}", frequency, e);
    }
    return from.plusMonths(1);
  }
}
