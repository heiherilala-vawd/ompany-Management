package com.example.demo.integration.conf;

import static com.example.demo.integration.conf.TestUtils.*;

import com.example.demo.client.model.CrupdateCompany;
import com.example.demo.client.model.CrupdateTask;
import com.example.demo.client.model.CrupdateUser;
import com.example.demo.client.model.Task;
import com.example.demo.client.model.TaskPriority;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

final class TestTaskFixtures {

  private TestTaskFixtures() {}

  static Task task1() {
    Task task = new Task();
    task.setId("task1_id");
    task.setTitle("Vérifier le matériel");
    task.setDescription("Inventaire du matériel sur le chantier A");
    task.setDueDate(LocalDate.of(2026, 6, 15));
    task.setPriority(TaskPriority.HIGH);
    task.setCompany(new CrupdateCompany().id(COMPANY1_ID));
    task.setCompleted(true);
    task.setAssignedUsers(List.of(new CrupdateUser().id(ADMIN_ID), new CrupdateUser().id(EMPLOYEE_ID)));
    return task;
  }

  static Task task2() {
    Task task = new Task();
    task.setId("task2_id");
    task.setTitle("Maintenance équipement");
    task.setDescription("Maintenance mensuelle des équipements");
    task.setDueDate(LocalDate.of(2026, 7, 1));
    task.setPriority(TaskPriority.MEDIUM);
    task.setCompany(new CrupdateCompany().id(COMPANY1_ID));
    task.setAssignedUsers(List.of(new CrupdateUser().id(EMPLOYEE_ID)));
    return task;
  }

  static CrupdateTask taskToCrupdateTask(Task task) {
    CrupdateTask crupdate = new CrupdateTask();
    crupdate.setId(task.getId());
    crupdate.setTitle(task.getTitle());
    crupdate.setDescription(task.getDescription());
    crupdate.setDueDate(task.getDueDate());
    crupdate.setPriority(task.getPriority());
    crupdate.setAssignedUserIds(
        task.getAssignedUsers() != null
            ? task.getAssignedUsers().stream().map(CrupdateUser::getId).toList()
            : null);
    return crupdate;
  }

  static CrupdateTask someCreatableTask() {
    CrupdateTask crupdate = new CrupdateTask();
    crupdate.setId(UUID.randomUUID().toString());
    crupdate.setTitle("Nouvelle tâche");
    crupdate.setDescription("Description de la nouvelle tâche");
    crupdate.setDueDate(LocalDate.of(2026, 8, 1));
    crupdate.setPriority(TaskPriority.LOW);
    crupdate.setCompanyId(COMPANY1_ID);
    crupdate.setAssignedUserIds(List.of(ADMIN_ID));
    return crupdate;
  }
}
