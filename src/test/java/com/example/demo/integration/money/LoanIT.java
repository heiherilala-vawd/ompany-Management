package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.LoanApi;
import com.example.demo.client.api.LoanRepaymentApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateLoan;
import com.example.demo.client.model.CrupdateLoanRepayment;
import com.example.demo.client.model.Loan;
import com.example.demo.client.model.LoanRepayment;
import com.example.demo.client.model.LoanStatus;
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
@ContextConfiguration(initializers = LoanIT.ContextInitializer.class)
class LoanIT {
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
  void administration_can_get_loan_by_id() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    LoanApi api = new LoanApi(administrationClient);

    Loan actual = api.getLoanById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID);
    Loan expected = loan1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    if (actual.getRepayments() != null && expected.getRepayments() != null) {
      for (int i = 0;
          i < Math.min(actual.getRepayments().size(), expected.getRepayments().size());
          i++) {
        var actualRep = actual.getRepayments().get(i);
        var expectedRep = expected.getRepayments().get(i);
        expectedRep.setCreatedAt(actualRep.getCreatedAt());
        expectedRep.setUpdatedAt(actualRep.getUpdatedAt());
        expectedRep.setCreatedBy(actualRep.getCreatedBy());
        expectedRep.setUpdatedBy(actualRep.getUpdatedBy());
        expectedRep.setComment(actualRep.getComment());
      }
    }

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_loan_by_id() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    LoanApi api = new LoanApi(badClient);

    assertThrowsNotAuthorizedException(
        () -> api.getLoanById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID));
  }

  @Test
  void admin_can_get_all_loans() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    List<Loan> loans =
        api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null, null, null);

    assertEquals(2, loans.size());
    assertTrue(loans.stream().anyMatch(loan -> LOAN1_ID.equals(loan.getId())));
    assertTrue(loans.stream().anyMatch(loan -> LOAN2_ID.equals(loan.getId())));
  }

  @Test
  void employee_cannot_get_all_loans() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    LoanApi api = new LoanApi(employeeClient);

    assertThrowsForbiddenException(
        () -> api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null, null, null));
  }

  @Test
  void admin_can_filter_loans_by_lender() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    List<Loan> loans =
        api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null, "BNI", null);

    assertEquals(1, loans.size());
    assertEquals(LOAN1_ID, loans.get(0).getId());
  }

  @Test
  void admin_can_filter_loans_by_status() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    List<Loan> loans =
        api.getLoans(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null, null, LoanStatus.ACTIVE);

    assertEquals(2, loans.size());
  }

  @Test
  void admin_can_filter_loans_by_amount() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    List<Loan> loans =
        api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, 5000000, null, null);

    assertEquals(1, loans.size());
    assertEquals(LOAN1_ID, loans.get(0).getId());
  }

  @Test
  @DirtiesContext
  void administration_can_update_loans() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    LoanApi api = new LoanApi(administrationClient);

    CrupdateLoan loanToUpdate = loanToCrupdateLoan(loan1());
    loanToUpdate.setDescription("Emprunt construction entrepot mis a jour");

    List<Loan> updatedLoans =
        api.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(loanToUpdate));
    Loan updatedLoan = updatedLoans.get(0);

    assertEquals(1, updatedLoans.size());
    assertEquals(LOAN1_ID, updatedLoan.getId());
    assertEquals("Emprunt construction entrepot mis a jour", updatedLoan.getDescription());
    assertEquals(loan1().getAmount(), updatedLoan.getAmount());
  }

  @Test
  void employee_cannot_create_loans() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    LoanApi api = new LoanApi(employeeClient);

    assertThrowsForbiddenException(
        () -> api.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(someCreatableLoan())));
  }

  @Test
  void administration_cannot_delete_loan() {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    LoanApi api = new LoanApi(administrationClient);

    assertThrowsForbiddenException(
        () -> api.deleteLoanById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID));
  }

  @Test
  void admin_cannot_create_loan_with_negative_amount() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    CrupdateLoan invalidLoan = someCreatableLoan();
    invalidLoan.setAmount(-5000);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Loan amount must be positive\"}",
        () -> api.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidLoan)));
  }

  @Test
  void admin_cannot_create_loan_without_lender() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    CrupdateLoan invalidLoan = someCreatableLoan();
    invalidLoan.setLender(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Lender is mandatory for loan\"}",
        () -> api.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidLoan)));
  }

  @Test
  void admin_can_get_loan_repayment_by_id() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    LoanRepayment actual =
        api.getLoanRepaymentById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, REPAYMENT1_ID);
    LoanRepayment expected = repayment1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void admin_can_get_all_repayments_for_loan() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    List<LoanRepayment> repayments =
        api.getLoanRepayments(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, 1, 100);

    assertEquals(2, repayments.size());
  }

  @Test
  @DirtiesContext
  void admin_can_create_loan_repayment_with_interest_calculation() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    CrupdateLoanRepayment newRepayment = someCreatableRepayment();

    List<LoanRepayment> created =
        api.crupdateLoanRepayments(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, List.of(newRepayment));

    assertEquals(1, created.size());
    LoanRepayment saved = created.get(0);
    assertEquals(newRepayment.getId(), saved.getId());
    assertEquals(newRepayment.getPaymentDate(), saved.getPaymentDate());
    assertEquals(newRepayment.getAmount(), saved.getAmount());
    assertTrue(saved.getPrincipalPortion() > 0);
    assertTrue(saved.getInterestPortion() > 0);
    assertEquals(
        saved.getAmount().intValue(), saved.getPrincipalPortion() + saved.getInterestPortion());
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
