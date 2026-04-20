package com.example.demo.model.criteria;

import com.example.demo.model.Job;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobCriteria {
  private Job.JobStatus status;
  private String companyId;
  private String description;
}
