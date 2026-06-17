package com.example.demo.endpoint.rest.controller.task;

import com.example.demo.client.model.CrupdateTaskSchedule;
import com.example.demo.client.model.TaskSchedule;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.task.TaskScheduleMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.task.TaskScheduleService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TaskScheduleController {

  private final TaskScheduleService taskScheduleService;
  private final TaskScheduleMapper taskScheduleMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/task_schedules")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getTaskSchedules(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    var result = taskScheduleService.findAll(page, pageSize, companyId);
    return new PaginatedResponse(
        result.stream().map(taskScheduleMapper::toRestTaskSchedule).toList(),
        (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/task_schedules")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<TaskSchedule> crupdateTaskSchedules(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateTaskSchedule> toWrite) {
    List<com.example.demo.model.task.TaskSchedule> schedules =
        taskScheduleService.createOrUpdateAll(
            toWrite.stream().map(rest -> taskScheduleMapper.toDomain(rest, companyId)).toList());
    for (int i = 0; i < toWrite.size(); i++) {
      CrupdateTaskSchedule rest = toWrite.get(i);
      com.example.demo.model.task.TaskSchedule schedule = schedules.get(i);
      if (rest.getAssignedUserIds() != null) {
        taskScheduleService.syncAssignedUsers(schedule, rest.getAssignedUserIds());
      }
    }
    return schedules.stream().map(taskScheduleMapper::toRestTaskSchedule).toList();
  }

  @GetMapping("/users/{userId}/companies/{companyId}/task_schedules/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TaskSchedule getTaskScheduleById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    com.example.demo.model.task.TaskSchedule schedule =
        taskScheduleService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("TaskSchedule " + id + " not found"));
    return taskScheduleMapper.toRestTaskSchedule(schedule);
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/task_schedules/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteTaskScheduleById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    taskScheduleService.deleteById(id);
  }
}
