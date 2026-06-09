package com.example.demo.service.dashboard;

import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.model.money.IncomeMoney;
import com.example.demo.model.task.Task;
import com.example.demo.model.task.TaskPriority;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.money.IncomeMoneyRepository;
import com.example.demo.repository.task.TaskRepository;
import com.example.demo.service.notification.NotificationService;
import com.example.demo.service.utils.ModificationUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class TaskAutoCreationService {

  private final IncomeMoneyRepository incomeMoneyRepository;
  private final JobRepository jobRepository;
  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final NotificationService notificationService;
  private final ModificationUtils modificationUtils;

  @Scheduled(cron = "0 0 6 * * *")
  @Transactional
  public void autoCreateTasks() {
    log.info("Running TaskAutoCreationService...");
    createInvoiceReminders();
    createClientReminders();
  }

  private void createInvoiceReminders() {
    LocalDate threshold = LocalDate.now().plusDays(7);
    List<IncomeMoney> dueIncomes =
        incomeMoneyRepository.findAll().stream()
            .filter(
                i -> i.getDueDate() != null && i.getDueDate().equals(threshold) && !isFullyPaid(i))
            .toList();

    for (IncomeMoney income : dueIncomes) {
      String title = "Relance client - Facture " + income.getInvoiceReference();
      Task task =
          Task.builder()
              .id(UUID.randomUUID().toString())
              .title(title)
              .description(
                  "Relance automatique pour la facture "
                      + income.getInvoiceReference()
                      + " d'un montant de "
                      + income.getAmount()
                      + " arrivant à échéance le "
                      + income.getDueDate())
              .priority(TaskPriority.MEDIUM)
              .dueDate(income.getDueDate())
              .completed(false)
              .company(income.getJob() != null ? income.getJob().getCompany() : null)
              .build();
      modificationUtils.createOrUpdateModel(
          task, null, task.getId(), modificationUtils.takePrimaryUser());
      taskRepository.save(task);

      var admins =
          userRepository.findAll().stream().filter(u -> u.getRole() == User.Role.ADMIN).toList();
      for (var admin : admins) {
        notificationService.createForUser(
            admin,
            title,
            "Une facture arrive à échéance le " + income.getDueDate(),
            task,
            modificationUtils.takePrimaryUser());
      }
      log.info("Created reminder task for income {}", income.getId());
    }
  }

  private void createClientReminders() {
    List<Job> jobsEnded = jobRepository.findByEndDateBefore(LocalDate.now());

    for (Job job : jobsEnded) {
      BigDecimal totalIncome = incomeMoneyRepository.sumByJobId(job.getId());
      if (totalIncome.compareTo(BigDecimal.ZERO) == 0) {
        String title = "Facturer travaux " + job.getDescription();
        Task task =
            Task.builder()
                .id(UUID.randomUUID().toString())
                .title(title)
                .description(
                    "Les travaux '"
                        + job.getDescription()
                        + "' sont terminés mais "
                        + "aucune facture n'a été émise.")
                .priority(TaskPriority.LOW)
                .dueDate(LocalDate.now().plusDays(15))
                .completed(false)
                .company(job.getCompany())
                .build();
        modificationUtils.createOrUpdateModel(
            task, null, task.getId(), modificationUtils.takePrimaryUser());
        taskRepository.save(task);

        var admins =
            userRepository.findAll().stream().filter(u -> u.getRole() == User.Role.ADMIN).toList();
        for (var admin : admins) {
          notificationService.createForUser(
              admin,
              title,
              "Les travaux '"
                  + job.getDescription()
                  + "' sont terminés mais aucune facture n'a été émise.",
              task,
              modificationUtils.takePrimaryUser());
        }
        log.info("Created invoice reminder for job {}", job.getId());
      }
    }
  }

  private boolean isFullyPaid(IncomeMoney income) {
    return false;
  }
}
