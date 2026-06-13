package com.example.demo.config.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class RequestLoggingFilterTest {

  private RequestLoggingFilter filter;

  @BeforeEach
  void setUp() {
    filter = new RequestLoggingFilter();
  }

  @Test
  void shouldLog_ShouldReturnTrue_ForApiRequest() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setServletPath("/users/123/companies/456/notifications");

    assertThat(filter.shouldLog(request)).isTrue();
  }

  @Test
  void shouldLog_ShouldReturnTrue_ForAuthRequest() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setServletPath("/auth/login");

    assertThat(filter.shouldLog(request)).isTrue();
  }

  @Test
  void shouldLog_ShouldReturnFalse_ForActuatorHealth() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setServletPath("/actuator/health");

    assertThat(filter.shouldLog(request)).isFalse();
  }

  @Test
  void shouldLog_ShouldReturnFalse_ForActuatorInfo() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setServletPath("/actuator/info");

    assertThat(filter.shouldLog(request)).isFalse();
  }

  @Test
  void shouldLog_ShouldReturnFalse_ForPing() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setServletPath("/ping");

    assertThat(filter.shouldLog(request)).isFalse();
  }

  @Test
  void shouldLog_ShouldHandleNestedActuatorPath() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setServletPath("/actuator/metrics/jvm.memory.used");

    assertThat(filter.shouldLog(request)).isFalse();
  }
}
