package com.example.demo.service.task;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.task.Task;
import com.example.demo.model.task.TaskAssignment;
import com.example.demo.repository.notification.NotificationRepository;
import com.example.demo.repository.task.TaskAssignmentRepository;
import com.example.demo.repository.task.TaskRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import java.util.ArrayList;
import java.util.List;
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
public class TaskService {

  private final TaskRepository taskRepository;
  private final TaskAssignmentRepository taskAssignmentRepository;
  private final NotificationRepository notificationRepository;
  private final ModificationUtils modificationUtils;

  public Optional<Task> findById(String id) {
    return taskRepository.findById(id);
  }

  public List<Task> findByCompanyId(String companyId) {
    return taskRepository.findByCompanyId(companyId);
  }

  public Page<Task> findAll(PageFromOne page, BoundedPageSize pageSize, String companyId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    List<Task> tasks = taskRepository.findByCompanyId(companyId);
    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), tasks.size());
    return new org.springframework.data.domain.PageImpl<>(
        tasks.subList(start, end), pageable, tasks.size());
  }

  public List<TaskAssignment> getAssignments(String taskId) {
    return taskAssignmentRepository.findByTaskId(taskId);
  }

  @Transactional
  public List<Task> createOrUpdateAll(List<Task> tasks) {
    for (Task task : tasks) {
      Task existing = taskRepository.findById(task.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          task, existing, task.getId(), modificationUtils.takePrimaryUser());
    }
    return taskRepository.saveAll(tasks);
  }

  @Transactional
  public void syncAssignments(String taskId, List<String> userIds) {
    User currentUser = modificationUtils.takePrimaryUser();
    taskAssignmentRepository.deleteByTaskId(taskId);
    Task task = taskRepository.findById(taskId).orElse(null);
    if (task == null) return;
    List<TaskAssignment> assignments = new ArrayList<>();
    for (String userId : userIds) {
      User user = User.builder().id(userId).build();
      TaskAssignment assignment =
          TaskAssignment.builder().id(UUID.randomUUID().toString()).task(task).user(user).build();
      modificationUtils.createOrUpdateModel(assignment, null, assignment.getId(), currentUser);
      assignments.add(assignment);
    }
    taskAssignmentRepository.saveAll(assignments);
  }

  @Transactional
  public void deleteById(String id) {
    taskAssignmentRepository.deleteByTaskId(id);
    notificationRepository.deleteByTaskId(id);
    taskRepository.deleteById(id);
  }
}
