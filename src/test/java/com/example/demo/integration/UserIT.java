package com.example.demo.integration;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.UsersApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateUser;
import com.example.demo.client.model.PaginatedResponse;
import com.example.demo.client.model.Role;
import com.example.demo.client.model.Sex;
import com.example.demo.client.model.User;
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
@ContextConfiguration(initializers = UserIT.ContextInitializer.class)
class UserIT {
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
  void admin_can_get_any_user_by_id() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    UsersApi api = new UsersApi(adminClient);

    User admin = api.getUserById(ADMIN_ID);
    User employee = api.getUserById(EMPLOYEE_ID);
    User warehouse = api.getUserById(WAREHOUSE_ID);

    assertEquals(admin1(), admin);
    assertEquals(employee1(), employee);
    assertEquals(warehouseWorker1(), warehouse);
  }

  @Test
  void employee_can_only_get_own_user_by_id() throws Exception {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    UsersApi api = new UsersApi(employeeClient);

    User ownUser = api.getUserById(EMPLOYEE_ID);
    assertEquals(employee1(), ownUser);

    assertThrowsForbiddenException(() -> api.getUserById(ADMIN_ID));
    assertThrowsForbiddenException(() -> api.getUserById(WAREHOUSE_ID));
  }

  @Test
  void user_with_bad_token_cannot_get_any_user() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    UsersApi api = new UsersApi(badClient);

    assertThrowsNotAuthorizedException(() -> api.getUserById(ADMIN_ID));
  }

  @Test
  void admin_can_get_all_users() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    UsersApi api = new UsersApi(adminClient);

    PaginatedResponse resp = api.getUsers(1, 100, null, null, null, null, null);

    List<User> users = extractData(resp, User.class);

    assertTrue(users.size() >= 7);
    assertTrue(users.contains(admin1()));
    assertTrue(users.contains(employee1()));
  }

  @Test
  void employee_cannot_get_all_users() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    UsersApi api = new UsersApi(employeeClient);

    assertThrowsForbiddenException(() -> api.getUsers(1, 100, null, null, null, null, null));
  }

  @Test
  @DirtiesContext
  void admin_can_update_users() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    UsersApi api = new UsersApi(adminClient);

    CrupdateUser newUser1 = userToCrupdateUser(admin1());
    CrupdateUser newUser2 = userToCrupdateUser(employee1());
    CrupdateUser newUser3 = userToCrupdateUser(administration1());

    newUser1.setLastName("new last name");
    newUser2.setFirstName("new first name");
    newUser3.setSex(Sex.F);

    User newAdmin1 = admin1();
    newAdmin1.setLastName("new last name");

    List<User> created = api.crupdateUsers(COMPANY1_ID, List.of(newUser1, newUser2, newUser3));
    User user =
        created.stream().filter(u -> ADMIN_EMAIL.equals(u.getEmail())).findFirst().orElse(null);
    newAdmin1.setUpdatedAt(user.getUpdatedAt());
    newAdmin1.setUpdatedBy(user.getUpdatedBy());

    assertEquals(3, created.size());
    assertNotNull(created.get(0).getId());
    assertTrue(created.contains(newAdmin1));
  }

  @Test
  void administration_cannot_promote_to_admin() {
    ApiClient adminClient = anApiClient(ADMINISTRATION_TOKEN);
    UsersApi api = new UsersApi(adminClient);

    CrupdateUser employeeToPromote = userToCrupdateUser(employee1());
    employeeToPromote.setRole(Role.ADMIN);

    assertThrowsApiException(
        "\"type\":\"403 FORBIDDEN\"",
        () -> api.crupdateUsers(COMPANY1_ID, List.of(employeeToPromote)));
  }

  @Test
  void administration_can_change_role_to_lower() throws Exception {
    ApiClient adminClient = anApiClient(ADMINISTRATION_TOKEN);
    UsersApi api = new UsersApi(adminClient);

    CrupdateUser employeeToUpdate = userToCrupdateUser(employee1());
    employeeToUpdate.setRole(Role.WAREHOUSE_WORKER);

    List<User> updated = api.crupdateUsers(COMPANY1_ID, List.of(employeeToUpdate));

    assertEquals(1, updated.size());
    assertEquals(Role.WAREHOUSE_WORKER, updated.get(0).getRole());
  }

  @Test
  @DirtiesContext
  void admin_can_create_new_user_via_crupdate() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    UsersApi api = new UsersApi(adminClient);

    CrupdateUser newUser = someCreatableUser();
    List<User> created = api.crupdateUsers(COMPANY1_ID, List.of(newUser));

    assertEquals(1, created.size());
    assertEquals(newUser.getEmail(), created.get(0).getEmail());
    assertEquals(newUser.getFirstName(), created.get(0).getFirstName());
    assertEquals(Role.EMPLOYEE, created.get(0).getRole());
  }

  @Test
  void employee_cannot_create_users() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    UsersApi api = new UsersApi(employeeClient);

    assertThrowsForbiddenException(
        () -> api.crupdateUsers(COMPANY1_ID, List.of(someCreatableUser())));
  }

  @Test
  void employee_cannot_delete_user() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    UsersApi api = new UsersApi(employeeClient);

    assertThrowsForbiddenException(() -> api.deleteUserById(USER1_ID));
  }

  @Test
  void admin_can_filter_users_by_first_name() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    UsersApi api = new UsersApi(adminClient);

    PaginatedResponse resp = api.getUsers(1, 100, null, "Alice", null, null, null);

    List<User> users = extractData(resp, User.class);

    assertEquals(1, users.size());
    assertEquals(user1(), users.get(0));
  }

  @Test
  void admin_can_filter_users_by_role() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    UsersApi api = new UsersApi(adminClient);

    PaginatedResponse resp = api.getUsers(1, 100, null, null, null, null, Role.EMPLOYEE);

    List<User> users = extractData(resp, User.class);

    assertTrue(users.stream().allMatch(u -> u.getRole() == Role.EMPLOYEE));
  }

  @Test
  void admin_can_filter_users_by_last_name() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    UsersApi api = new UsersApi(adminClient);

    PaginatedResponse resp = api.getUsers(1, 100, null, null, "Martin", null, null);

    List<User> users = extractData(resp, User.class);

    assertEquals(1, users.size());
    assertEquals(USER1_ID, users.get(0).getId());
  }

  @Test
  void admin_can_filter_users_by_email() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    UsersApi api = new UsersApi(adminClient);

    PaginatedResponse resp = api.getUsers(1, 100, null, null, null, USER1_EMAIL, null);

    List<User> users = extractData(resp, User.class);

    assertEquals(1, users.size());
    assertEquals(USER1_ID, users.get(0).getId());
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
