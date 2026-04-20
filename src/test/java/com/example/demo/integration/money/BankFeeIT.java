package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.BankFeeApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.BankFee;
import com.example.demo.client.model.CrupdateBankFee;
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
@ContextConfiguration(initializers = BankFeeIT.ContextInitializer.class)
class BankFeeIT {
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
  void administration_can_get_bank_fee_by_id() throws Exception {
    BankFeeApi api = new BankFeeApi(anApiClient(ADMINISTRATION_TOKEN));

    BankFee actual =
        api.getBankFeeById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, BANK_FEE1_ID);

    assertEquals(bankFee1(), actual);
  }

  @Test
  void user_with_bad_token_cannot_get_bank_fee_by_id() {
    BankFeeApi api = new BankFeeApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getBankFeeById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, BANK_FEE1_ID));
  }

  @Test
  void admin_can_get_all_bank_fees() throws Exception {
    BankFeeApi api = new BankFeeApi(anApiClient(ADMIN_TOKEN));

    List<BankFee> bankFees =
        api.getBankFees(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, 1, 100);

    assertEquals(2, bankFees.size());
    assertTrue(bankFees.stream().anyMatch(bankFee -> BANK_FEE1_ID.equals(bankFee.getId())));
    assertTrue(bankFees.stream().anyMatch(bankFee -> BANK_FEE2_ID.equals(bankFee.getId())));
  }

  @Test
  @DirtiesContext
  void administration_can_update_bank_fees() throws Exception {
    BankFeeApi api = new BankFeeApi(anApiClient(ADMINISTRATION_TOKEN));

    CrupdateBankFee bankFeeToUpdate = bankFeeToCrupdateBankFee(bankFee1());
    bankFeeToUpdate.setDescription("Frais virement fournisseur ajustes");

    List<BankFee> updatedBankFees =
        api.crupdateBankFees(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, List.of(bankFeeToUpdate));

    assertEquals(1, updatedBankFees.size());
    assertEquals(BANK_FEE1_ID, updatedBankFees.get(0).getId());
    assertEquals("Frais virement fournisseur ajustes", updatedBankFees.get(0).getDescription());
  }

  @Test
  void employee_cannot_create_bank_fees() {
    BankFeeApi api = new BankFeeApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateBankFees(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, List.of(someCreatableBankFee())));
  }

  @Test
  void administration_cannot_delete_bank_fee() {
    BankFeeApi api = new BankFeeApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () -> api.deleteBankFeeById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, BANK_FEE1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
