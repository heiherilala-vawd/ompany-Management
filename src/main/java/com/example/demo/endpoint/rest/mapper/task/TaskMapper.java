package com.example.demo.endpoint.rest.mapper.task;

import com.example.demo.client.model.CrupdateTask;
import com.example.demo.client.model.Task;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.model.Company;
import com.example.demo.model.task.TaskAssignment;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskMapper {

  public com.example.demo.model.task.Task toDomain(CrupdateTask rest) {
    if (rest == null) return null;
    return com.example.demo.model.task.Task.builder()
        .id(rest.getId())
        .title(rest.getTitle())
        .description(rest.getDescription())
        .dueDate(rest.getDueDate())
        .priority(rest.getPriority() != null ? rest.getPriority().getValue() : null)
        .frequency(rest.getFrequency())
        .company(
            rest.getCompanyId() != null ? Company.builder().id(rest.getCompanyId()).build() : null)
        .comment(rest.getComment())
        .build();
  }

  public Task toRestTask(
      com.example.demo.model.task.Task domain, List<TaskAssignment> assignments) {
    if (domain == null) return null;
    Task rest = new Task();
    rest.setId(domain.getId());
    rest.setTitle(domain.getTitle());
    rest.setDescription(domain.getDescription());
    rest.setDueDate(domain.getDueDate());
    if (domain.getPriority() != null) {
      rest.setPriority(com.example.demo.client.model.TaskPriority.fromValue(domain.getPriority()));
    }
    rest.setFrequency(domain.getFrequency());
    rest.setCompanyId(domain.getCompany() != null ? domain.getCompany().getId() : null);
    if (assignments != null) {
      rest.setAssignedUserIds(
          assignments.stream().map(a -> a.getUser().getId()).collect(Collectors.toList()));
      rest.setCompletedUserIds(
          assignments.stream()
              .filter(a -> Boolean.TRUE.equals(a.getCompleted()))
              .map(a -> a.getUser().getId())
              .collect(Collectors.toList()));
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

  public List<Task> toRestTasks(List<com.example.demo.model.task.Task> domains) {
    return domains.stream().map(t -> toRestTask(t, null)).collect(Collectors.toList());
  }
}
