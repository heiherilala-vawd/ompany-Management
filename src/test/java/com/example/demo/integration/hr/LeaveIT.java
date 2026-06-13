package com.example.demo.integration.hr;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.SentryConf;
import com.example.demo.client.api.HrApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.PaginatedResponse;
import com.example.demo.client.model.CrupdateLeave;
import com.example.demo.client.model.Leave;
import com.example.demo.client.model.LeaveBalance;
import com.example.demo.client.model.LeaveStatus;
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
@ContextConfiguration(initializers = LeaveIT.ContextInitializer.class)
class LeaveIT {
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
  void administration_can_get_leave_by_id() throws Exception {
    HrApi api = new HrApi(anApiClient(ADMINISTRATION_TOKEN));

    Leave actual = api.getLeaveById(ADMIN_ID, COMPANY1_ID, LEAVE1_ID);

    assertEquals(LEAVE1_ID, actual.getId());
    assertNotNull(actual.getUser());
    assertNotNull(actual.getLeaveType());
  }

  @Test
  void user_with_bad_token_cannot_get_leave_by_id() {
    HrApi api = new HrApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(() -> api.getLeaveById(ADMIN_ID, COMPANY1_ID, LEAVE1_ID));
  }

  @Test
  void administration_can_get_all_leaves() throws Exception {
    HrApi api = new HrApi(anApiClient(ADMINISTRATION_TOKEN));

    PaginatedResponse resp = api.getLeaves(ADMIN_ID, COMPANY1_ID, null, null, null, null, null, null);


    List<Leave> leaves = extractData(resp, Leave.class);

    assertEquals(2, leaves.size());
  }

  @Test
  void administration_can_filter_leaves_by_user() throws Exception {
    HrApi api = new HrApi(anApiClient(ADMINISTRATION_TOKEN));

    PaginatedResponse resp = api.getLeaves(ADMIN_ID, COMPANY1_ID, EMPLOYEE_ID, null, null, null, null, null);


    List<Leave> leaves = extractData(resp, Leave.class);

    assertEquals(2, leaves.size());
  }

  @Test
  void administration_can_filter_leaves_by_status() throws Exception {
    HrApi api = new HrApi(anApiClient(ADMINISTRATION_TOKEN));

    PaginatedResponse resp = api.getLeaves(
        ADMIN_ID, COMPANY1_ID, null, null, LeaveStatus.APPROVED.getValue(), null, null, null);
    List<Leave> leaves = extractData(resp, Leave.class);

    assertEquals(1, leaves.size());
    assertEquals(LeaveStatus.APPROVED, leaves.get(0).getStatus());
  }

  @Test
  @DirtiesContext
  void admin_can_update_leaves() throws Exception {
    HrApi api = new HrApi(anApiClient(ADMIN_TOKEN));
    CrupdateLeave toUpdate = leaveToCrupdateLeave(leave1());
    toUpdate.setReason("Updated reason");

    List<Leave> updated = api.crupdateLeaves(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(LEAVE1_ID, updated.get(0).getId());
    assertEquals("Updated reason", updated.get(0).getReason());
  }

  @Test
  void employee_cannot_create_leaves() {
    HrApi api = new HrApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () -> api.crupdateLeaves(ADMIN_ID, COMPANY1_ID, List.of(someCreatableLeave())));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_leave() throws Exception {
    HrApi api = new HrApi(anApiClient(ADMIN_TOKEN));

    api.deleteLeaveById(ADMIN_ID, COMPANY1_ID, LEAVE1_ID);

    PaginatedResponse resp = api.getLeaves(ADMIN_ID, COMPANY1_ID, null, null, null, null, null, null);


    List<Leave> leaves = extractData(resp, Leave.class);
    assertEquals(1, leaves.size());
  }

  @Test
  void administration_cannot_delete_leave() {
    HrApi api = new HrApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(() -> api.deleteLeaveById(ADMIN_ID, COMPANY1_ID, LEAVE1_ID));
  }

  @Test
  void administration_can_get_leave_balances() throws Exception {
    HrApi api = new HrApi(anApiClient(ADMINISTRATION_TOKEN));

    PaginatedResponse resp = api.getLeaveBalances(ADMIN_ID, COMPANY1_ID, 2026);


    List<LeaveBalance> balances = extractData(resp, LeaveBalance.class);

    assertNotNull(balances);
  }

  @Test
  void administration_can_get_employees_without_leave() throws Exception {
    HrApi api = new HrApi(anApiClient(ADMINISTRATION_TOKEN));

    var employees = api.getEmployeesWithoutLeave(ADMIN_ID, COMPANY1_ID, 2026);

    assertNotNull(employees);
  }

  @Test
  void admin_cannot_create_leave_with_invalid_data() {
    HrApi api = new HrApi(anApiClient(ADMIN_TOKEN));
    CrupdateLeave invalid = someCreatableLeave();
    invalid.setStartDate(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Start date is mandatory\"}",
        () -> api.crupdateLeaves(ADMIN_ID, COMPANY1_ID, List.of(invalid)));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
