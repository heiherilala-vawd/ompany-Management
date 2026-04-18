package com.example.demo.endpoint.rest.mapper;

import com.example.demo.client.model.CrupdateJob;
import com.example.demo.client.model.Job;
import com.example.demo.client.model.JobStatus;
import com.example.demo.service.CompanyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobMapper {

  private final CompanyService companyService;

  public com.example.demo.model.Job toDomain(Job restJob) {
    if (restJob == null) return null;

    return com.example.demo.model.Job.builder()
        .id(restJob.getId())
        .company(
            restJob.getCompanyId() != null
                ? companyService.findById(restJob.getCompanyId()).orElse(null)
                : null)
        .description(restJob.getDescription())
        .contractSignatureDate(restJob.getContractSignatureDate())
        .startDate(restJob.getStartDate())
        .endDate(restJob.getEndDate())
        .status(
            restJob.getStatus() != null
                ? com.example.demo.model.Job.JobStatus.valueOf(restJob.getStatus().name())
                : null)
        .build();
  }

  public com.example.demo.model.Job toDomain(CrupdateJob restJob) {
    if (restJob == null) return null;

    return com.example.demo.model.Job.builder()
        .id(restJob.getId())
        .company(
            restJob.getCompanyId() != null
                ? companyService.findById(restJob.getCompanyId()).orElse(null)
                : null)
        .description(restJob.getDescription())
        .contractSignatureDate(restJob.getContractSignatureDate())
        .startDate(restJob.getStartDate())
        .endDate(restJob.getEndDate())
        .status(
            restJob.getStatus() != null
                ? com.example.demo.model.Job.JobStatus.valueOf(restJob.getStatus().name())
                : null)
        .build();
  }

  public Job toRestJob(com.example.demo.model.Job domainJob) {
    if (domainJob == null) return null;

    Job restJob = new Job();
    restJob.setId(domainJob.getId());
    restJob.setCompanyId(domainJob.getCompany() != null ? domainJob.getCompany().getId() : null);
    restJob.setDescription(domainJob.getDescription());
    restJob.setContractSignatureDate(domainJob.getContractSignatureDate());
    restJob.setStartDate(domainJob.getStartDate());
    restJob.setEndDate(domainJob.getEndDate());
    restJob.setStatus(
        domainJob.getStatus() != null ? JobStatus.valueOf(domainJob.getStatus().name()) : null);
    restJob.setCreatedAt(domainJob.getCreatedAt());
    restJob.setUpdatedAt(domainJob.getUpdatedAt());
    restJob.setComment(domainJob.getComment());

    if (domainJob.getCreatedBy() != null) {
      restJob.setCreatedBy(domainJob.getCreatedBy().getId());
    }
    if (domainJob.getUpdatedBy() != null) {
      restJob.setUpdatedBy(domainJob.getUpdatedBy().getId());
    }

    return restJob;
  }

  public List<Job> toRestJobs(List<com.example.demo.model.Job> domainJobs) {
    return domainJobs.stream().map(this::toRestJob).toList();
  }

  public List<com.example.demo.model.Job> toDomain(List<Job> restJobs) {
    return restJobs.stream().map(this::toDomain).toList();
  }
}
