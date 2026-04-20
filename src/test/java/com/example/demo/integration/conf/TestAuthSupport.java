package com.example.demo.integration.conf;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.demo.client.invoker.ApiClient;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

final class TestAuthSupport {

  private TestAuthSupport() {}

  static void setUpJwtService(JwtUtils jwtServiceMock) {
    when(jwtServiceMock.getUserEmailFromJwtToken(anyString()))
        .thenAnswer(
            invocation -> {
              String token = invocation.getArgument(0);

              if (TestUtils.ADMIN_TOKEN.equals(token)) return TestUtils.ADMIN_EMAIL;
              if (TestUtils.WAREHOUSE_TOKEN.equals(token)) return TestUtils.WAREHOUSE_EMAIL;
              if (TestUtils.EMPLOYEE_TOKEN.equals(token)) return TestUtils.EMPLOYEE_EMAIL;
              if (TestUtils.ADMINISTRATION_TOKEN.equals(token))
                return TestUtils.ADMINISTRATION_EMAIL;
              if (TestUtils.USER1_TOKEN.equals(token)) return TestUtils.USER1_EMAIL;
              if (TestUtils.USER2_TOKEN.equals(token)) return TestUtils.USER2_EMAIL;
              if (TestUtils.RANDOM_TOKEN.equals(token)) return TestUtils.RANDOM_EMAIL;
              return null;
            });

    when(jwtServiceMock.generateJwtToken(any()))
        .thenAnswer(
            invocation -> {
              Object auth = invocation.getArgument(0);

              if (TestUtils.ADMIN_AUTH.equals(auth)) return TestUtils.ADMIN_TOKEN;
              if (TestUtils.WAREHOUSE_AUTH.equals(auth)) return TestUtils.WAREHOUSE_TOKEN;
              if (TestUtils.EMPLOYEE_AUTH.equals(auth)) return TestUtils.EMPLOYEE_TOKEN;
              if (TestUtils.ADMINISTRATION_AUTH.equals(auth)) return TestUtils.ADMINISTRATION_TOKEN;
              if (TestUtils.USER1_AUTH.equals(auth)) return TestUtils.USER1_TOKEN;
              if (TestUtils.USER2_AUTH.equals(auth)) return TestUtils.USER2_TOKEN;
              if (TestUtils.RANDOM_AUTH.equals(auth)) return TestUtils.RANDOM_TOKEN;
              return null;
            });

    when(jwtServiceMock.validateJwtToken(anyString()))
        .thenAnswer(
            invocation -> {
              String token = invocation.getArgument(0);

              return TestUtils.ADMIN_TOKEN.equals(token)
                  || TestUtils.WAREHOUSE_TOKEN.equals(token)
                  || TestUtils.EMPLOYEE_TOKEN.equals(token)
                  || TestUtils.ADMINISTRATION_TOKEN.equals(token)
                  || TestUtils.USER1_TOKEN.equals(token)
                  || TestUtils.USER2_TOKEN.equals(token)
                  || TestUtils.RANDOM_TOKEN.equals(token);
            });
  }

  static void setUpAuthenticationManager(AuthenticationManager authenticationManagerMock) {
    when(authenticationManagerMock.authenticate(any()))
        .thenAnswer(
            invocation -> {
              UsernamePasswordAuthenticationToken auth = invocation.getArgument(0);
              String email = auth.getName();
              String password = (String) auth.getCredentials();

              if (!TestUtils.PASSWORD.equals(password)) {
                throw new BadCredentialsException("Les identifications sont erronée");
              }

              if (TestUtils.ADMIN_EMAIL.equals(email)) return TestUtils.ADMIN_AUTH;
              if (TestUtils.EMPLOYEE_EMAIL.equals(email)) return TestUtils.EMPLOYEE_AUTH;
              if (TestUtils.ADMINISTRATION_EMAIL.equals(email))
                return TestUtils.ADMINISTRATION_AUTH;
              if (TestUtils.USER1_EMAIL.equals(email)) return TestUtils.USER1_AUTH;
              if (TestUtils.USER2_EMAIL.equals(email)) return TestUtils.USER2_AUTH;
              if (TestUtils.WAREHOUSE_EMAIL.equals(email)) return TestUtils.WAREHOUSE_AUTH;
              if (TestUtils.RANDOM_EMAIL.equals(email)) return TestUtils.RANDOM_AUTH;

              throw new BadCredentialsException("Les identifications sont erronée");
            });
  }

  static ApiClient anApiClient(String token, int serverPort) {
    ApiClient client = new ApiClient();
    client.setScheme("http");
    client.setHost("localhost");
    client.setPort(serverPort);
    client.setRequestInterceptor(
        httpRequestBuilder -> httpRequestBuilder.header("Authorization", "Bearer " + token));
    return client;
  }
}
