package com.example.demo.repository.task;

import com.example.demo.model.task.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

  List<Task> findByCompanyId(String companyId);
}
