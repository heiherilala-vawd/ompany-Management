package com.example.demo.endpoint.rest.controller.task;

import com.example.demo.client.model.CrupdateTask;
import com.example.demo.client.model.Task;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.task.TaskMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.task.TaskAssignment;
import com.example.demo.service.notification.NotificationService;
import com.example.demo.service.task.TaskService;
import com.example.demo.service.utils.ModificationUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TaskController {

  private final TaskService taskService;
  private final TaskMapper taskMapper;
  private final NotificationService notificationService;
  private final ModificationUtils modificationUtils;

  @GetMapping("/users/{userId}/companies/{companyId}/tasks")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER', 'EMPLOYEE') or #userId == authentication.principal.id")
  public PaginatedResponse getTasks(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    var result = taskService.findAll(page, pageSize, companyId);
    var list =
        result.stream()
            .map(
                t -> {
                  List<TaskAssignment> assignments = taskService.getAssignments(t.getId());
                  return taskMapper.toRestTask(t, assignments);
                })
            .toList();
    return new PaginatedResponse(list, (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/tasks")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Task> crupdateTasks(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateTask> toWrite) {
    List<com.example.demo.model.task.Task> tasks =
        taskService.createOrUpdateAll(
            toWrite.stream().map(rest -> taskMapper.toDomain(rest, companyId)).toList());
    User currentUser = modificationUtils.takePrimaryUser();
    for (int i = 0; i < toWrite.size(); i++) {
      CrupdateTask rest = toWrite.get(i);
      com.example.demo.model.task.Task task = tasks.get(i);
      if (rest.getAssignedUserIds() != null) {
        taskService.syncAssignments(task.getId(), rest.getAssignedUserIds());
        for (String assignedUserId : rest.getAssignedUserIds()) {
          notificationService.createForUser(
              User.builder().id(assignedUserId).build(),
              "Nouvelle tâche : " + task.getTitle(),
              "Vous avez été assigné à la tâche '" + task.getTitle() + "'",
              task,
              currentUser);
        }
      }
    }
    return tasks.stream()
        .map(
            t -> {
              List<TaskAssignment> assignments = taskService.getAssignments(t.getId());
              return taskMapper.toRestTask(t, assignments);
            })
        .toList();
  }

  @GetMapping("/users/{userId}/companies/{companyId}/tasks/{id}")
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER', 'EMPLOYEE') or #userId == authentication.principal.id")
  public Task getTaskById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    com.example.demo.model.task.Task task =
        taskService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Task " + id + " not found"));
    List<TaskAssignment> assignments = taskService.getAssignments(task.getId());
    return taskMapper.toRestTask(task, assignments);
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/tasks/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteTaskById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    taskService.deleteById(id);
  }
}
