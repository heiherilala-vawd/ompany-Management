package com.example.demo.config.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.demo.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class MdcFilterTest {

  private MdcFilter filter;

  @Mock private User user;

  @BeforeEach
  void setUp() {
    filter = new MdcFilter();
  }

  @AfterEach
  void tearDown() {
    MDC.clear();
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilter_ShouldGenerateCorrelationId_WhenNoHeader() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    String[] captured = new String[1];
    FilterChain capturingChain = (req, res) -> captured[0] = MDC.get("correlationId");

    filter.doFilterInternal(request, response, capturingChain);

    assertThat(captured[0]).isNotNull().isNotEmpty();
  }

  @Test
  void doFilter_ShouldUseCorrelationId_FromHeader() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-Correlation-Id", "my-correlation-id");
    MockHttpServletResponse response = new MockHttpServletResponse();

    String[] captured = new String[1];
    FilterChain capturingChain = (req, res) -> captured[0] = MDC.get("correlationId");

    filter.doFilterInternal(request, response, capturingChain);

    assertThat(captured[0]).isEqualTo("my-correlation-id");
  }

  @Test
  void doFilter_ShouldSetUserId_WhenAuthenticated() throws ServletException, IOException {
    when(user.getId()).thenReturn("user-123");

    Authentication auth =
        new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setServletPath("/users/user-123/companies/company-456/notifications");
    MockHttpServletResponse response = new MockHttpServletResponse();

    String[] captured = new String[1];
    FilterChain capturingChain = (req, res) -> captured[0] = MDC.get("userId");

    filter.doFilterInternal(request, response, capturingChain);

    assertThat(captured[0]).isEqualTo("user-123");
  }

  @Test
  void doFilter_ShouldSetCompanyId_WhenCompanyInPath() throws ServletException, IOException {
    when(user.getId()).thenReturn("user-123");

    Authentication auth =
        new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setServletPath("/users/user-123/companies/company-456/notifications");
    MockHttpServletResponse response = new MockHttpServletResponse();

    String[] captured = new String[1];
    FilterChain capturingChain = (req, res) -> captured[0] = MDC.get("companyId");

    filter.doFilterInternal(request, response, capturingChain);

    assertThat(captured[0]).isEqualTo("company-456");
  }

  @Test
  void doFilter_ShouldNotSetCompanyId_WhenNoCompanyInPath() throws ServletException, IOException {
    when(user.getId()).thenReturn("user-123");

    Authentication auth =
        new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setServletPath("/auth/login");
    MockHttpServletResponse response = new MockHttpServletResponse();

    String[] captured = new String[1];
    FilterChain capturingChain = (req, res) -> captured[0] = MDC.get("companyId");

    filter.doFilterInternal(request, response, capturingChain);

    assertThat(captured[0]).isNull();
  }

  @Test
  void doFilter_ShouldNotSetUserId_WhenNotAuthenticated() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    String[] capturedUserId = new String[1];
    String[] capturedCorrelationId = new String[1];
    FilterChain capturingChain =
        (req, res) -> {
          capturedUserId[0] = MDC.get("userId");
          capturedCorrelationId[0] = MDC.get("correlationId");
        };

    filter.doFilterInternal(request, response, capturingChain);

    assertThat(capturedUserId[0]).isNull();
    assertThat(capturedCorrelationId[0]).isNotNull();
  }

  @Test
  void doFilter_ShouldClearMdc_AfterRequest() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain noopChain = (req, res) -> {};

    filter.doFilterInternal(request, response, noopChain);

    assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
  }
}
