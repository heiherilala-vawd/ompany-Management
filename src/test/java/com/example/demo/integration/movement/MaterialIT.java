package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.MaterialApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateMaterial;
import com.example.demo.client.model.Material;
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
@ContextConfiguration(initializers = MaterialIT.ContextInitializer.class)
class MaterialIT {
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
  void warehouse_worker_can_get_material_by_id() throws Exception {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    MaterialApi api = new MaterialApi(warehouseClient);

    Material actual = api.getMaterialById(MATERIAL1_ID);
    Material expected = material1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_material_by_id() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    MaterialApi api = new MaterialApi(badClient);

    assertThrowsNotAuthorizedException(() -> api.getMaterialById(MATERIAL1_ID));
  }

  @Test
  void admin_can_get_all_materials() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    MaterialApi api = new MaterialApi(adminClient);

    List<Material> materials = api.getMaterials(1, 100, null, null, null, null, null);

    assertEquals(3, materials.size());
    assertTrue(materials.stream().anyMatch(material -> MATERIAL1_ID.equals(material.getId())));
    assertTrue(materials.stream().anyMatch(material -> MATERIAL2_ID.equals(material.getId())));
    assertTrue(materials.stream().anyMatch(material -> MATERIAL3_ID.equals(material.getId())));
  }

  @Test
  void employee_cannot_get_all_materials() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    MaterialApi api = new MaterialApi(employeeClient);

    assertThrowsForbiddenException(() -> api.getMaterials(1, 100, null, null, null, null, null));
  }

  @Test
  void administration_can_filter_materials_by_warehouse_id() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    MaterialApi api = new MaterialApi(administrationClient);

    List<Material> materials = api.getMaterials(1, 100, WAREHOUSE2_ID, null, null, null, null);

    assertEquals(1, materials.size());
    assertEquals(MATERIAL3_ID, materials.get(0).getId());
  }

  @Test
  void administration_can_filter_materials_by_name() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    MaterialApi api = new MaterialApi(administrationClient);

    List<Material> materials = api.getMaterials(1, 100, null, "Brique", null, null, null);

    assertEquals(1, materials.size());
    assertEquals(MATERIAL2_ID, materials.get(0).getId());
  }

  @Test
  void administration_can_filter_materials_by_description() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    MaterialApi api = new MaterialApi(administrationClient);

    List<Material> materials = api.getMaterials(1, 100, null, null, "blanche", null, null);

    assertEquals(1, materials.size());
    assertEquals(MATERIAL3_ID, materials.get(0).getId());
  }

  @Test
  void administration_can_filter_materials_by_floor_number() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    MaterialApi api = new MaterialApi(administrationClient);

    List<Material> materials = api.getMaterials(1, 100, null, null, null, 2, null);

    assertEquals(1, materials.size());
    assertEquals(MATERIAL3_ID, materials.get(0).getId());
  }

  @Test
  void administration_can_filter_materials_by_storage_number() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    MaterialApi api = new MaterialApi(administrationClient);

    List<Material> materials = api.getMaterials(1, 100, null, null, null, null, 100);

    assertEquals(1, materials.size());
    assertEquals(MATERIAL1_ID, materials.get(0).getId());
  }

  @Test
  @DirtiesContext
  void warehouse_worker_can_update_materials() throws Exception {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    MaterialApi api = new MaterialApi(warehouseClient);

    CrupdateMaterial materialToUpdate = materialToCrupdateMaterial(material1());
    materialToUpdate.setDescription("Ciment Portland 35kg premium");

    List<Material> updatedMaterials = api.crupdateMaterials(List.of(materialToUpdate));
    Material updated = updatedMaterials.get(0);

    assertEquals(1, updatedMaterials.size());
    assertEquals(MATERIAL1_ID, updated.getId());
    assertEquals("Ciment Portland 35kg premium", updated.getDescription());
    assertEquals(material1().getName(), updated.getName());
  }

  @Test
  void employee_cannot_create_materials() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    MaterialApi api = new MaterialApi(employeeClient);

    assertThrowsForbiddenException(() -> api.crupdateMaterials(List.of(someCreatableMaterial())));
  }

  @Test
  void administration_cannot_delete_material() {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    MaterialApi api = new MaterialApi(administrationClient);

    assertThrowsForbiddenException(() -> api.deleteMaterialById(MATERIAL1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
