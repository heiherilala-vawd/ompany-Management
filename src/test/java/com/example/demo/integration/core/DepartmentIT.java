package com.example.demo.integration.core;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.DepartmentApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateDepartment;
import com.example.demo.client.model.Department;
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
@ContextConfiguration(initializers = DepartmentIT.ContextInitializer.class)
class DepartmentIT {
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
  void administration_can_get_department_by_id() throws Exception {
    DepartmentApi api = new DepartmentApi(anApiClient(ADMINISTRATION_TOKEN));

    Department actual = api.getDepartmentById(ADMIN_ID, COMPANY1_ID, DEPARTMENT1_ID);
    Department expected = department1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_department_by_id() {
    DepartmentApi api = new DepartmentApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getDepartmentById(ADMIN_ID, COMPANY1_ID, DEPARTMENT1_ID));
  }

  @Test
  void administration_can_get_all_departments() throws Exception {
    DepartmentApi api = new DepartmentApi(anApiClient(ADMINISTRATION_TOKEN));

    PaginatedResponse resp = api.getDepartments(ADMIN_ID, COMPANY1_ID, 1, 100);

    List<Department> departments = extractData(resp, Department.class);

    assertEquals(2, departments.size());
    assertTrue(departments.stream().anyMatch(d -> DEPARTMENT1_ID.equals(d.getId())));
    assertTrue(departments.stream().anyMatch(d -> DEPARTMENT2_ID.equals(d.getId())));
  }

  @Test
  void employee_cannot_get_all_departments() {
    DepartmentApi api = new DepartmentApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(() -> api.getDepartments(EMPLOYEE_ID, COMPANY1_ID, 1, 100));
  }

  @Test
  @DirtiesContext
  void admin_can_update_department() throws Exception {
    DepartmentApi api = new DepartmentApi(anApiClient(ADMIN_TOKEN));

    CrupdateDepartment toUpdate = departmentToCrupdateDepartment(department1());
    toUpdate.setName("Génie Civil - mis à jour");

    List<Department> updated = api.crupdateDepartments(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(DEPARTMENT1_ID, updated.get(0).getId());
    assertEquals("Génie Civil - mis à jour", updated.get(0).getName());
  }

  @Test
  void employee_cannot_create_department() {
    DepartmentApi api = new DepartmentApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateDepartments(EMPLOYEE_ID, COMPANY1_ID, List.of(someCreatableDepartment())));
  }

  @Test
  void administration_cannot_delete_department() {
    DepartmentApi api = new DepartmentApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () -> api.deleteDepartmentById(ADMIN_ID, COMPANY1_ID, DEPARTMENT1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_department() throws Exception {
    DepartmentApi api = new DepartmentApi(anApiClient(ADMIN_TOKEN));

    api.deleteDepartmentById(ADMIN_ID, COMPANY1_ID, DEPARTMENT1_ID);

    PaginatedResponse resp = api.getDepartments(ADMIN_ID, COMPANY1_ID, 1, 100);

    List<Department> departments = extractData(resp, Department.class);
    assertEquals(1, departments.size());
    assertEquals(DEPARTMENT2_ID, departments.get(0).getId());
  }

  @Test
  void admin_gets_not_found_when_department_does_not_exist() {
    DepartmentApi api = new DepartmentApi(anApiClient(ADMIN_TOKEN));

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"Department with id nonexistent_dept not found\"}",
        () -> api.getDepartmentById(ADMIN_ID, COMPANY1_ID, "nonexistent_dept"));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
