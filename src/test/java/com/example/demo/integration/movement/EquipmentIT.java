package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.EquipmentApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateEquipment;
import com.example.demo.client.model.Equipment;
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
@ContextConfiguration(initializers = EquipmentIT.ContextInitializer.class)
class EquipmentIT {
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
  void employee_can_get_equipment_by_id() throws Exception {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    EquipmentApi api = new EquipmentApi(employeeClient);

    Equipment actual = api.getEquipmentById(COMPANY1_ID, EQUIPMENT1_ID);
    Equipment expected = equipment1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_equipment_by_id() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    EquipmentApi api = new EquipmentApi(badClient);

    assertThrowsNotAuthorizedException(() -> api.getEquipmentById(COMPANY1_ID, EQUIPMENT1_ID));
  }

  @Test
  void employee_can_get_all_equipment() throws Exception {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    EquipmentApi api = new EquipmentApi(employeeClient);

    List<Equipment> equipment =
        api.getEquipment(COMPANY1_ID, 1, 100, null, null, null, null, null, null);

    assertEquals(5, equipment.size());
    assertTrue(equipment.stream().anyMatch(item -> EQUIPMENT1_ID.equals(item.getId())));
    assertTrue(equipment.stream().anyMatch(item -> EQUIPMENT2_ID.equals(item.getId())));
    assertTrue(equipment.stream().anyMatch(item -> EQUIPMENT3_ID.equals(item.getId())));
    assertTrue(equipment.stream().anyMatch(item -> EQUIPMENT4_ID.equals(item.getId())));
    assertTrue(equipment.stream().anyMatch(item -> EQUIPMENT5_ID.equals(item.getId())));
  }

  @Test
  void employee_can_filter_equipment_by_warehouse_id() throws Exception {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    EquipmentApi api = new EquipmentApi(employeeClient);

    List<Equipment> equipment =
        api.getEquipment(COMPANY1_ID, 1, 100, WAREHOUSE2_ID, null, null, null, null, null);

    assertEquals(1, equipment.size());
    assertEquals(EQUIPMENT3_ID, equipment.get(0).getId());
  }

  @Test
  void employee_can_filter_equipment_by_name() throws Exception {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    EquipmentApi api = new EquipmentApi(employeeClient);

    List<Equipment> equipment =
        api.getEquipment(COMPANY1_ID, 1, 100, null, "Bétonnière", null, null, null, null);

    assertEquals(1, equipment.size());
    assertEquals(EQUIPMENT2_ID, equipment.get(0).getId());
  }

  @Test
  void employee_can_filter_equipment_by_description() throws Exception {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    EquipmentApi api = new EquipmentApi(employeeClient);

    List<Equipment> equipment =
        api.getEquipment(COMPANY1_ID, 1, 100, null, null, "extérieure", null, null, null);

    assertEquals(1, equipment.size());
    assertEquals(EQUIPMENT3_ID, equipment.get(0).getId());
  }

  @Test
  void employee_can_filter_equipment_by_floor_number() throws Exception {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    EquipmentApi api = new EquipmentApi(employeeClient);

    List<Equipment> equipment =
        api.getEquipment(COMPANY1_ID, 1, 100, null, null, null, 2, null, null);

    assertEquals(1, equipment.size());
    assertEquals(EQUIPMENT3_ID, equipment.get(0).getId());
  }

  @Test
  void employee_can_filter_equipment_by_storage_number() throws Exception {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    EquipmentApi api = new EquipmentApi(employeeClient);

    List<Equipment> equipment =
        api.getEquipment(COMPANY1_ID, 1, 100, null, null, null, null, 10, null);

    assertEquals(1, equipment.size());
    assertEquals(EQUIPMENT1_ID, equipment.get(0).getId());
  }

  @Test
  @DirtiesContext
  void administration_can_update_equipment() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    EquipmentApi api = new EquipmentApi(administrationClient);

    CrupdateEquipment equipmentToUpdate = equipmentToCrupdateEquipment(equipment1());
    equipmentToUpdate.setDescription("Pelle Caterpillar 320 revisee");

    List<Equipment> updatedEquipment =
        api.crupdateEquipment(COMPANY1_ID, List.of(equipmentToUpdate));
    Equipment updated = updatedEquipment.get(0);

    assertEquals(1, updatedEquipment.size());
    assertEquals(EQUIPMENT1_ID, updated.getId());
    assertEquals("Pelle Caterpillar 320 revisee", updated.getDescription());
    assertEquals(equipment1().getName(), updated.getName());
  }

  @Test
  void employee_cannot_create_equipment() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    EquipmentApi api = new EquipmentApi(employeeClient);

    assertThrowsForbiddenException(
        () -> api.crupdateEquipment(COMPANY1_ID, List.of(someCreatableEquipment())));
  }

  @Test
  void warehouse_worker_cannot_delete_equipment() {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    EquipmentApi api = new EquipmentApi(warehouseClient);

    assertThrowsForbiddenException(() -> api.deleteEquipmentById(COMPANY1_ID, EQUIPMENT1_ID));
  }

  @Test
  void admin_cannot_create_equipment_without_name() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    EquipmentApi api = new EquipmentApi(adminClient);

    CrupdateEquipment invalidEquipment = someCreatableEquipment();
    invalidEquipment.setName(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Equipment name is mandatory\"}",
        () -> api.crupdateEquipment(COMPANY1_ID, List.of(invalidEquipment)));
  }

  @Test
  void admin_cannot_create_equipment_without_warehouse() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    EquipmentApi api = new EquipmentApi(adminClient);

    CrupdateEquipment invalidEquipment = someCreatableEquipment();
    invalidEquipment.setWarehouseId(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Equipment must be associated with a warehouse\"}",
        () -> api.crupdateEquipment(COMPANY1_ID, List.of(invalidEquipment)));
  }

  @Test
  void admin_can_filter_not_arrived_equipment() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    EquipmentApi api = new EquipmentApi(adminClient);

    List<Equipment> notArrivedEquipment =
        api.getEquipment(COMPANY1_ID, 1, 100, null, null, null, null, null, true);

    assertEquals(2, notArrivedEquipment.size());
    assertTrue(notArrivedEquipment.stream().anyMatch(e -> EQUIPMENT4_ID.equals(e.getId())));
    assertTrue(notArrivedEquipment.stream().anyMatch(e -> EQUIPMENT5_ID.equals(e.getId())));
    assertFalse(notArrivedEquipment.stream().anyMatch(e -> EQUIPMENT1_ID.equals(e.getId())));
    assertFalse(notArrivedEquipment.stream().anyMatch(e -> EQUIPMENT2_ID.equals(e.getId())));
    assertFalse(notArrivedEquipment.stream().anyMatch(e -> EQUIPMENT3_ID.equals(e.getId())));
  }

  @Test
  void admin_gets_all_equipment_when_not_arrived_is_false() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    EquipmentApi api = new EquipmentApi(adminClient);

    List<Equipment> equipment =
        api.getEquipment(COMPANY1_ID, 1, 100, null, null, null, null, null, false);

    assertEquals(5, equipment.size());
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
