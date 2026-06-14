package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.PurchaseOrderApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdatePurchaseOrder;
import com.example.demo.client.model.PaginatedResponse;
import com.example.demo.client.model.PurchaseOrder;
import com.example.demo.client.model.PurchaseOrderStatus;
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
@ContextConfiguration(initializers = PurchaseOrderIT.ContextInitializer.class)
class PurchaseOrderIT {
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

  private static PurchaseOrder expectedPo1() {
    PurchaseOrder po = purchaseOrder1();
    po.setCreatedAt(null);
    po.setUpdatedAt(null);
    po.setCreatedBy(null);
    po.setUpdatedBy(null);
    po.setComment(null);
    return po;
  }

  @Test
  void admin_can_get_purchase_order_by_id() throws Exception {
    PurchaseOrderApi api = new PurchaseOrderApi(anApiClient(ADMIN_TOKEN));
    PurchaseOrder actual = api.getPurchaseOrderById(ADMIN_ID, COMPANY1_ID, PO1_ID);
    actual.setCreatedAt(null);
    actual.setUpdatedAt(null);
    actual.setCreatedBy(null);
    actual.setUpdatedBy(null);
    assertEquals(expectedPo1(), actual);
  }

  @Test
  void user_with_bad_token_cannot_get_purchase_order() {
    PurchaseOrderApi api = new PurchaseOrderApi(anApiClient(BAD_TOKEN));
    assertThrowsNotAuthorizedException(
        () -> api.getPurchaseOrderById(ADMIN_ID, COMPANY1_ID, PO1_ID));
  }

  @Test
  void admin_can_get_all_purchase_orders() throws Exception {
    PurchaseOrderApi api = new PurchaseOrderApi(anApiClient(ADMIN_TOKEN));
    PaginatedResponse resp = api.getPurchaseOrders(ADMIN_ID, COMPANY1_ID, null);

    List<PurchaseOrder> orders = extractData(resp, PurchaseOrder.class);
    assertEquals(2, orders.size());
    assertTrue(orders.stream().anyMatch(po -> PO1_ID.equals(po.getId())));
    assertTrue(orders.stream().anyMatch(po -> PO2_ID.equals(po.getId())));
  }

  @Test
  void admin_can_filter_purchase_orders_by_job_id() throws Exception {
    PurchaseOrderApi api = new PurchaseOrderApi(anApiClient(ADMIN_TOKEN));

    PaginatedResponse resp = api.getPurchaseOrders(ADMIN_ID, COMPANY1_ID, JOB1_ID);

    List<PurchaseOrder> result = extractData(resp, PurchaseOrder.class);

    assertEquals(2, result.size());
    assertTrue(result.stream().allMatch(po -> JOB1_ID.equals(po.getJobId())));
  }

  @Test
  void employee_cannot_get_purchase_orders() {
    PurchaseOrderApi api = new PurchaseOrderApi(anApiClient(EMPLOYEE_TOKEN));
    assertThrowsForbiddenException(() -> api.getPurchaseOrders(EMPLOYEE_ID, COMPANY1_ID, null));
  }

  @Test
  @DirtiesContext
  void admin_can_create_purchase_order() throws Exception {
    PurchaseOrderApi api = new PurchaseOrderApi(anApiClient(ADMIN_TOKEN));
    CrupdatePurchaseOrder toCreate = someCreatablePurchaseOrder();
    List<PurchaseOrder> created =
        api.crupdatePurchaseOrders(ADMIN_ID, COMPANY1_ID, List.of(toCreate));
    assertEquals(1, created.size());
    assertEquals(toCreate.getTotalAmount(), created.get(0).getTotalAmount());
    assertEquals(toCreate.getSupplierId(), created.get(0).getSupplierId());
  }

  @Test
  @DirtiesContext
  void admin_can_update_purchase_order_status() throws Exception {
    PurchaseOrderApi api = new PurchaseOrderApi(anApiClient(ADMIN_TOKEN));
    CrupdatePurchaseOrder toUpdate = purchaseOrderToCrupdatePurchaseOrder(purchaseOrder2());
    toUpdate.setStatus(PurchaseOrderStatus.VALIDATED);
    List<PurchaseOrder> updated =
        api.crupdatePurchaseOrders(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));
    assertEquals(1, updated.size());
    assertEquals(PO2_ID, updated.get(0).getId());
    assertEquals(PurchaseOrderStatus.VALIDATED, updated.get(0).getStatus());
  }

  @Test
  void employee_cannot_create_purchase_order() {
    PurchaseOrderApi api = new PurchaseOrderApi(anApiClient(EMPLOYEE_TOKEN));
    assertThrowsForbiddenException(
        () ->
            api.crupdatePurchaseOrders(
                EMPLOYEE_ID, COMPANY1_ID, List.of(someCreatablePurchaseOrder())));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_purchase_order() throws Exception {
    PurchaseOrderApi api = new PurchaseOrderApi(anApiClient(ADMIN_TOKEN));
    CrupdatePurchaseOrder toDelete = someCreatablePurchaseOrder();
    api.crupdatePurchaseOrders(ADMIN_ID, COMPANY1_ID, List.of(toDelete));
    api.deletePurchaseOrderById(ADMIN_ID, COMPANY1_ID, toDelete.getId());
    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"PurchaseOrder with id "
            + toDelete.getId()
            + " not found\"}",
        () -> api.getPurchaseOrderById(ADMIN_ID, COMPANY1_ID, toDelete.getId()));
  }

  @Test
  void administration_cannot_delete_purchase_order() {
    PurchaseOrderApi api = new PurchaseOrderApi(anApiClient(ADMINISTRATION_TOKEN));
    assertThrowsForbiddenException(
        () -> api.deletePurchaseOrderById(ADMIN_ID, COMPANY1_ID, PO1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
