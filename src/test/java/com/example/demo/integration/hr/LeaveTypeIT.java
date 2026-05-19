package com.example.demo.integration.hr;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.SentryConf;
import com.example.demo.client.api.HrApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateLeaveType;
import com.example.demo.client.model.LeaveType;
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
@ContextConfiguration(initializers = LeaveTypeIT.ContextInitializer.class)
class LeaveTypeIT {
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
  void administration_can_get_all_leave_types() throws Exception {
    HrApi api = new HrApi(anApiClient(ADMINISTRATION_TOKEN));

    List<LeaveType> leaveTypes = api.getLeaveTypes(COMPANY1_ID);

    assertEquals(2, leaveTypes.size());
    assertTrue(leaveTypes.stream().anyMatch(lt -> LEAVE_TYPE1_ID.equals(lt.getId())));
    assertTrue(leaveTypes.stream().anyMatch(lt -> LEAVE_TYPE2_ID.equals(lt.getId())));
  }

  @Test
  void user_with_bad_token_cannot_get_leave_types() {
    HrApi api = new HrApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(() -> api.getLeaveTypes(COMPANY1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_update_leave_types() throws Exception {
    HrApi api = new HrApi(anApiClient(ADMIN_TOKEN));
    CrupdateLeaveType toUpdate = leaveTypeToCrupdateLeaveType(leaveType1());
    toUpdate.setDescription("Updated description");

    List<LeaveType> updated = api.crupdateLeaveTypes(COMPANY1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(LEAVE_TYPE1_ID, updated.get(0).getId());
    assertEquals("Updated description", updated.get(0).getDescription());
  }

  @Test
  void employee_cannot_create_leave_types() {
    HrApi api = new HrApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () -> api.crupdateLeaveTypes(COMPANY1_ID, List.of(someCreatableLeaveType())));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
