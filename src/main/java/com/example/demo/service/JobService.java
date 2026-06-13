package com.example.demo.service;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.Job;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.criteria.JobCriteria;
import com.example.demo.model.exception.ForbiddenException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import com.example.demo.validator.CoreValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobService {

  private final JobRepository jobRepository;
  private final UserRepository userRepository;
  private final ModificationUtils modificationUtils;
  private final CoreValidator coreValidator;

  public Optional<Job> findById(String id) {
    return jobRepository.findById(id);
  }

  public Page<Job> findAll(PageFromOne page, BoundedPageSize pageSize, JobCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return jobRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<Job> createOrUpdateAll(List<Job> jobs) {
    coreValidator.validateJobs(jobs);
    List<Job> processedJobs = new ArrayList<>();
    for (Job job : jobs) {
      Job existingJob =
          job.getId() == null ? null : jobRepository.findById(job.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          job, existingJob, job.getId(), modificationUtils.takePrimaryUser());
      processedJobs.add(job);
    }
    return jobRepository.saveAll(processedJobs);
  }

  @Transactional
  public void deleteById(String id) {
    jobRepository.deleteById(id);
  }

  @Transactional
  public void assignUserToJob(String jobId, String userId) {
    Job job =
        jobRepository
            .findById(jobId)
            .orElseThrow(() -> new NotFoundException("Job not found: " + jobId));
    validateWarehouseWorkerAccess(job);
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found: " + userId));

    job.getResponsibleUsers().add(user);
    jobRepository.save(job);
  }

  @Transactional
  public void unassignUserFromJob(String jobId, String userId) {
    Job job =
        jobRepository
            .findById(jobId)
            .orElseThrow(() -> new NotFoundException("Job not found: " + jobId));
    validateWarehouseWorkerAccess(job);

    job.getResponsibleUsers().removeIf(user -> user.getId().equals(userId));
    jobRepository.save(job);
  }

  public List<User> getJobResponsibleUsers(String jobId) {
    Job job =
        jobRepository
            .findById(jobId)
            .orElseThrow(() -> new NotFoundException("Job not found: " + jobId));
    validateWarehouseWorkerAccess(job);

    return new ArrayList<>(job.getResponsibleUsers());
  }

  private void validateWarehouseWorkerAccess(Job job) {
    User actingUser = modificationUtils.takePrimaryUser();
    if (actingUser.getRole() == User.Role.WAREHOUSE_WORKER) {
      boolean isAssigned =
          job.getResponsibleUsers().stream()
              .anyMatch(u -> u.getId().equals(actingUser.getId()));
      if (!isAssigned) {
        throw new ForbiddenException("Warehouse worker is not assigned to this job");
      }
    }
  }

  private Specification<Job> toSpecification(JobCriteria criteria) {
    Specification<Job> spec = Specification.where((root, query, cb) -> cb.conjunction());
    if (criteria.getYear() != null) {
      Integer year = criteria.getYear();
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.or(
                      cb.equal(
                          cb.function(
                              "date_part", Double.class, cb.literal("year"), root.get("startDate")),
                          year.doubleValue()),
                      cb.equal(
                          cb.function(
                              "date_part",
                              Double.class,
                              cb.literal("year"),
                              root.get("contractSignatureDate")),
                          year.doubleValue()),
                      cb.equal(
                          cb.function(
                              "date_part", Double.class, cb.literal("year"), root.get("endDate")),
                          year.doubleValue())));
    }
    if (criteria.getStatus() != null) {
      spec = spec.and(equal(criteria.getStatus(), "status"));
    }
    if (criteria.getCompanyId() != null) {
      spec = spec.and(equal(criteria.getCompanyId(), "company", "id"));
    }
    if (criteria.getDescription() != null) {
      spec = spec.and(containsIgnoreCase(criteria.getDescription(), "description"));
    }
    return spec;
  }
}
