package com.example.demo.service.notification;

import com.example.demo.model.User;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.notification.Notification;
import com.example.demo.model.task.Task;
import com.example.demo.repository.notification.NotificationRepository;
import com.example.demo.repository.task.TaskRepository;
import com.example.demo.service.utils.ModificationUtils;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final TaskRepository taskRepository;
  private final ModificationUtils modificationUtils;

  public Page<Notification> findByUserId(String userId, Pageable pageable) {
    return notificationRepository.findByUserId(userId, pageable);
  }

  public Page<Notification> findByUserIdAndRead(String userId, Boolean read, Pageable pageable) {
    return notificationRepository.findByUserIdAndRead(userId, read, pageable);
  }

  public Page<Notification> findByUserIdAndCompleted(
      String userId, Boolean completed, Pageable pageable) {
    return notificationRepository.findByUserIdAndCompleted(userId, completed, pageable);
  }

  public Page<Notification> findByUserIdAndReadAndCompleted(
      String userId, Boolean read, Boolean completed, Pageable pageable) {
    return notificationRepository.findByUserIdAndReadAndCompleted(
        userId, read, completed, pageable);
  }

  public Optional<Notification> findById(String id) {
    return notificationRepository.findById(id);
  }

  public Optional<Notification> findByIdAndUserId(String id, String userId) {
    return notificationRepository.findByIdAndUserId(id, userId);
  }

  public long countUnreadByUserId(String userId) {
    return notificationRepository.countByUserIdAndRead(userId, false);
  }

  @Transactional
  public Notification createOrUpdate(Notification notification) {
    var existing = notificationRepository.findById(notification.getId()).orElse(null);
    var creator = modificationUtils.takePrimaryUser();
    if (existing == null) {
      notification.setCreatedAt(Instant.now());
      notification.setCreatedBy(creator);
    } else {
      notification.setCreatedAt(existing.getCreatedAt());
      notification.setCreatedBy(existing.getCreatedBy());
    }
    notification.setUpdatedAt(Instant.now());
    notification.setUpdatedBy(creator);
    return notificationRepository.save(notification);
  }

  @Transactional
  public Notification createForUser(
      User user, String title, String message, Task task, User creator) {
    var existing = notificationRepository.findByUserIdAndTaskId(user.getId(), task.getId());
    if (existing.isPresent()) {
      var notification = existing.get();
      notification.setTitle(title);
      notification.setMessage(message);
      return notification;
    }
    var notification =
        Notification.builder()
            .id(UUID.randomUUID().toString())
            .user(user)
            .task(task)
            .title(title)
            .message(message)
            .read(false)
            .completed(false)
            .build();
    return saveWithCreator(notification, creator);
  }

  @Transactional
  public Notification saveWithCreator(Notification notification, User creator) {
    var existing = notificationRepository.findById(notification.getId()).orElse(null);
    if (existing == null) {
      notification.setCreatedAt(Instant.now());
      notification.setCreatedBy(creator);
    } else {
      notification.setCreatedAt(existing.getCreatedAt());
      notification.setCreatedBy(existing.getCreatedBy());
    }
    notification.setUpdatedAt(Instant.now());
    notification.setUpdatedBy(creator);
    return notificationRepository.save(notification);
  }

  @Transactional
  public Notification markAsRead(String id) {
    var notification =
        notificationRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Notification not found with id: " + id));
    notification.setRead(true);
    notification.setReadAt(Instant.now());
    return notificationRepository.save(notification);
  }

  @Transactional
  public Notification markAsCompleted(String id) {
    var notification =
        notificationRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Notification not found with id: " + id));
    notification.setCompleted(true);
    notification.setCompletedAt(Instant.now());
    return notificationRepository.save(notification);
  }

  @Transactional(readOnly = true)
  public boolean isEffectiveCompleted(Notification notification) {
    if (Boolean.TRUE.equals(notification.getCompleted())) return true;
    if (notification.getTask() == null) return false;
    return taskRepository.findCompletedById(notification.getTask().getId()).orElse(false);
  }

  @Transactional
  public void deleteById(String id) {
    notificationRepository.deleteById(id);
  }

  @Transactional
  public void deleteByTaskId(String taskId) {
    notificationRepository.deleteByTaskId(taskId);
  }
}
