package com.example.demo.service;

import static com.example.demo.repository.specification.SpecificationUtils.containsIgnoreCase;
import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.Job;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.criteria.JobCriteria;
import com.example.demo.repository.JobRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
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
  private final ModificationUtils modificationUtils;

  public Optional<Job> findById(String id) {
    return jobRepository.findById(id);
  }

  public Page<Job> findAll(PageFromOne page, BoundedPageSize pageSize, JobCriteria criteria) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return jobRepository.findAll(toSpecification(criteria), pageable);
  }

  @Transactional
  public List<Job> createOrUpdateAll(List<Job> jobs) {
    List<Job> processedJobs = new ArrayList<>();
    for (Job job : jobs) {
      Job existingJob = jobRepository.findById(job.getId()).orElse(null);
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

  private Specification<Job> toSpecification(JobCriteria criteria) {
    return Specification.<Job>where(equal(criteria.getStatus(), "status"))
        .and(equal(criteria.getCompanyId(), "company", "id"))
        .and(containsIgnoreCase(criteria.getDescription(), "description"));
  }
}
