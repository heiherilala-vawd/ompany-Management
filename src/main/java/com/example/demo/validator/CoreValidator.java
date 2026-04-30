package com.example.demo.validator;

import com.example.demo.model.Company;
import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.model.exception.BadRequestException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CoreValidator {

  public void validateCompany(Company company) {
    if (company == null) {
      throw new BadRequestException("Company cannot be null");
    }
    if (company.getName() == null || company.getName().isBlank()) {
      throw new BadRequestException("Company name is mandatory");
    }
    if (company.getCompanyType() == null) {
      throw new BadRequestException("Company type is mandatory");
    }
  }

  public void validateCompanies(List<Company> companies) {
    if (companies == null || companies.isEmpty()) {
      throw new BadRequestException("Company list cannot be null or empty");
    }
    companies.forEach(this::validateCompany);
  }

  public void validateUser(User user) {
    if (user == null) {
      throw new BadRequestException("User cannot be null");
    }
    if (user.getFirstName() == null || user.getFirstName().isBlank()) {
      throw new BadRequestException("First name is mandatory");
    }
    if (user.getLastName() == null || user.getLastName().isBlank()) {
      throw new BadRequestException("Last name is mandatory");
    }
    if (user.getEmail() == null || user.getEmail().isBlank()) {
      throw new BadRequestException("Email is mandatory");
    }
    if (user.getPassword() == null || user.getPassword().isBlank()) {
      throw new BadRequestException("Password is mandatory");
    }
    if (user.getRole() == null) {
      throw new BadRequestException("Role is mandatory");
    }
    if (user.getSex() == null) {
      throw new BadRequestException("Sex is mandatory");
    }
  }

  public void validateUsers(List<User> users) {
    if (users == null || users.isEmpty()) {
      throw new BadRequestException("User list cannot be null or empty");
    }
    users.forEach(this::validateUser);
  }

  public void validateJob(Job job) {
    if (job == null) {
      throw new BadRequestException("Job cannot be null");
    }
    if (job.getCompany() == null || job.getCompany().getId() == null) {
      throw new BadRequestException("Job must be associated with a company");
    }
    if (job.getStartDate() != null && job.getEndDate() != null) {
      if (job.getEndDate().isBefore(job.getStartDate())) {
        throw new BadRequestException("Job end date cannot be before start date");
      }
    }
    if (job.getStatus() == null) {
      throw new BadRequestException("Job status is mandatory");
    }
  }

  public void validateJobs(List<Job> jobs) {
    if (jobs == null || jobs.isEmpty()) {
      throw new BadRequestException("Job list cannot be null or empty");
    }
    jobs.forEach(this::validateJob);
  }
}
