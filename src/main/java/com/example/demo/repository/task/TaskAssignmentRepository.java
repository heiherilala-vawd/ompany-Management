package com.example.demo.repository.task;

import com.example.demo.model.task.TaskAssignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, String> {

  List<TaskAssignment> findByTaskId(String taskId);

  void deleteByTaskId(String taskId);
}
