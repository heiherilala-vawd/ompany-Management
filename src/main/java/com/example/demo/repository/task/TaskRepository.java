package com.example.demo.repository.task;

import com.example.demo.model.task.Task;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

  List<Task> findByCompanyId(String companyId);

  List<Task> findByCompanyIdAndCompletedAtBetween(String companyId, Instant from, Instant to);

  @Query("SELECT t.completed FROM Task t WHERE t.id = :taskId")
  Optional<Boolean> findCompletedById(@Param("taskId") String taskId);

  @Query(
      "SELECT DISTINCT t FROM Task t JOIN TaskAssignment ta ON ta.task.id = t.id WHERE t.company.id = :companyId AND ta.user.id = :userId")
  List<Task> findByCompanyIdAndAssignedUserId(
      @Param("companyId") String companyId, @Param("userId") String userId);
}
