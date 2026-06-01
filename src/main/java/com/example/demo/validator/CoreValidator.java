package com.example.demo.validator;

import com.example.demo.model.Company;
import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.model.exception.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CoreValidator {

  public void validateCompany(Company company) {
    List<String> errors = new ArrayList<>();
    if (company == null) {
      errors.add("Company cannot be null");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateCompanies(List<Company> companies) {
    List<String> errors = new ArrayList<>();
    if (companies == null || companies.isEmpty()) {
      errors.add("Company list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    companies.forEach(this::validateCompany);
  }

  public void validateUser(User user) {
    List<String> errors = new ArrayList<>();
    if (user == null) {
      errors.add("User cannot be null");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateUsers(List<User> users) {
    List<String> errors = new ArrayList<>();
    if (users == null || users.isEmpty()) {
      errors.add("User list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    users.forEach(this::validateUser);
  }

  public void validateJob(Job job) {
    List<String> errors = new ArrayList<>();
    if (job == null) {
      errors.add("Job cannot be null");
    }
    if (job != null && (job.getCompany() == null || job.getCompany().getId() == null)) {
      errors.add("Job must be associated with a company");
    }
    if (job != null
        && job.getStartDate() != null
        && job.getEndDate() != null
        && job.getEndDate().isBefore(job.getStartDate())) {
      errors.add("Job end date cannot be before start date");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
  }

  public void validateJobs(List<Job> jobs) {
    List<String> errors = new ArrayList<>();
    if (jobs == null || jobs.isEmpty()) {
      errors.add("Job list cannot be null or empty");
    }
    if (!errors.isEmpty()) {
      throw new BadRequestException(String.join("; ", errors));
    }
    jobs.forEach(this::validateJob);
  }
}
