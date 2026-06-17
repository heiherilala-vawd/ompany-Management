package com.example.demo.endpoint.rest.mapper.notification;

import com.example.demo.client.model.CrupdateNotification;
import com.example.demo.client.model.Notification;
import com.example.demo.endpoint.rest.mapper.RestAuditMapperUtils;
import com.example.demo.endpoint.rest.mapper.UserMapper;
import com.example.demo.endpoint.rest.mapper.task.TaskMapper;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotificationMapper {

  private final UserMapper userMapper;
  private final TaskMapper taskMapper;

  public Notification toRestNotification(com.example.demo.model.notification.Notification domain) {
    if (domain == null) return null;
    var rest = new Notification();
    rest.setId(domain.getId());
    rest.setUser(userMapper.toRestCrupdateUser(domain.getUser()));
    rest.setTask(taskMapper.toRestCrupdateTask(domain.getTask()));
    rest.setTitle(domain.getTitle());
    rest.setMessage(domain.getMessage());
    rest.setRead(domain.getRead());
    rest.setReadAt(domain.getReadAt());
    rest.setCompleted(domain.getCompleted());
    rest.setCompletedAt(domain.getCompletedAt());
    RestAuditMapperUtils.mapAuditFields(
        domain,
        rest::setCreatedAt,
        rest::setUpdatedAt,
        rest::setComment,
        rest::setCreatedBy,
        rest::setUpdatedBy);
    return rest;
  }

  public List<Notification> toRestNotifications(
      List<com.example.demo.model.notification.Notification> domains) {
    return domains.stream().map(this::toRestNotification).toList();
  }

  public com.example.demo.model.notification.Notification toDomain(
      CrupdateNotification rest,
      com.example.demo.model.User user,
      com.example.demo.model.task.Task task) {
    if (rest == null) return null;
    var builder =
        com.example.demo.model.notification.Notification.builder()
            .id(rest.getId())
            .user(user)
            .task(task)
            .title(rest.getTitle())
            .message(rest.getMessage())
            .read(rest.getRead() != null ? rest.getRead() : false)
            .readAt(rest.getReadAt())
            .completed(rest.getCompleted() != null ? rest.getCompleted() : false)
            .completedAt(rest.getCompletedAt());
    return builder.build();
  }
}
