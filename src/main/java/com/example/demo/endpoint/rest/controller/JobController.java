package com.example.demo.endpoint.rest.controller;

import com.example.demo.client.model.CrupdateJob;
import com.example.demo.client.model.Job;
import com.example.demo.client.model.JobStatus;
import com.example.demo.endpoint.rest.mapper.JobMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.JobCriteria;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.JobService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class JobController {

  private final JobService jobService;
  private final JobMapper jobMapper;

  @GetMapping("/companies/{comp_id}/jobs/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER', 'EMPLOYEE')")
  public Job getJobById(@PathVariable String comp_id, @PathVariable String id) {
    return jobMapper.toRestJob(
        jobService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Job with id " + id + " not found")));
  }

  @GetMapping("/companies/{companyId}/jobs")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Job> getJobs(
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

  @PutMapping("/companies/{comp_id}/jobs")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Job> crupdateJobs(
      @PathVariable String comp_id, @RequestBody List<CrupdateJob> toWrite) {
    var saved = jobService.createOrUpdateAll(toWrite.stream().map(jobMapper::toDomain).toList());
    return saved.stream().map(jobMapper::toRestJob).toList();
  }

  @DeleteMapping("/companies/{comp_id}/jobs/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteJobById(@PathVariable String comp_id, @PathVariable String id) {
    jobService.deleteById(id);
  }
}
