package com.example.demo.repository.notification;

import com.example.demo.model.notification.Notification;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

  Page<Notification> findByUserId(String userId, Pageable pageable);

  Page<Notification> findByUserIdAndRead(String userId, Boolean read, Pageable pageable);

  Page<Notification> findByUserIdAndCompleted(String userId, Boolean completed, Pageable pageable);

  Page<Notification> findByUserIdAndReadAndCompleted(
      String userId, Boolean read, Boolean completed, Pageable pageable);

  Optional<Notification> findByIdAndUserId(String id, String userId);

  Optional<Notification> findByUserIdAndTaskId(String userId, String taskId);

  long countByUserIdAndRead(String userId, Boolean read);

  void deleteByTaskId(String taskId);
}
