package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.MaterialWarehouseApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateMaterialWarehouse;
import com.example.demo.client.model.MaterialWarehouseView;
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
@ContextConfiguration(initializers = MaterialWarehouseIT.ContextInitializer.class)
class MaterialWarehouseIT {
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
  void warehouse_worker_can_get_all_material_warehouses() throws Exception {
    ApiClient client = anApiClient(WAREHOUSE_TOKEN);
    MaterialWarehouseApi api = new MaterialWarehouseApi(client);

    List<MaterialWarehouseView> records =
        api.getMaterialWarehouses(COMPANY1_ID, 1, 100, null, null, null);

    assertEquals(4, records.size());
  }

  @Test
  void user_with_bad_token_cannot_get_material_warehouses() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    MaterialWarehouseApi api = new MaterialWarehouseApi(badClient);

    assertThrowsNotAuthorizedException(
        () -> api.getMaterialWarehouses(COMPANY1_ID, 1, 100, null, null, null));
  }

  @Test
  void employee_cannot_get_material_warehouses() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    MaterialWarehouseApi api = new MaterialWarehouseApi(employeeClient);

    assertThrowsForbiddenException(
        () -> api.getMaterialWarehouses(COMPANY1_ID, 1, 100, null, null, null));
  }

  @Test
  void can_filter_material_warehouses_by_material_id() throws Exception {
    ApiClient client = anApiClient(WAREHOUSE_TOKEN);
    MaterialWarehouseApi api = new MaterialWarehouseApi(client);

    List<MaterialWarehouseView> records =
        api.getMaterialWarehouses(COMPANY1_ID, 1, 100, MATERIAL1_ID, null, null);

    assertEquals(2, records.size());
    assertTrue(records.stream().allMatch(r -> MATERIAL1_ID.equals(r.getMaterial().getId())));
  }

  @Test
  void can_filter_material_warehouses_by_warehouse_id() throws Exception {
    ApiClient client = anApiClient(ADMIN_TOKEN);
    MaterialWarehouseApi api = new MaterialWarehouseApi(client);

    List<MaterialWarehouseView> records =
        api.getMaterialWarehouses(COMPANY1_ID, 1, 100, null, WAREHOUSE1_ID, null);

    assertEquals(1, records.size());
    assertEquals(WAREHOUSE1_ID, records.get(0).getWarehouse().getId());
  }

  @Test
  void can_filter_material_warehouses_by_not_arrived() throws Exception {
    ApiClient client = anApiClient(ADMIN_TOKEN);
    MaterialWarehouseApi api = new MaterialWarehouseApi(client);

    List<MaterialWarehouseView> records =
        api.getMaterialWarehouses(COMPANY1_ID, 1, 100, null, null, true);

    assertEquals(2, records.size());
    assertTrue(records.stream().allMatch(r -> r.getQuantity() > 0));
  }

  @Test
  void can_filter_material_warehouses_with_combined_filters() throws Exception {
    ApiClient client = anApiClient(ADMIN_TOKEN);
    MaterialWarehouseApi api = new MaterialWarehouseApi(client);

    List<MaterialWarehouseView> records =
        api.getMaterialWarehouses(COMPANY1_ID, 1, 100, MATERIAL1_ID, null, true);

    assertEquals(1, records.size());
    assertEquals(MATERIAL1_ID, records.get(0).getMaterial().getId());
  }

  @Test
  @DirtiesContext
  void warehouse_worker_can_crupdate_material_warehouses() throws Exception {
    ApiClient client = anApiClient(WAREHOUSE_TOKEN);
    MaterialWarehouseApi api = new MaterialWarehouseApi(client);

    CrupdateMaterialWarehouse mw = new CrupdateMaterialWarehouse();
    mw.setMaterialId(MATERIAL1_ID);
    mw.setWarehouseId(WAREHOUSE2_ID);
    mw.setQuantity(200);

    var saved = api.crupdateMaterialWarehouses(COMPANY1_ID, List.of(mw));

    assertEquals(1, saved.size());
    assertEquals(WAREHOUSE2_ID, saved.get(0).getWarehouse().getId());
    assertEquals(200, saved.get(0).getQuantity());

    List<MaterialWarehouseView> all =
        api.getMaterialWarehouses(COMPANY1_ID, 1, 100, null, null, null);
    assertEquals(5, all.size());
  }

  @Test
  void employee_cannot_crupdate_material_warehouses() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    MaterialWarehouseApi api = new MaterialWarehouseApi(employeeClient);

    CrupdateMaterialWarehouse mw = new CrupdateMaterialWarehouse();
    mw.setMaterialId(MATERIAL1_ID);
    mw.setWarehouseId(WAREHOUSE2_ID);
    mw.setQuantity(50);

    assertThrowsForbiddenException(() -> api.crupdateMaterialWarehouses(COMPANY1_ID, List.of(mw)));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
