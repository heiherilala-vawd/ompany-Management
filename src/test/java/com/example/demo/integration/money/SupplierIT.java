package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.SupplierApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateSupplier;
import com.example.demo.client.model.Supplier;
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
@ContextConfiguration(initializers = SupplierIT.ContextInitializer.class)
class SupplierIT {
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

  private static Supplier expectedSupplier1() {
    Supplier s = supplier1();
    s.setCreatedAt(null);
    s.setUpdatedAt(null);
    s.setCreatedBy(null);
    s.setUpdatedBy(null);
    s.setComment(null);
    return s;
  }

  @Test
  void admin_can_get_supplier_by_id() throws Exception {
    SupplierApi api = new SupplierApi(anApiClient(ADMIN_TOKEN));
    Supplier actual = api.getSupplierById(ADMIN_ID, COMPANY1_ID, SUPPLIER1_ID);
    actual.setCreatedAt(null);
    actual.setUpdatedAt(null);
    actual.setCreatedBy(null);
    actual.setUpdatedBy(null);
    assertEquals(expectedSupplier1(), actual);
  }

  @Test
  void user_with_bad_token_cannot_get_supplier() {
    SupplierApi api = new SupplierApi(anApiClient(BAD_TOKEN));
    assertThrowsNotAuthorizedException(
        () -> api.getSupplierById(ADMIN_ID, COMPANY1_ID, SUPPLIER1_ID));
  }

  @Test
  void admin_can_get_all_suppliers() throws Exception {
    SupplierApi api = new SupplierApi(anApiClient(ADMIN_TOKEN));
    List<Supplier> suppliers = api.getSuppliers(ADMIN_ID, COMPANY1_ID);
    assertEquals(2, suppliers.size());
    assertTrue(suppliers.stream().anyMatch(s -> SUPPLIER1_ID.equals(s.getId())));
    assertTrue(suppliers.stream().anyMatch(s -> SUPPLIER2_ID.equals(s.getId())));
  }

  @Test
  void employee_cannot_get_suppliers() {
    SupplierApi api = new SupplierApi(anApiClient(EMPLOYEE_TOKEN));
    assertThrowsForbiddenException(() -> api.getSuppliers(EMPLOYEE_ID, COMPANY1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_create_supplier() throws Exception {
    SupplierApi api = new SupplierApi(anApiClient(ADMIN_TOKEN));
    CrupdateSupplier toCreate = someCreatableSupplier();
    List<Supplier> created = api.crupdateSuppliers(ADMIN_ID, COMPANY1_ID, List.of(toCreate));
    assertEquals(1, created.size());
    assertEquals(toCreate.getName(), created.get(0).getName());
  }

  @Test
  @DirtiesContext
  void admin_can_update_supplier() throws Exception {
    SupplierApi api = new SupplierApi(anApiClient(ADMIN_TOKEN));
    CrupdateSupplier toUpdate = supplierToCrupdateSupplier(supplier1());
    toUpdate.setName("Fournitures Pro Modifié");
    List<Supplier> updated = api.crupdateSuppliers(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));
    assertEquals(1, updated.size());
    assertEquals(SUPPLIER1_ID, updated.get(0).getId());
    assertEquals("Fournitures Pro Modifié", updated.get(0).getName());
  }

  @Test
  void employee_cannot_create_supplier() {
    SupplierApi api = new SupplierApi(anApiClient(EMPLOYEE_TOKEN));
    assertThrowsForbiddenException(
        () -> api.crupdateSuppliers(EMPLOYEE_ID, COMPANY1_ID, List.of(someCreatableSupplier())));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_supplier() throws Exception {
    SupplierApi api = new SupplierApi(anApiClient(ADMIN_TOKEN));
    CrupdateSupplier toDelete = someCreatableSupplier();
    api.crupdateSuppliers(ADMIN_ID, COMPANY1_ID, List.of(toDelete));
    api.deleteSupplierById(ADMIN_ID, COMPANY1_ID, toDelete.getId());
    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"Supplier with id "
            + toDelete.getId()
            + " not found\"}",
        () -> api.getSupplierById(ADMIN_ID, COMPANY1_ID, toDelete.getId()));
  }

  @Test
  void administration_cannot_delete_supplier() {
    SupplierApi api = new SupplierApi(anApiClient(ADMINISTRATION_TOKEN));
    assertThrowsForbiddenException(
        () -> api.deleteSupplierById(ADMIN_ID, COMPANY1_ID, SUPPLIER1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
