package com.example.demo.endpoint.rest.interceptor;

import com.example.demo.model.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

public class PathVariableValidationInterceptor implements HandlerInterceptor {

  static final String EMPTY_PATH_VARIABLE_MESSAGE = "Path variable '%s' must not be empty";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (handler instanceof org.springframework.web.method.HandlerMethod) {
      Map<String, String> pathVariables =
          (Map<String, String>)
              request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

      if (pathVariables != null) {
        for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
          if (entry.getValue() == null || entry.getValue().isBlank()) {
            throw new BadRequestException(
                EMPTY_PATH_VARIABLE_MESSAGE.formatted(entry.getKey()));
          }
        }
      }
    }
    return true;
  }
}
