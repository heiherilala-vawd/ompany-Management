package com.example.demo.endpoint.rest.controller.task;

import com.example.demo.client.model.CrupdateTask;
import com.example.demo.client.model.Task;
import com.example.demo.endpoint.rest.mapper.task.TaskMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.task.TaskAssignment;
import com.example.demo.service.task.TaskService;
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

  @GetMapping("/companies/{comp_id}/tasks")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Task> getTasks(
      @PathVariable String comp_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return taskService.findAll(page, pageSize, comp_id).stream()
        .map(
            t -> {
              List<TaskAssignment> assignments = taskService.getAssignments(t.getId());
              return taskMapper.toRestTask(t, assignments);
            })
        .toList();
  }

  @PutMapping("/companies/{comp_id}/tasks")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Task> crupdateTasks(
      @PathVariable String comp_id, @Valid @RequestBody List<CrupdateTask> toWrite) {
    List<com.example.demo.model.task.Task> tasks =
        taskService.createOrUpdateAll(
            toWrite.stream().map(rest -> taskMapper.toDomain(rest, comp_id)).toList());
    for (int i = 0; i < toWrite.size(); i++) {
      CrupdateTask rest = toWrite.get(i);
      com.example.demo.model.task.Task task = tasks.get(i);
      if (rest.getAssignedUserIds() != null) {
        taskService.syncAssignments(task.getId(), rest.getAssignedUserIds());
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

  @GetMapping("/companies/{comp_id}/tasks/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public Task getTaskById(@PathVariable String comp_id, @PathVariable String id) {
    com.example.demo.model.task.Task task =
        taskService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Task " + id + " not found"));
    List<TaskAssignment> assignments = taskService.getAssignments(task.getId());
    return taskMapper.toRestTask(task, assignments);
  }

  @DeleteMapping("/companies/{comp_id}/tasks/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteTaskById(@PathVariable String comp_id, @PathVariable String id) {
    taskService.deleteById(id);
  }
}
