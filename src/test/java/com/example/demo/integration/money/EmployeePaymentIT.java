package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.EmployeePaymentApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateEmployeePayment;
import com.example.demo.client.model.EmployeePayment;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestUtils;
import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = EmployeePaymentIT.ContextInitializer.class)
class EmployeePaymentIT {
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
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("db/testdata/V99_1__testdata.sql"));
    }
  }

  @Test
  void employee_can_get_own_employee_payment_by_id() throws Exception {
    EmployeePaymentApi api = new EmployeePaymentApi(anApiClient(EMPLOYEE_TOKEN));

    EmployeePayment actual =
        api.getEmployeePaymentById(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, EMPLOYEE_PAYMENT1_ID);

    assertEquals(employeePayment1(), actual);
  }

  @Test
  void user_with_bad_token_cannot_get_employee_payment_by_id() {
    EmployeePaymentApi api = new EmployeePaymentApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () ->
            api.getEmployeePaymentById(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, EMPLOYEE_PAYMENT1_ID));
  }

  @Test
  void admin_can_get_all_employee_payments() throws Exception {
    EmployeePaymentApi api = new EmployeePaymentApi(anApiClient(ADMIN_TOKEN));

    List<EmployeePayment> employeePayments =
        api.getEmployeePayments(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, 1, 100, null);

    assertEquals(2, employeePayments.size());
    assertTrue(
        employeePayments.stream()
            .anyMatch(employeePayment -> EMPLOYEE_PAYMENT1_ID.equals(employeePayment.getId())));
    assertTrue(
        employeePayments.stream()
            .anyMatch(employeePayment -> EMPLOYEE_PAYMENT2_ID.equals(employeePayment.getId())));
  }

  @Test
  @DirtiesContext
  void employee_can_update_own_employee_payments() throws Exception {
    EmployeePaymentApi api = new EmployeePaymentApi(anApiClient(EMPLOYEE_TOKEN));

    CrupdateEmployeePayment paymentToUpdate =
        employeePaymentToCrupdateEmployeePayment(employeePayment1());
    paymentToUpdate.setPaymentDescription("Avance salaire chantier A verifiee");

    List<EmployeePayment> updatedPayments =
        api.crupdateEmployeePayments(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, List.of(paymentToUpdate));

    assertEquals(1, updatedPayments.size());
    assertEquals(EMPLOYEE_PAYMENT1_ID, updatedPayments.get(0).getId());
    assertEquals(
        "Avance salaire chantier A verifiee", updatedPayments.get(0).getPaymentDescription());
  }

  @Test
  void warehouse_worker_cannot_create_employee_payments() {
    EmployeePaymentApi api = new EmployeePaymentApi(anApiClient(WAREHOUSE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateEmployeePayments(
                COMPANY1_ID,
                JOB1_ID,
                EMPLOYEE_ID,
                EXPENSE1_ID,
                List.of(someCreatableEmployeePayment())));
  }

  @Test
  void employee_cannot_delete_employee_payment() {
    EmployeePaymentApi api = new EmployeePaymentApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.deleteEmployeePaymentById(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, EMPLOYEE_PAYMENT1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
