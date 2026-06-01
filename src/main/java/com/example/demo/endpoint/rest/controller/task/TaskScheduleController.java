package com.example.demo.endpoint.rest.controller.task;

import com.example.demo.client.model.CrupdateTaskSchedule;
import com.example.demo.client.model.TaskSchedule;
import com.example.demo.endpoint.rest.mapper.task.TaskScheduleMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.task.TaskScheduleService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TaskScheduleController {

  private final TaskScheduleService taskScheduleService;
  private final TaskScheduleMapper taskScheduleMapper;

  @GetMapping("/companies/{comp_id}/task_schedules")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<TaskSchedule> getTaskSchedules(
      @PathVariable String comp_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return taskScheduleService.findAll(page, pageSize, comp_id).stream()
        .map(taskScheduleMapper::toRestTaskSchedule)
        .toList();
  }

  @PutMapping("/companies/{comp_id}/task_schedules")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<TaskSchedule> crupdateTaskSchedules(
      @PathVariable String comp_id, @Valid @RequestBody List<CrupdateTaskSchedule> toWrite) {
    List<com.example.demo.model.task.TaskSchedule> schedules =
        taskScheduleService.createOrUpdateAll(
            toWrite.stream().map(rest -> taskScheduleMapper.toDomain(rest, comp_id)).toList());
    for (int i = 0; i < toWrite.size(); i++) {
      CrupdateTaskSchedule rest = toWrite.get(i);
      com.example.demo.model.task.TaskSchedule schedule = schedules.get(i);
      if (rest.getAssignedUserIds() != null) {
        taskScheduleService.syncAssignedUsers(schedule, rest.getAssignedUserIds());
      }
    }
    return schedules.stream().map(taskScheduleMapper::toRestTaskSchedule).toList();
  }

  @GetMapping("/companies/{comp_id}/task_schedules/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public TaskSchedule getTaskScheduleById(@PathVariable String comp_id, @PathVariable String id) {
    com.example.demo.model.task.TaskSchedule schedule =
        taskScheduleService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("TaskSchedule " + id + " not found"));
    return taskScheduleMapper.toRestTaskSchedule(schedule);
  }

  @DeleteMapping("/companies/{comp_id}/task_schedules/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteTaskScheduleById(@PathVariable String comp_id, @PathVariable String id) {
    taskScheduleService.deleteById(id);
  }
}
