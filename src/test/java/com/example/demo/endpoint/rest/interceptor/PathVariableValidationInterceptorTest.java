package com.example.demo.endpoint.rest.interceptor;

import static com.example.demo.endpoint.rest.interceptor.PathVariableValidationInterceptor.EMPTY_PATH_VARIABLE_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.model.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

@ExtendWith(MockitoExtension.class)
class PathVariableValidationInterceptorTest {

  private PathVariableValidationInterceptor interceptor;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private HandlerMethod handlerMethod;

  @BeforeEach
  void setUp() {
    interceptor = new PathVariableValidationInterceptor();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    handlerMethod = mock(HandlerMethod.class);
  }

  @Test
  void preHandle_ShouldPass_WhenAllPathVariablesAreValid() {
    // Given
    Map<String, String> pathVariables = Map.of("userId", "valid-id", "companyId", "company-123");
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
        .thenReturn(pathVariables);

    // When & Then
    assertThatCode(() -> interceptor.preHandle(request, response, handlerMethod))
        .doesNotThrowAnyException();
  }

  @Test
  void preHandle_ShouldThrowBadRequest_WhenPathVariableIsEmpty() {
    // Given
    Map<String, String> pathVariables = Map.of("userId", "", "companyId", "company-123");
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
        .thenReturn(pathVariables);

    // When & Then
    assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
        .isInstanceOf(BadRequestException.class)
        .hasMessage(EMPTY_PATH_VARIABLE_MESSAGE.formatted("userId"));
  }

  @Test
  void preHandle_ShouldThrowBadRequest_WhenPathVariableIsBlank() {
    // Given
    Map<String, String> pathVariables = Map.of("companyId", "   ");
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
        .thenReturn(pathVariables);

    // When & Then
    assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
        .isInstanceOf(BadRequestException.class)
        .hasMessage(EMPTY_PATH_VARIABLE_MESSAGE.formatted("companyId"));
  }

  @Test
  void preHandle_ShouldThrowBadRequest_WhenPathVariableIsNull() {
    // Given
    Map<String, String> pathVariables = new HashMap<>();
    pathVariables.put("userId", null);
    pathVariables.put("companyId", "company-123");
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
        .thenReturn(pathVariables);

    // When & Then
    assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
        .isInstanceOf(BadRequestException.class)
        .hasMessage(EMPTY_PATH_VARIABLE_MESSAGE.formatted("userId"));
  }

  @Test
  void preHandle_ShouldPass_WhenNoPathVariables() {
    // Given
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);

    // When & Then
    assertThatCode(() -> interceptor.preHandle(request, response, handlerMethod))
        .doesNotThrowAnyException();
  }

  @Test
  void preHandle_ShouldPass_WhenHandlerIsNotHandlerMethod() {
    // Given: handler is not a HandlerMethod (e.g. a resource handler)
    Object nonHandlerMethod = new Object();

    // When & Then
    assertThatCode(() -> interceptor.preHandle(request, response, nonHandlerMethod))
        .doesNotThrowAnyException();
  }

  @Test
  void preHandle_ShouldReturnTrue_WhenAllPathVariablesAreValid() {
    // Given
    Map<String, String> pathVariables = Map.of("userId", "valid-id");
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
        .thenReturn(pathVariables);

    // When
    boolean result = interceptor.preHandle(request, response, handlerMethod);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void preHandle_ShouldThrowBadRequest_WhenFirstEmptyBeforeSecondValid() {
    // Given: userId is empty, companyId is valid
    Map<String, String> pathVariables = Map.of("userId", "", "companyId", "valid-id");
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
        .thenReturn(pathVariables);

    // When & Then
    assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
        .isInstanceOf(BadRequestException.class)
        .hasMessage(EMPTY_PATH_VARIABLE_MESSAGE.formatted("userId"));
  }
}
