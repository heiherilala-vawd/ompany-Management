package com.example.demo.model.criteria;

import com.example.demo.model.Job;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
public class JobCriteria {
  private Job.JobStatus status;
  private String companyId;
  private String description;
  private Integer year;

  public static Specification<Job> toSpecification(JobCriteria criteria) {
    return Specification.<Job>where(
            criteria.getYear() == null
                ? null
                : (root, query, cb) ->
                    cb.or(
                        cb.equal(
                            cb.function("year", Integer.class, root.get("startDate")),
                            criteria.getYear()),
                        cb.equal(
                            cb.function("year", Integer.class, root.get("contractSignatureDate")),
                            criteria.getYear()),
                        cb.equal(
                            cb.function("year", Integer.class, root.get("endDate")),
                            criteria.getYear())))
        .and(
            criteria.getStatus() == null
                ? null
                : (root, query, cb) -> cb.equal(root.get("status"), criteria.getStatus()))
        .and(
            criteria.getCompanyId() == null
                ? null
                : (root, query, cb) ->
                    cb.equal(root.get("company").get("id"), criteria.getCompanyId()))
        .and(
            criteria.getDescription() == null
                ? null
                : (root, query, cb) ->
                    cb.like(
                        cb.lower(root.get("description")),
                        "%" + criteria.getDescription().toLowerCase() + "%"));
  }
}
