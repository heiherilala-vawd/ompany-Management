package com.example.demo.endpoint.rest.security;

import com.example.demo.model.User;
import com.example.demo.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("jobSecurity")
@RequiredArgsConstructor
public class JobSecurityUtil {

  private final JobRepository jobRepository;

  public boolean isCurrentUserAssignedToJob(String jobId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }
    Object principal = authentication.getPrincipal();
    if (!(principal instanceof User user)) {
      return false;
    }
    if (user.getRole() != User.Role.WAREHOUSE_WORKER) {
      return false;
    }
    return jobRepository.isUserAssignedToJob(jobId, user.getId());
  }
}
