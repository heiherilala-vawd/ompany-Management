package com.example.demo.endpoint.rest.mapper.task;

import com.example.demo.client.model.CrupdateTask;
import com.example.demo.client.model.Task;
import com.example.demo.endpoint.rest.mapper.CompanyMapper;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.model.Company;
import com.example.demo.model.task.TaskAssignment;
import com.example.demo.model.task.TaskPriority;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskMapper {

  private final CompanyMapper companyMapper;
  private final com.example.demo.endpoint.rest.mapper.UserMapper userMapper;

  public com.example.demo.model.task.Task toDomain(CrupdateTask rest, String companyId) {
    if (rest == null) return null;
    return com.example.demo.model.task.Task.builder()
        .id(rest.getId())
        .title(rest.getTitle())
        .description(rest.getDescription())
        .dueDate(rest.getDueDate())
        .priority(
            rest.getPriority() != null ? TaskPriority.valueOf(rest.getPriority().getValue()) : null)
        .completed(rest.getCompleted() != null ? rest.getCompleted() : false)
        .company(companyId != null ? Company.builder().id(companyId).build() : null)
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
      rest.setPriority(
          com.example.demo.client.model.TaskPriority.fromValue(domain.getPriority().name()));
    }
    rest.setCompany(companyMapper.toRestCrupdateCompany(domain.getCompany()));
    rest.setCompleted(domain.getCompleted());
    rest.setCompletedAt(domain.getCompletedAt());
    if (assignments != null) {
      rest.setAssignedUsers(
          assignments.stream()
              .map(a -> userMapper.toRestCrupdateUser(a.getUser()))
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

  public CrupdateTask toRestCrupdateTask(com.example.demo.model.task.Task domain) {
    if (domain == null) return null;
    return new CrupdateTask()
        .id(domain.getId())
        .title(domain.getTitle())
        .description(domain.getDescription())
        .companyId(domain.getCompany() != null ? domain.getCompany().getId() : null);
  }

  public List<Task> toRestTasks(List<com.example.demo.model.task.Task> domains) {
    return domains.stream().map(t -> toRestTask(t, null)).collect(Collectors.toList());
  }
}
