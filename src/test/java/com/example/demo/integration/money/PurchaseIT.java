package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.PurchaseApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdatePurchase;
import com.example.demo.client.model.Purchase;
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
@ContextConfiguration(initializers = PurchaseIT.ContextInitializer.class)
class PurchaseIT {
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
  void warehouse_worker_can_get_purchase_by_id() throws Exception {
    PurchaseApi api = new PurchaseApi(anApiClient(WAREHOUSE_TOKEN));

    Purchase actual = api.getPurchaseById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, PURCHASE1_ID);

    assertEquals(purchase1(), actual);
  }

  @Test
  void user_with_bad_token_cannot_get_purchase_by_id() {
    PurchaseApi api = new PurchaseApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getPurchaseById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, PURCHASE1_ID));
  }

  @Test
  void admin_can_get_all_purchases() throws Exception {
    PurchaseApi api = new PurchaseApi(anApiClient(ADMIN_TOKEN));

    List<Purchase> purchases =
        api.getPurchases(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, null);

    assertEquals(2, purchases.size());
    assertTrue(purchases.stream().anyMatch(purchase -> PURCHASE1_ID.equals(purchase.getId())));
    assertTrue(purchases.stream().anyMatch(purchase -> PURCHASE2_ID.equals(purchase.getId())));
  }

  @Test
  void admin_can_filter_purchases_by_supplier() throws Exception {
    PurchaseApi api = new PurchaseApi(anApiClient(ADMIN_TOKEN));

    List<Purchase> purchases =
        api.getPurchases(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, WAREHOUSE1_ID, null);

    assertEquals(1, purchases.size());
    assertEquals(PURCHASE1_ID, purchases.get(0).getId());
  }

  @Test
  void admin_can_filter_purchases_by_is_equipment() throws Exception {
    PurchaseApi api = new PurchaseApi(anApiClient(ADMIN_TOKEN));

    List<Purchase> purchases =
        api.getPurchases(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, 1, 100, null, false);

    assertEquals(1, purchases.size());
    assertEquals(PURCHASE2_ID, purchases.get(0).getId());
  }

  @Test
  @DirtiesContext
  void warehouse_worker_can_update_purchases() throws Exception {
    PurchaseApi api = new PurchaseApi(anApiClient(WAREHOUSE_TOKEN));

    CrupdatePurchase purchaseToUpdate = purchaseToCrupdatePurchase(purchase1());
    purchaseToUpdate.setQuantity(5);

    List<Purchase> updatedPurchases =
        api.crupdatePurchases(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(purchaseToUpdate));

    assertEquals(1, updatedPurchases.size());
    assertEquals(PURCHASE1_ID, updatedPurchases.get(0).getId());
    assertEquals(5, updatedPurchases.get(0).getQuantity());
  }

  @Test
  void employee_cannot_create_purchases() {
    PurchaseApi api = new PurchaseApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () ->
            api.crupdatePurchases(
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(someCreatablePurchase())));
  }

  @Test
  void administration_cannot_delete_purchase() {
    PurchaseApi api = new PurchaseApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () -> api.deletePurchaseById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, PURCHASE1_ID));
  }

  @Test
  void admin_cannot_create_purchase_with_negative_quantity() {
    PurchaseApi api = new PurchaseApi(anApiClient(ADMIN_TOKEN));

    CrupdatePurchase invalidPurchase = purchaseToCrupdatePurchase(purchase1());
    invalidPurchase.setQuantity(-5);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Quantity must be positive\"}",
        () -> api.crupdatePurchases(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidPurchase)));
  }

  @Test
  void admin_cannot_create_purchase_without_supplier() {
    PurchaseApi api = new PurchaseApi(anApiClient(ADMIN_TOKEN));

    CrupdatePurchase invalidPurchase = purchaseToCrupdatePurchase(purchase1());
    invalidPurchase.setSupplier(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Supplier is mandatory for purchase\"}",
        () -> api.crupdatePurchases(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, List.of(invalidPurchase)));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
