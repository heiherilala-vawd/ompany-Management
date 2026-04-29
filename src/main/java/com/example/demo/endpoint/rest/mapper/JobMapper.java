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
  private final CompanyMapper companyMapper;

  public com.example.demo.model.Job toDomain(Job restJob) {
    if (restJob == null) return null;

    return com.example.demo.model.Job.builder()
        .id(restJob.getId())
        .company(
            restJob.getCompany() != null && restJob.getCompany().getId() != null
                ? companyService.findById(restJob.getCompany().getId()).orElse(null)
                : null)
        .description(restJob.getDescription())
        .contractSignatureDate(restJob.getContractSignatureDate())
        .startDate(restJob.getStartDate())
        .endDate(restJob.getEndDate())
        .comment(restJob.getComment())
        .status(EnumMapper.mapEnum(restJob.getStatus(), com.example.demo.model.Job.JobStatus.class))
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
        .comment(restJob.getComment())
        .status(EnumMapper.mapEnum(restJob.getStatus(), com.example.demo.model.Job.JobStatus.class))
        .build();
  }

  public Job toRestJob(com.example.demo.model.Job domainJob) {
    if (domainJob == null) return null;

    Job restJob = new Job();
    restJob.setId(domainJob.getId());
    restJob.setCompany(companyMapper.toRestCrupdateCompany(domainJob.getCompany()));
    restJob.setDescription(domainJob.getDescription());
    restJob.setContractSignatureDate(domainJob.getContractSignatureDate());
    restJob.setStartDate(domainJob.getStartDate());
    restJob.setEndDate(domainJob.getEndDate());
    restJob.setStatus(EnumMapper.mapEnum(domainJob.getStatus(), JobStatus.class));
    RestAuditMapperUtils.mapAuditFields(
        domainJob,
        restJob::setCreatedAt,
        restJob::setUpdatedAt,
        restJob::setComment,
        restJob::setCreatedBy,
        restJob::setUpdatedBy);

    return restJob;
  }

  public CrupdateJob toRestCrupdateJob(com.example.demo.model.Job domainJob) {
    if (domainJob == null) return null;

    return new CrupdateJob()
        .id(domainJob.getId())
        .companyId(domainJob.getCompany() != null ? domainJob.getCompany().getId() : null)
        .description(domainJob.getDescription())
        .contractSignatureDate(domainJob.getContractSignatureDate())
        .startDate(domainJob.getStartDate())
        .endDate(domainJob.getEndDate())
        .status(EnumMapper.mapEnum(domainJob.getStatus(), JobStatus.class))
        .comment(domainJob.getComment());
  }

  public List<Job> toRestJobs(List<com.example.demo.model.Job> domainJobs) {
    return domainJobs.stream().map(this::toRestJob).toList();
  }

  public List<com.example.demo.model.Job> toDomain(List<Job> restJobs) {
    return restJobs.stream().map(this::toDomain).toList();
  }
}
