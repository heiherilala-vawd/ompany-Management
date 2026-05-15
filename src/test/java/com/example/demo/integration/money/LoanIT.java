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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
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

    List<Loan> loans = api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null, null);

    assertEquals(5, loans.size());
    assertTrue(loans.stream().anyMatch(loan -> LOAN1_ID.equals(loan.getId())));
    assertTrue(loans.stream().anyMatch(loan -> LOAN2_ID.equals(loan.getId())));
    assertTrue(loans.stream().anyMatch(loan -> LOAN3_ID.equals(loan.getId())));
    assertTrue(loans.stream().anyMatch(loan -> LOAN4_ID.equals(loan.getId())));
    assertTrue(loans.stream().anyMatch(loan -> LOAN5_ID.equals(loan.getId())));
  }

  @Test
  void employee_cannot_get_all_loans() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    LoanApi api = new LoanApi(employeeClient);

    assertThrowsForbiddenException(
        () -> api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null, null));
  }

  @Test
  void admin_can_filter_loans_by_lender() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    List<Loan> loans = api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null, "BNI");

    assertEquals(1, loans.size());
    assertEquals(LOAN1_ID, loans.get(0).getId());
  }

  @Test
  void admin_can_filter_loans_by_amount() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    List<Loan> loans = api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, 5000000, null);

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

  @Test
  void admin_can_get_loan_with_status_paid() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    Loan actual = api.getLoanById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN3_ID);
    assertEquals(0, actual.getRemainingAmount());
    assertEquals(LoanStatus.PAID, actual.getStatus());
  }

  @Test
  void admin_can_get_loan_with_status_defaulted() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    Loan actual = api.getLoanById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN4_ID);
    assertEquals(2000000, actual.getRemainingAmount());
    assertEquals(LoanStatus.DEFAULTED, actual.getStatus());
  }

  @Test
  void admin_can_get_loan_with_status_paid_before_due_date() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    Loan actual = api.getLoanById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN5_ID);
    assertEquals(0, actual.getRemainingAmount());
    assertEquals(LoanStatus.PAID, actual.getStatus());
  }

  // ========== VALIDATION : LOAN ==========

  @Test
  void admin_cannot_create_loan_with_amount_zero() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    CrupdateLoan invalidLoan = someCreatableLoan();
    invalidLoan.setAmount(0);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Loan amount must be positive\"}",
        () -> api.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidLoan)));
  }

  @Test
  void admin_cannot_create_loan_with_null_amount() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    CrupdateLoan invalidLoan = someCreatableLoan();
    invalidLoan.setAmount(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Loan amount must be positive\"}",
        () -> api.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidLoan)));
  }

  @Test
  void admin_cannot_create_loan_with_blank_lender() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    CrupdateLoan invalidLoan = someCreatableLoan();
    invalidLoan.setLender("");

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Lender is mandatory for loan\"}",
        () -> api.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidLoan)));
  }

  @Test
  void admin_cannot_create_loan_with_null_interest_rate() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    CrupdateLoan invalidLoan = someCreatableLoan();
    invalidLoan.setInterestRate(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Interest rate must be non-negative\"}",
        () -> api.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidLoan)));
  }

  @Test
  void admin_cannot_create_loan_with_negative_interest_rate() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    CrupdateLoan invalidLoan = someCreatableLoan();
    invalidLoan.setInterestRate(-500);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Interest rate must be non-negative\"}",
        () -> api.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidLoan)));
  }

  @Test
  void admin_cannot_create_loan_with_null_start_date() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    CrupdateLoan invalidLoan = someCreatableLoan();
    invalidLoan.setStartDate(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Start date is mandatory for loan\"}",
        () -> api.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidLoan)));
  }

  @Test
  void admin_cannot_create_loan_without_job() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    CrupdateLoan invalidLoan = someCreatableLoan();
    invalidLoan.setJobId(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Loan must be associated with a job\"}",
        () -> api.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidLoan)));
  }

  @Test
  void admin_cannot_create_loan_with_empty_list() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Loan list cannot be null or empty\"}",
        () -> api.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of()));
  }

  // ========== VALIDATION : REPAYMENT ==========

  @Test
  void admin_cannot_create_repayment_with_amount_zero() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    CrupdateLoanRepayment invalid = someCreatableRepayment();
    invalid.setAmount(0);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Repayment amount must be positive\"}",
        () ->
            api.crupdateLoanRepayments(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, List.of(invalid)));
  }

  @Test
  void admin_cannot_create_repayment_with_null_amount() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    CrupdateLoanRepayment invalid = someCreatableRepayment();
    invalid.setAmount(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Repayment amount must be positive\"}",
        () ->
            api.crupdateLoanRepayments(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, List.of(invalid)));
  }

  @Test
  void admin_cannot_create_repayment_without_payment_date() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    CrupdateLoanRepayment invalid = someCreatableRepayment();
    invalid.setPaymentDate(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Payment date is mandatory for loan repayment\"}",
        () ->
            api.crupdateLoanRepayments(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, List.of(invalid)));
  }

  @Test
  void admin_cannot_create_repayment_without_loan() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    CrupdateLoanRepayment invalid = someCreatableRepayment();
    invalid.setLoanId(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Loan repayment must be linked to a loan\"}",
        () ->
            api.crupdateLoanRepayments(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, List.of(invalid)));
  }

  @Test
  void admin_cannot_create_repayment_with_nonexistent_loan() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    CrupdateLoanRepayment invalid = someCreatableRepayment();
    invalid.setLoanId("nonexistent_loan_id");

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Loan repayment must be linked to a loan\"}",
        () ->
            api.crupdateLoanRepayments(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, List.of(invalid)));
  }

  // ========== STATUS ==========

  @Test
  void admin_can_get_loan_with_status_active_and_due_date() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    Loan actual = api.getLoanById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID);
    assertTrue(actual.getRemainingAmount() > 0);
    assertEquals(LoanStatus.ACTIVE, actual.getStatus());
  }

  @Test
  void admin_can_get_loan_with_status_active_no_due_date() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    Loan actual = api.getLoanById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN2_ID);
    assertTrue(actual.getRemainingAmount() > 0);
    assertNull(actual.getDueDate());
    assertEquals(LoanStatus.ACTIVE, actual.getStatus());
  }

  // ========== FILTERS ==========

  @Test
  void admin_can_filter_loans_by_description() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    List<Loan> loans =
        api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, "equipement", null, null);

    assertEquals(1, loans.size());
    assertEquals(LOAN2_ID, loans.get(0).getId());
  }

  @Test
  void admin_can_filter_loans_by_description_case_insensitive() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    List<Loan> loans =
        api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, "CONSTRUCTION", null, null);

    assertEquals(1, loans.size());
    assertEquals(LOAN1_ID, loans.get(0).getId());
  }

  @Test
  void admin_can_filter_loans_by_lender_case_insensitive() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    List<Loan> loans = api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null, "bni");

    assertEquals(1, loans.size());
    assertEquals(LOAN1_ID, loans.get(0).getId());
  }

  @Test
  void admin_gets_empty_list_when_no_loan_matches_filter() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    List<Loan> loans =
        api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, 999999999, null);

    assertTrue(loans.isEmpty());
  }

  @Test
  void admin_can_filter_loans_by_combined_filters() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    List<Loan> loans =
        api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, 5000000, "BNI Madagascar");

    assertEquals(1, loans.size());
    assertEquals(LOAN1_ID, loans.get(0).getId());
  }

  // ========== PAGINATION ==========

  @Test
  void admin_can_paginate_loans() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    List<Loan> page1 = api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 2, null, null, null);
    assertEquals(2, page1.size());

    List<Loan> page2 = api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 2, 2, null, null, null);
    assertEquals(2, page2.size());

    List<Loan> page3 = api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 3, 2, null, null, null);
    assertEquals(1, page3.size());
  }

  // ========== AUTHORIZATION ==========

  @Test
  void employee_cannot_get_loan_by_id() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    LoanApi api = new LoanApi(employeeClient);

    assertThrowsForbiddenException(
        () -> api.getLoanById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID));
  }

  @Test
  void warehouse_cannot_get_loans() {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    LoanApi api = new LoanApi(warehouseClient);

    assertThrowsForbiddenException(
        () -> api.getLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null, null));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_loan_repayment() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    api.deleteLoanRepaymentById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, REPAYMENT1_ID);

    List<LoanRepayment> repayments =
        api.getLoanRepayments(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, 1, 100);
    assertEquals(1, repayments.size());
  }

  @Test
  void administration_cannot_delete_loan_repayment() {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(administrationClient);

    assertThrowsForbiddenException(
        () ->
            api.deleteLoanRepaymentById(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, REPAYMENT1_ID));
  }

  @Test
  void admin_cannot_delete_loan_repayment_with_bad_token() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(badClient);

    assertThrowsNotAuthorizedException(
        () ->
            api.deleteLoanRepaymentById(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, REPAYMENT1_ID));
  }

  // ========== INTEREST CALCULATION ==========

  @Test
  @DirtiesContext
  void admin_can_create_repayment_on_same_day_as_start_date() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    CrupdateLoanRepayment repayment = someCreatableRepayment();
    repayment.setPaymentDate(LocalDate.of(2024, 2, 1)); // same as loan1 start date

    List<LoanRepayment> created =
        api.crupdateLoanRepayments(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, List.of(repayment));

    assertEquals(1, created.size());
    LoanRepayment saved = created.get(0);
    assertEquals(repayment.getAmount(), saved.getPrincipalPortion() + saved.getInterestPortion());
    assertEquals(0, saved.getInterestPortion()); // 0 days → no interest
    assertEquals(repayment.getAmount(), saved.getPrincipalPortion());
  }

  @Test
  @DirtiesContext
  void admin_can_create_repayment_before_start_date() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    CrupdateLoanRepayment repayment = someCreatableRepayment();
    repayment.setPaymentDate(LocalDate.of(2024, 1, 15)); // before loan1 start date (2024-02-01)

    List<LoanRepayment> created =
        api.crupdateLoanRepayments(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, List.of(repayment));

    assertEquals(1, created.size());
    LoanRepayment saved = created.get(0);
    assertEquals(repayment.getAmount(), saved.getPrincipalPortion() + saved.getInterestPortion());
    assertEquals(0, saved.getInterestPortion()); // days clamped to 0
    assertEquals(repayment.getAmount(), saved.getPrincipalPortion());
  }

  @Test
  @DirtiesContext
  void admin_can_create_repayment_with_amount_less_than_interest() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    CrupdateLoanRepayment repayment = someCreatableRepayment();
    repayment.setPaymentDate(LocalDate.of(2025, 1, 1)); // far in future → lots of accrued interest
    repayment.setAmount(100); // very small, likely < interest portion

    List<LoanRepayment> created =
        api.crupdateLoanRepayments(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, List.of(repayment));

    assertEquals(1, created.size());
    LoanRepayment saved = created.get(0);
    assertEquals(100, saved.getAmount().intValue());
    assertEquals(100, saved.getInterestPortion()); // all to interest
    assertEquals(0, saved.getPrincipalPortion()); // none to principal
  }

  @Test
  @DirtiesContext
  void admin_can_create_repayment_with_zero_interest_loan() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi loanApi = new LoanApi(adminClient);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    CrupdateLoan zeroInterestLoan = someCreatableLoan();
    zeroInterestLoan.setInterestRate(0);
    zeroInterestLoan.setAmount(1000000);
    zeroInterestLoan.setDescription("Pret sans interet");

    List<Loan> createdLoans =
        loanApi.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(zeroInterestLoan));
    String zeroInterestLoanId = createdLoans.get(0).getId();

    CrupdateLoanRepayment repayment =
        new CrupdateLoanRepayment()
            .id(UUID.randomUUID().toString())
            .loanId(zeroInterestLoanId)
            .paymentDate(LocalDate.of(2025, 1, 1))
            .amount(100000);

    List<LoanRepayment> created =
        api.crupdateLoanRepayments(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, zeroInterestLoanId, List.of(repayment));

    assertEquals(1, created.size());
    LoanRepayment saved = created.get(0);
    assertEquals(0, saved.getInterestPortion()); // 0% rate → no interest
    assertEquals(100000, saved.getPrincipalPortion()); // all to principal
  }

  @Test
  @DirtiesContext
  void admin_can_update_repayment_without_recalculating_portions() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    CrupdateLoanRepayment newRepayment = someCreatableRepayment();
    List<LoanRepayment> created =
        api.crupdateLoanRepayments(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, List.of(newRepayment));

    String repaymentId = created.get(0).getId();
    int originalPrincipal = created.get(0).getPrincipalPortion();
    int originalInterest = created.get(0).getInterestPortion();

    assertEquals(newRepayment.getAmount(), created.get(0).getAmount());
    assertEquals(originalPrincipal + originalInterest, created.get(0).getAmount().intValue());

    CrupdateLoanRepayment updatedRepayment =
        new CrupdateLoanRepayment()
            .id(repaymentId)
            .loanId(LOAN1_ID)
            .paymentDate(LocalDate.of(2024, 5, 1))
            .amount(1000000);

    List<LoanRepayment> updated =
        api.crupdateLoanRepayments(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, List.of(updatedRepayment));

    assertEquals(originalPrincipal, updated.get(0).getPrincipalPortion());
    assertEquals(originalInterest, updated.get(0).getInterestPortion());
  }

  @Test
  @DirtiesContext
  void admin_can_create_repayment_with_principal_capped_at_outstanding() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi loanApi = new LoanApi(adminClient);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    CrupdateLoan smallLoan = someCreatableLoan();
    smallLoan.setAmount(100000);
    smallLoan.setInterestRate(0);

    List<Loan> createdLoans =
        loanApi.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(smallLoan));
    String smallLoanId = createdLoans.get(0).getId();

    CrupdateLoanRepayment repayment =
        new CrupdateLoanRepayment()
            .id(UUID.randomUUID().toString())
            .loanId(smallLoanId)
            .paymentDate(LocalDate.of(2024, 7, 1))
            .amount(200000); // more than outstanding

    List<LoanRepayment> created =
        api.crupdateLoanRepayments(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, smallLoanId, List.of(repayment));

    assertEquals(1, created.size());
    LoanRepayment saved = created.get(0);
    assertEquals(100000, saved.getPrincipalPortion()); // capped at outstanding
    assertEquals(100000, saved.getInterestPortion()); // remainder after capping
    assertEquals(200000, saved.getAmount().intValue());
  }

  // ========== OVERPAYMENT ==========

  @Test
  @DirtiesContext
  void admin_can_overpay_loan_and_status_is_paid() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi loanApi = new LoanApi(adminClient);
    LoanRepaymentApi repaymentApi = new LoanRepaymentApi(adminClient);

    CrupdateLoanRepayment repayment =
        new CrupdateLoanRepayment()
            .id(UUID.randomUUID().toString())
            .loanId(LOAN2_ID)
            .paymentDate(LocalDate.of(2024, 7, 1))
            .amount(5000000); // more than loan2 amount (3,000,000)

    repaymentApi.crupdateLoanRepayments(
        COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN2_ID, List.of(repayment));

    Loan actual = loanApi.getLoanById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN2_ID);
    assertTrue(actual.getRemainingAmount() <= 0);
    assertEquals(LoanStatus.PAID, actual.getStatus());
  }

  @Test
  @DirtiesContext
  void admin_can_create_loan_with_zero_interest_and_verify_status() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi loanApi = new LoanApi(adminClient);

    CrupdateLoan zeroInterestLoan = someCreatableLoan();
    zeroInterestLoan.setInterestRate(0);

    List<Loan> created =
        loanApi.crupdateLoans(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(zeroInterestLoan));

    assertEquals(1, created.size());
    Loan saved = created.get(0);
    assertEquals(0, saved.getInterestRate().intValue());
    assertEquals(LoanStatus.ACTIVE, saved.getStatus());
    assertEquals(zeroInterestLoan.getAmount(), saved.getRemainingAmount());
  }

  // ========== NOT FOUND ==========

  @Test
  void admin_gets_not_found_when_loan_does_not_exist() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanApi api = new LoanApi(adminClient);

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"Loan with id nonexistent_loan not found\"}",
        () -> api.getLoanById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, "nonexistent_loan"));
  }

  @Test
  void administration_can_get_loan_repayment_not_found() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    LoanRepaymentApi api = new LoanRepaymentApi(adminClient);

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"Loan repayment with id nonexistent_repayment not found\"}",
        () ->
            api.getLoanRepaymentById(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, LOAN1_ID, "nonexistent_repayment"));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
