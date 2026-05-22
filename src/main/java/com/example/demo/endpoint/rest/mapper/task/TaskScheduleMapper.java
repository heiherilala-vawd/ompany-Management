package com.example.demo.endpoint.rest.mapper.task;

import com.example.demo.client.model.CrupdateTaskSchedule;
import com.example.demo.client.model.TaskSchedule;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.model.Company;
import com.example.demo.model.User;
import com.example.demo.model.task.ScheduleStatus;
import com.example.demo.model.task.TaskPriority;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskScheduleMapper {

  public com.example.demo.model.task.TaskSchedule toDomain(CrupdateTaskSchedule rest) {
    if (rest == null) return null;
    com.example.demo.model.task.TaskSchedule.TaskScheduleBuilder<?, ?> builder =
        com.example.demo.model.task.TaskSchedule.builder()
            .id(rest.getId())
            .title(rest.getTitle())
            .description(rest.getDescription())
            .frequency(rest.getFrequency())
            .scheduledDate(rest.getScheduledDate())
            .status(ScheduleStatus.PENDING)
            .comment(rest.getComment());
    if (rest.getPriority() != null) {
      builder.priority(TaskPriority.valueOf(rest.getPriority().getValue()));
    }
    if (rest.getCompanyId() != null) {
      builder.company(Company.builder().id(rest.getCompanyId()).build());
    }
    com.example.demo.model.task.TaskSchedule domain = builder.build();
    if (rest.getAssignedUserIds() != null) {
      domain.setAssignedUsers(
          rest.getAssignedUserIds().stream()
              .map(uid -> User.builder().id(uid).build())
              .collect(Collectors.toList()));
    }
    return domain;
  }

  public TaskSchedule toRestTaskSchedule(com.example.demo.model.task.TaskSchedule domain) {
    if (domain == null) return null;
    TaskSchedule rest = new TaskSchedule();
    rest.setId(domain.getId());
    rest.setTitle(domain.getTitle());
    rest.setDescription(domain.getDescription());
    if (domain.getPriority() != null) {
      rest.setPriority(
          com.example.demo.client.model.TaskPriority.fromValue(domain.getPriority().name()));
    }
    rest.setFrequency(domain.getFrequency());
    rest.setScheduledDate(domain.getScheduledDate());
    if (domain.getStatus() != null) {
      rest.setStatus(
          com.example.demo.client.model.ScheduleStatus.fromValue(domain.getStatus().name()));
    }
    rest.setCompanyId(domain.getCompany() != null ? domain.getCompany().getId() : null);
    if (domain.getAssignedUsers() != null) {
      rest.setAssignedUserIds(
          domain.getAssignedUsers().stream().map(User::getId).collect(Collectors.toList()));
    }
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);
    return rest;
  }
}
