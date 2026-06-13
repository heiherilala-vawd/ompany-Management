package com.example.demo.config.logging;

import com.example.demo.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 5)
public class MdcFilter extends OncePerRequestFilter {

  private static final Pattern COMPANY_PATH_PATTERN = Pattern.compile("/companies/([^/]+)");

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String correlationId = request.getHeader("X-Correlation-Id");
      if (correlationId == null || correlationId.isBlank()) {
        correlationId = UUID.randomUUID().toString();
      }
      MDC.put("correlationId", correlationId);

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null
          && authentication.isAuthenticated()
          && authentication.getPrincipal() instanceof User user) {
        MDC.put("userId", user.getId());

        String path = request.getServletPath();
        Matcher companyMatcher = COMPANY_PATH_PATTERN.matcher(path);
        if (companyMatcher.find()) {
          MDC.put("companyId", companyMatcher.group(1));
        }
      }

      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
