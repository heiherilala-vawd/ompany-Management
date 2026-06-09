package com.example.demo.endpoint.rest.controller.notification;

import com.example.demo.client.model.CrupdateNotification;
import com.example.demo.client.model.Notification;
import com.example.demo.endpoint.rest.mapper.notification.NotificationMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.notification.NotificationService;
import com.example.demo.service.task.TaskService;
import com.example.demo.service.utils.ModificationUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;
  private final NotificationMapper notificationMapper;
  private final ModificationUtils modificationUtils;
  private final TaskService taskService;

  @GetMapping("/users/{userId}/companies/{companyId}/notifications")
  @PreAuthorize("isAuthenticated()")
  public List<Notification> getNotifications(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "read", required = false) Boolean read,
      @RequestParam(name = "completed", required = false) Boolean completed) {
    User currentUser = modificationUtils.takePrimaryUser();
    List<com.example.demo.model.notification.Notification> domainNotifs;
    if (read != null && completed != null) {
      domainNotifs =
          notificationService.findByUserIdAndReadAndCompleted(
              currentUser.getId(), read, completed, page, pageSize);
    } else if (read != null) {
      domainNotifs =
          notificationService.findByUserIdAndRead(currentUser.getId(), read, page, pageSize);
    } else if (completed != null) {
      domainNotifs =
          notificationService.findByUserIdAndCompleted(
              currentUser.getId(), completed, page, pageSize);
    } else {
      domainNotifs = notificationService.findByUserId(currentUser.getId(), page, pageSize);
    }
    var restNotifs = notificationMapper.toRestNotifications(domainNotifs);
    for (int i = 0; i < domainNotifs.size(); i++) {
      restNotifs
          .get(i)
          .setEffectiveCompleted(notificationService.isEffectiveCompleted(domainNotifs.get(i)));
    }
    return restNotifs;
  }

  @GetMapping("/users/{userId}/companies/{companyId}/notifications/unread_count")
  @PreAuthorize("isAuthenticated()")
  public java.util.Map<String, Long> getUnreadCount(
      @PathVariable String userId, @PathVariable String companyId) {
    User currentUser = modificationUtils.takePrimaryUser();
    return java.util.Map.of(
        "unread_count", notificationService.countUnreadByUserId(currentUser.getId()));
  }

  @PutMapping("/users/{userId}/companies/{companyId}/notifications")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Notification> crupdateNotifications(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateNotification> toWrite) {
    User currentUser = modificationUtils.takePrimaryUser();
    var notifications =
        toWrite.stream()
            .map(
                rest -> {
                  var task =
                      rest.getTaskId() != null
                          ? taskService.findById(rest.getTaskId()).orElse(null)
                          : null;
                  return notificationMapper.toDomain(rest, currentUser, task);
                })
            .map(notificationService::createOrUpdate)
            .map(notificationMapper::toRestNotification)
            .toList();
    return notifications;
  }

  @GetMapping("/users/{userId}/companies/{companyId}/notifications/{id}")
  @PreAuthorize("isAuthenticated()")
  public Notification getNotificationById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    User currentUser = modificationUtils.takePrimaryUser();
    var domain =
        notificationService
            .findByIdAndUserId(id, currentUser.getId())
            .orElseThrow(() -> new NotFoundException("Notification not found with id: " + id));
    var rest = notificationMapper.toRestNotification(domain);
    rest.setEffectiveCompleted(notificationService.isEffectiveCompleted(domain));
    return rest;
  }

  @PutMapping("/users/{userId}/companies/{companyId}/notifications/{id}/read")
  @PreAuthorize("isAuthenticated()")
  public Notification markAsRead(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    User currentUser = modificationUtils.takePrimaryUser();
    var domain =
        notificationService
            .findByIdAndUserId(id, currentUser.getId())
            .orElseThrow(() -> new NotFoundException("Notification not found with id: " + id));
    domain = notificationService.markAsRead(domain.getId());
    var rest = notificationMapper.toRestNotification(domain);
    rest.setEffectiveCompleted(notificationService.isEffectiveCompleted(domain));
    return rest;
  }

  @PutMapping("/users/{userId}/companies/{companyId}/notifications/{id}/complete")
  @PreAuthorize("isAuthenticated()")
  public Notification markAsCompleted(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    User currentUser = modificationUtils.takePrimaryUser();
    var domain =
        notificationService
            .findByIdAndUserId(id, currentUser.getId())
            .orElseThrow(() -> new NotFoundException("Notification not found with id: " + id));
    domain = notificationService.markAsCompleted(domain.getId());
    var rest = notificationMapper.toRestNotification(domain);
    rest.setEffectiveCompleted(notificationService.isEffectiveCompleted(domain));
    return rest;
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/notifications/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteNotificationById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    notificationService.deleteById(id);
  }
}
