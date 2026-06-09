package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.SentryConf;
import com.example.demo.client.api.OtherExpenseTypeApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateOtherExpenseType;
import com.example.demo.client.model.OtherExpenseType;
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
@ContextConfiguration(initializers = OtherExpenseTypeIT.ContextInitializer.class)
class OtherExpenseTypeIT {
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
  void administration_can_get_other_expense_type_by_id() throws Exception {
    OtherExpenseTypeApi api = new OtherExpenseTypeApi(anApiClient(ADMINISTRATION_TOKEN));

    OtherExpenseType actual =
        api.getOtherExpenseTypeById(ADMIN_ID, COMPANY1_ID, OTHER_EXPENSE_TYPE1_ID);
    OtherExpenseType expected = otherExpenseType1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_other_expense_type_by_id() {
    OtherExpenseTypeApi api = new OtherExpenseTypeApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getOtherExpenseTypeById(ADMIN_ID, COMPANY1_ID, OTHER_EXPENSE_TYPE1_ID));
  }

  @Test
  void administration_can_get_all_other_expense_types() throws Exception {
    OtherExpenseTypeApi api = new OtherExpenseTypeApi(anApiClient(ADMINISTRATION_TOKEN));

    List<OtherExpenseType> types = api.getOtherExpenseTypes(ADMIN_ID, COMPANY1_ID);

    assertEquals(2, types.size());
    assertTrue(types.stream().anyMatch(t -> OTHER_EXPENSE_TYPE1_ID.equals(t.getId())));
    assertTrue(types.stream().anyMatch(t -> OTHER_EXPENSE_TYPE2_ID.equals(t.getId())));
  }

  @Test
  @DirtiesContext
  void admin_can_update_other_expense_types() throws Exception {
    OtherExpenseTypeApi api = new OtherExpenseTypeApi(anApiClient(ADMIN_TOKEN));
    CrupdateOtherExpenseType toUpdate =
        otherExpenseTypeToCrupdateOtherExpenseType(otherExpenseType1());
    toUpdate.setDescription("Frais logistiques et transport ajustes");

    List<OtherExpenseType> updated =
        api.crupdateOtherExpenseTypes(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(OTHER_EXPENSE_TYPE1_ID, updated.get(0).getId());
    assertEquals("Frais logistiques et transport ajustes", updated.get(0).getDescription());
  }

  @Test
  void employee_cannot_create_other_expense_types() {
    OtherExpenseTypeApi api = new OtherExpenseTypeApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdateOtherExpenseTypes(
                EMPLOYEE_ID, COMPANY1_ID, List.of(someCreatableOtherExpenseType())));
  }

  @Test
  void administration_cannot_delete_other_expense_type() {
    OtherExpenseTypeApi api = new OtherExpenseTypeApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () -> api.deleteOtherExpenseTypeById(ADMIN_ID, COMPANY1_ID, OTHER_EXPENSE_TYPE1_ID));
  }

  @Test
  void admin_cannot_create_other_expense_type_without_name() {
    OtherExpenseTypeApi api = new OtherExpenseTypeApi(anApiClient(ADMIN_TOKEN));
    CrupdateOtherExpenseType invalid = someCreatableOtherExpenseType();
    invalid.setName(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Other expense type name is mandatory\"}",
        () -> api.crupdateOtherExpenseTypes(ADMIN_ID, COMPANY1_ID, List.of(invalid)));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
