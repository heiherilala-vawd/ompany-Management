package com.example.demo.service.movement;

import com.example.demo.model.User;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.movement.MaintenanceSchedule;
import com.example.demo.model.movement.MaintenanceSchedule.MaintenanceScheduleStatus;
import com.example.demo.model.task.Task;
import com.example.demo.model.task.TaskPriority;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.movement.MaintenanceScheduleRepository;
import com.example.demo.repository.task.TaskRepository;
import com.example.demo.service.notification.NotificationService;
import com.example.demo.service.utils.ModificationUtils;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceScheduleSchedulerService {

  private final MaintenanceScheduleRepository maintenanceScheduleRepository;
  private final TaskRepository taskRepository;
  private final ModificationUtils modificationUtils;
  private final UserRepository userRepository;
  private final NotificationService notificationService;

  @Transactional
  @Scheduled(cron = "${maintenance.scheduler.cron:0 0 2 * * ?}")
  public void generateTasksFromSchedules() {
    User systemUser = resolveSystemUser();
    LocalDate today = LocalDate.now();
    var due =
        maintenanceScheduleRepository.findByStatusAndScheduledDateLessThanEqual(
            MaintenanceScheduleStatus.PENDING, today);

    for (MaintenanceSchedule schedule : due) {
      try {
        generateTaskInstance(schedule, today, systemUser);
      } catch (Exception e) {
        log.error("Failed to generate maintenance task from schedule {}", schedule.getId(), e);
      }
    }
  }

  private void generateTaskInstance(
      MaintenanceSchedule schedule, LocalDate today, User systemUser) {
    String equipmentName =
        schedule.getEquipment() != null ? schedule.getEquipment().getName() : "Équipement";
    Task task =
        Task.builder()
            .id(UUID.randomUUID().toString())
            .title("Maintenance : " + equipmentName)
            .description(schedule.getDescription())
            .priority(TaskPriority.MEDIUM)
            .dueDate(today)
            .completed(false)
            .company(schedule.getCompany())
            .featureName("equipment")
            .featureId(schedule.getEquipment() != null ? schedule.getEquipment().getId() : null)
            .maintenanceSchedule(schedule)
            .build();
    modificationUtils.createOrUpdateModel(task, null, task.getId(), systemUser);
    taskRepository.save(task);

    var admins =
        userRepository.findAll().stream().filter(u -> u.getRole() == User.Role.ADMIN).toList();
    for (var admin : admins) {
      notificationService.createForUser(
          admin,
          "Nouvelle maintenance : " + task.getTitle(),
          "Une tâche de maintenance '" + task.getTitle() + "' a été programmée",
          task,
          systemUser);
    }

    LocalDate nextDate = calculateNextDate(schedule.getFrequency(), today);
    schedule.setScheduledDate(nextDate);
    maintenanceScheduleRepository.save(schedule);
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
