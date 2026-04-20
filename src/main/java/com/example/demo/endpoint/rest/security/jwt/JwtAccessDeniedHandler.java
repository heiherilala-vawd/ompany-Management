package com.example.demo.endpoint.rest.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {
    log.error("Forbidden error: {}", accessDeniedException.getMessage());

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

    com.example.demo.client.model.Exception exceptionResponse =
        new com.example.demo.client.model.Exception();
    exceptionResponse.setMessage(accessDeniedException.getMessage());
    exceptionResponse.setType("403 FORBIDDEN");

    final ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), exceptionResponse);
  }
}
