package com.example.demo.endpoint.rest.controller;

import com.example.demo.client.model.CrupdateJob;
import com.example.demo.client.model.Job;
import com.example.demo.client.model.JobStatus;
import com.example.demo.endpoint.rest.mapper.JobMapper;
import com.example.demo.endpoint.rest.mapper.UserMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.JobCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.JobService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class JobController {

  private final JobService jobService;
  private final JobMapper jobMapper;
  private final UserMapper userMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER', 'EMPLOYEE')")
  public Job getJobById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return jobMapper.toRestJob(
        jobService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Job with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Job> getJobs(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "status", required = false) JobStatus status,
      @RequestParam(name = "description", required = false) String description) {
    JobCriteria criteria = new JobCriteria();
    criteria.setStatus(
        status != null ? com.example.demo.model.Job.JobStatus.valueOf(status.name()) : null);
    criteria.setCompanyId(companyId);
    criteria.setDescription(description);

    return jobService.findAll(page, pageSize, criteria).stream().map(jobMapper::toRestJob).toList();
  }

  @PutMapping("/users/{userId}/companies/{companyId}/jobs")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Job> crupdateJobs(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateJob> toWrite) {
    List<com.example.demo.model.Job> saved =
        jobService.createOrUpdateAll(
            toWrite.stream().map(rest -> jobMapper.toDomain(rest, companyId)).toList());
    return saved.stream().map(jobMapper::toRestJob).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public Job deleteJobById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    Job entity =
        jobMapper.toRestJob(
            jobService
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Job " + id + " not found")));
    jobService.deleteById(id);
    return entity;
  }

  @GetMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/users")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<com.example.demo.client.model.User> getJobResponsibleUsers(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String jobId) {
    return jobService.getJobResponsibleUsers(jobId).stream().map(userMapper::toRestUser).toList();
  }

  @PutMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/users")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public void assignUserToJob(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String jobId) {
    jobService.assignUserToJob(jobId, userId);
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/jobs/{jobId}/users")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public void unassignUserFromJob(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String jobId) {
    jobService.unassignUserFromJob(jobId, userId);
  }
}
