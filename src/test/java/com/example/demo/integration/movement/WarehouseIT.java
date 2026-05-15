package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.WarehouseApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateWarehouse;
import com.example.demo.client.model.Warehouse;
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
@ContextConfiguration(initializers = WarehouseIT.ContextInitializer.class)
class WarehouseIT {
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
  void warehouse_worker_can_get_warehouse_by_id() throws Exception {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    WarehouseApi api = new WarehouseApi(warehouseClient);

    Warehouse actual = api.getWarehouseById(COMPANY1_ID, WAREHOUSE1_ID);
    Warehouse expected = warehouse1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_warehouse_by_id() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    WarehouseApi api = new WarehouseApi(badClient);

    assertThrowsNotAuthorizedException(() -> api.getWarehouseById(COMPANY1_ID, WAREHOUSE1_ID));
  }

  @Test
  void admin_can_get_all_warehouses() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    WarehouseApi api = new WarehouseApi(adminClient);

    List<Warehouse> warehouses = api.getWarehouses(COMPANY1_ID, 1, 100, null, null, null);

    assertEquals(5, warehouses.size());
    assertTrue(warehouses.stream().anyMatch(warehouse -> WAREHOUSE1_ID.equals(warehouse.getId())));
    assertTrue(warehouses.stream().anyMatch(warehouse -> WAREHOUSE2_ID.equals(warehouse.getId())));
    assertTrue(
        warehouses.stream().anyMatch(warehouse -> ROUTE_WAREHOUSE_ID.equals(warehouse.getId())));
    assertTrue(
        warehouses.stream()
            .anyMatch(warehouse -> AT_SELLER_WAREHOUSE_ID.equals(warehouse.getId())));
    assertTrue(
        warehouses.stream()
            .anyMatch(warehouse -> UNFINDABLE_WAREHOUSE_ID.equals(warehouse.getId())));
  }

  @Test
  void employee_cannot_get_all_warehouses() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    WarehouseApi api = new WarehouseApi(employeeClient);

    assertThrowsForbiddenException(() -> api.getWarehouses(COMPANY1_ID, 1, 100, null, null, null));
  }

  @Test
  void administration_can_filter_warehouses_by_job_id() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    WarehouseApi api = new WarehouseApi(administrationClient);

    List<Warehouse> warehouses = api.getWarehouses(COMPANY1_ID, 1, 100, JOB2_ID, null, null);

    assertEquals(1, warehouses.size());
    assertEquals(WAREHOUSE2_ID, warehouses.get(0).getId());
  }

  @Test
  void administration_can_filter_warehouses_by_name() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    WarehouseApi api = new WarehouseApi(administrationClient);

    List<Warehouse> warehouses = api.getWarehouses(COMPANY1_ID, 1, 100, null, "Nord", null);

    assertEquals(1, warehouses.size());
    assertEquals(WAREHOUSE1_ID, warehouses.get(0).getId());
  }

  @Test
  void administration_can_filter_warehouses_by_description() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    WarehouseApi api = new WarehouseApi(administrationClient);

    List<Warehouse> warehouses = api.getWarehouses(COMPANY1_ID, 1, 100, null, null, "équipements");

    assertEquals(4, warehouses.size());
    assertTrue(warehouses.stream().anyMatch(warehouse -> WAREHOUSE2_ID.equals(warehouse.getId())));
  }

  @Test
  @DirtiesContext
  void warehouse_worker_can_update_warehouses() throws Exception {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    WarehouseApi api = new WarehouseApi(warehouseClient);

    CrupdateWarehouse warehouseToUpdate = warehouseToCrupdateWarehouse(warehouse1());
    warehouseToUpdate.setDescription("Stockage materiaux lourds mis a jour");

    List<Warehouse> updatedWarehouses =
        api.crupdateWarehouses(COMPANY1_ID, List.of(warehouseToUpdate));
    Warehouse updatedWarehouse = updatedWarehouses.get(0);

    assertEquals(1, updatedWarehouses.size());
    assertEquals(WAREHOUSE1_ID, updatedWarehouse.getId());
    assertEquals("Stockage materiaux lourds mis a jour", updatedWarehouse.getDescription());
    assertEquals(warehouse1().getName(), updatedWarehouse.getName());
  }

  @Test
  void employee_cannot_create_warehouses() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    WarehouseApi api = new WarehouseApi(employeeClient);

    assertThrowsForbiddenException(
        () -> api.crupdateWarehouses(COMPANY1_ID, List.of(someCreatableWarehouse())));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_warehouse() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    WarehouseApi api = new WarehouseApi(adminClient);

    api.deleteWarehouseById(COMPANY1_ID, UNFINDABLE_WAREHOUSE_ID);

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"Warehouse with id "
            + UNFINDABLE_WAREHOUSE_ID
            + " not found\"}",
        () -> api.getWarehouseById(COMPANY1_ID, UNFINDABLE_WAREHOUSE_ID));
  }

  @Test
  void administration_cannot_delete_warehouse() {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    WarehouseApi api = new WarehouseApi(administrationClient);

    assertThrowsForbiddenException(() -> api.deleteWarehouseById(COMPANY1_ID, WAREHOUSE1_ID));
  }

  @Test
  void admin_cannot_create_warehouse_without_name() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    WarehouseApi api = new WarehouseApi(adminClient);

    CrupdateWarehouse invalidWarehouse = someCreatableWarehouse();
    invalidWarehouse.setName(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Warehouse name is mandatory\"}",
        () -> api.crupdateWarehouses(COMPANY1_ID, List.of(invalidWarehouse)));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
