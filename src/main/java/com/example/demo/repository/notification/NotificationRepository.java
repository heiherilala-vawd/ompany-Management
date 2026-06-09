package com.example.demo.repository.notification;

import com.example.demo.model.notification.Notification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

  List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

  List<Notification> findByUserIdAndReadOrderByCreatedAtDesc(String userId, Boolean read);

  List<Notification> findByUserIdAndCompletedOrderByCreatedAtDesc(String userId, Boolean completed);

  List<Notification> findByUserIdAndReadAndCompletedOrderByCreatedAtDesc(
      String userId, Boolean read, Boolean completed);

  Optional<Notification> findByIdAndUserId(String id, String userId);

  Optional<Notification> findByUserIdAndTaskId(String userId, String taskId);

  long countByUserIdAndRead(String userId, Boolean read);

  void deleteByTaskId(String taskId);
}
