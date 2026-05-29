package com.example.demo.repository;

import com.example.demo.model.Job;
import com.example.demo.model.Job.JobStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, String>, JpaSpecificationExecutor<Job> {
  Page<Job> findByStatus(JobStatus status, Pageable pageable);

  Page<Job> findByCompanyId(String companyId, Pageable pageable);

  Page<Job> findByStatusAndCompanyId(JobStatus status, String companyId, Pageable pageable);

  List<Job> findByCompanyId(String companyId);

  List<Job> findByCompanyIdOrderByStartDateDesc(String companyId);

  List<Job> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

  List<Job> findByEndDateBefore(LocalDate date);

  List<Job> findByStartDateAfter(LocalDate date);

  boolean existsByCompanyIdAndStatus(String companyId, JobStatus status);

  @Query(
      "SELECT j.id, j.description, COUNT(u) FROM Job j JOIN j.responsibleUsers u "
          + "WHERE j.company.id = :companyId GROUP BY j.id, j.description")
  List<Object[]> countUsersByJob(@Param("companyId") String companyId);
}
