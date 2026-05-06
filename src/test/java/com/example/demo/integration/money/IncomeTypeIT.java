package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.SentryConf;
import com.example.demo.client.api.IncomeTypeApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateIncomeType;
import com.example.demo.client.model.IncomeType;
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
@ContextConfiguration(initializers = IncomeTypeIT.ContextInitializer.class)
class IncomeTypeIT {
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
  void administration_can_get_all_income_types() throws Exception {
    IncomeTypeApi api = new IncomeTypeApi(anApiClient(ADMINISTRATION_TOKEN));

    List<IncomeType> incomeTypes = api.getIncomeTypes(COMPANY1_ID);

    assertEquals(2, incomeTypes.size());
    assertTrue(
        incomeTypes.stream().anyMatch(incomeType -> INCOME_TYPE1_ID.equals(incomeType.getId())));
    assertTrue(
        incomeTypes.stream().anyMatch(incomeType -> INCOME_TYPE2_ID.equals(incomeType.getId())));
  }

  @Test
  @DirtiesContext
  void admin_can_update_income_types() throws Exception {
    IncomeTypeApi api = new IncomeTypeApi(anApiClient(ADMIN_TOKEN));
    CrupdateIncomeType toUpdate = incomeTypeToCrupdateIncomeType(incomeType1());
    toUpdate.setDescription("Revenus contractuels client");

    List<IncomeType> updated = api.crupdateIncomeTypes(COMPANY1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(INCOME_TYPE1_ID, updated.get(0).getId());
    assertEquals("Revenus contractuels client", updated.get(0).getDescription());
  }

  @Test
  void employee_cannot_create_income_types() {
    IncomeTypeApi api = new IncomeTypeApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () -> api.crupdateIncomeTypes(COMPANY1_ID, List.of(someCreatableIncomeType())));
  }

  @Test
  void administration_cannot_delete_income_type() {
    IncomeTypeApi api = new IncomeTypeApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(() -> api.deleteIncomeTypeById(COMPANY1_ID, INCOME_TYPE1_ID));
  }

  @Test
  void admin_cannot_create_income_type_without_name() {
    IncomeTypeApi api = new IncomeTypeApi(anApiClient(ADMIN_TOKEN));
    CrupdateIncomeType invalid = someCreatableIncomeType();
    invalid.setName(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Income type name is mandatory\"}",
        () -> api.crupdateIncomeTypes(COMPANY1_ID, List.of(invalid)));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
