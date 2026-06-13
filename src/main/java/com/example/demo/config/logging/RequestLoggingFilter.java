package com.example.demo.config.logging;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Component
@Profile("dev")
public class RequestLoggingFilter extends CommonsRequestLoggingFilter {

  public RequestLoggingFilter() {
    setIncludeQueryString(true);
    setIncludePayload(true);
    setMaxPayloadLength(10000);
    setIncludeHeaders(false);
    setBeforeMessagePrefix(">>> ");
    setAfterMessagePrefix("<<< ");
  }

  @Override
  protected boolean shouldLog(HttpServletRequest request) {
    String path = request.getServletPath();
    return !path.startsWith("/actuator") && !path.equals("/ping");
  }
}
