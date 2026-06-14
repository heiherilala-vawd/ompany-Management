package com.example.demo.integration.hr;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.SentryConf;
import com.example.demo.client.api.HrApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.EmployeeLeaveConfig;
import com.example.demo.client.model.PaginatedResponse;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestDataSqlLoader;
import com.example.demo.integration.conf.TestUtils;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = EmployeeLeaveConfigIT.ContextInitializer.class)
class EmployeeLeaveConfigIT {
  @Autowired private DataSource dataSource;

  @MockitoBean private SentryConf sentryConf;
  @MockitoBean private AuthenticationManager authenticationManagerMock;
  @MockitoBean private JwtUtils jwtServiceMock;

  private static ApiClient anApiClient(String token) {
    return TestUtils.anApiClient(token, ContextInitializer.SERVER_PORT);
  }

  @BeforeEach
  void setUp() throws Exception {
    TestUtils.setUpJwtService(jwtServiceMock);
    TestUtils.setUpAuthenticationManager(authenticationManagerMock);
    TestDataSqlLoader.executeAllSqlScripts(dataSource);
  }

  @Test
  void administration_can_get_all_configs() throws Exception {
    HrApi api = new HrApi(anApiClient(ADMINISTRATION_TOKEN));

    PaginatedResponse resp = api.getEmployeeLeaveConfigs(ADMIN_ID, COMPANY1_ID);

    List<EmployeeLeaveConfig> configs = extractData(resp, EmployeeLeaveConfig.class);

    assertEquals(2, configs.size());
  }

  @Test
  void user_with_bad_token_cannot_get_configs() {
    HrApi api = new HrApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(() -> api.getEmployeeLeaveConfigs(ADMIN_ID, COMPANY1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_update_configs() throws Exception {
    HrApi api = new HrApi(anApiClient(ADMIN_TOKEN));

    List<EmployeeLeaveConfig> updated =
        api.crupdateEmployeeLeaveConfigs(ADMIN_ID, COMPANY1_ID, List.of(someCreatableConfig()));

    assertEquals(1, updated.size());
    assertNotNull(updated.get(0).getId());
  }

  @Test
  void employee_cannot_create_configs() {
    HrApi api = new HrApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateEmployeeLeaveConfigs(
                ADMIN_ID, COMPANY1_ID, List.of(someCreatableConfig())));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
