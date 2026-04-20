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

    Purchase actual =
        api.getPurchaseById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, PURCHASE1_ID);

    assertEquals(purchase1(), actual);
  }

  @Test
  void user_with_bad_token_cannot_get_purchase_by_id() {
    PurchaseApi api = new PurchaseApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(
        () -> api.getPurchaseById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, PURCHASE1_ID));
  }

  @Test
  void admin_can_get_all_purchases() throws Exception {
    PurchaseApi api = new PurchaseApi(anApiClient(ADMIN_TOKEN));

    List<Purchase> purchases =
        api.getPurchases(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, 1, 100);

    assertEquals(2, purchases.size());
    assertTrue(purchases.stream().anyMatch(purchase -> PURCHASE1_ID.equals(purchase.getId())));
    assertTrue(purchases.stream().anyMatch(purchase -> PURCHASE2_ID.equals(purchase.getId())));
  }

  @Test
  @DirtiesContext
  void warehouse_worker_can_update_purchases() throws Exception {
    PurchaseApi api = new PurchaseApi(anApiClient(WAREHOUSE_TOKEN));

    CrupdatePurchase purchaseToUpdate = purchaseToCrupdatePurchase(purchase1());
    purchaseToUpdate.setQuantity(5);

    List<Purchase> updatedPurchases =
        api.crupdatePurchases(
            COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, List.of(purchaseToUpdate));

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
                COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, List.of(someCreatablePurchase())));
  }

  @Test
  void administration_cannot_delete_purchase() {
    PurchaseApi api = new PurchaseApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(
        () -> api.deletePurchaseById(COMPANY1_ID, JOB1_ID, EMPLOYEE_ID, EXPENSE1_ID, PURCHASE1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
