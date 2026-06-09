package com.example.demo.endpoint.rest.security.jwt;

import com.example.demo.model.Company;
import com.example.demo.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class CompanyScopedFilter extends OncePerRequestFilter {

  private static final Pattern USER_PATH_PATTERN = Pattern.compile("^/users/([^/]+)");
  private static final Pattern COMPANY_PATH_PATTERN = Pattern.compile("/companies/([^/]+)");

  private static final String[] SKIP_PATHS = {"/ping", "/auth/login", "/auth/register"};

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String path = request.getServletPath();

    if (shouldSkip(path)) {
      filterChain.doFilter(request, response);
      return;
    }

    Matcher userMatcher = USER_PATH_PATTERN.matcher(path);
    if (!userMatcher.find()) {
      filterChain.doFilter(request, response);
      return;
    }
    String pathUserId = userMatcher.group(1);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      filterChain.doFilter(request, response);
      return;
    }

    Object principal = authentication.getPrincipal();
    if (!(principal instanceof User user)) {
      filterChain.doFilter(request, response);
      return;
    }

    if (!user.getId().equals(pathUserId)) {
      log.warn("User ID mismatch: principal={}, path={}", user.getId(), pathUserId);
      sendForbidden(response, "User ID mismatch");
      return;
    }

    Matcher companyMatcher = COMPANY_PATH_PATTERN.matcher(path);
    if (companyMatcher.find()) {
      String pathCompanyId = companyMatcher.group(1);
      Set<String> userCompanyIds =
          user.getCompanies().stream().map(Company::getId).collect(Collectors.toSet());
      if (!userCompanyIds.contains(pathCompanyId)) {
        log.warn("Company ID mismatch: userCompanies={}, path={}", userCompanyIds, pathCompanyId);
        sendForbidden(response, "Company ID mismatch");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private boolean shouldSkip(String path) {
    for (String skipPath : SKIP_PATHS) {
      if (path.startsWith(skipPath)) {
        return true;
      }
    }
    return false;
  }

  private void sendForbidden(HttpServletResponse response, String message) throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

    com.example.demo.client.model.ModelApiException exceptionResponse =
        new com.example.demo.client.model.ModelApiException();
    exceptionResponse.setMessage(message);
    exceptionResponse.setType("403 FORBIDDEN");

    final ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), exceptionResponse);
  }
}
