package com.example.demo.service;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.Job;
import com.example.demo.model.Job.JobStatus;
import com.example.demo.model.PageFromOne;
import com.example.demo.repository.JobRepository;
import com.example.demo.service.utils.PageUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobService {

  private final JobRepository jobRepository;

  public Optional<Job> findById(String id) {
    return jobRepository.findById(id);
  }

  public Page<Job> findAll(
      PageFromOne page, BoundedPageSize pageSize, JobStatus status, String companyId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);

    if (status != null && companyId != null) {
      return jobRepository.findByStatusAndCompanyId(status, companyId, pageable);
    }
    if (status != null) {
      return jobRepository.findByStatus(status, pageable);
    }
    if (companyId != null) {
      return jobRepository.findByCompanyId(companyId, pageable);
    }

    return jobRepository.findAll(pageable);
  }

  public List<Job> findAll() {
    return jobRepository.findAll();
  }

  public List<Job> findByCompanyIdOrderByStartDateDesc(String companyId) {
    return jobRepository.findByCompanyIdOrderByStartDateDesc(companyId);
  }

  public List<Job> findByStartDateBetween(LocalDate startDate, LocalDate endDate) {
    return jobRepository.findByStartDateBetween(startDate, endDate);
  }

  @Transactional
  public Job create(Job job) {
    return jobRepository.save(job);
  }

  @Transactional
  public Job update(Job job) {
    return jobRepository.save(job);
  }

  @Transactional
  public List<Job> createOrUpdateAll(List<Job> jobs) {
    return jobRepository.saveAll(jobs);
  }

  @Transactional
  public void deleteById(String id) {
    jobRepository.deleteById(id);
  }

  public boolean existsById(String id) {
    return jobRepository.existsById(id);
  }
}
