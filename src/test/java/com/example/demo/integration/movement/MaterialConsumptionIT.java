package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.MaterialConsumptionApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateMaterialConsumption;
import com.example.demo.client.model.MaterialConsumption;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestDataSqlLoader;
import com.example.demo.integration.conf.TestUtils;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = MaterialConsumptionIT.ContextInitializer.class)
class MaterialConsumptionIT {
  @Autowired private DataSource dataSource;

  @MockitoBean private SentryConf sentryConf;
  @MockitoBean private AuthenticationManager authenticationManagerMock;
  @MockitoBean private JwtUtils jwtServiceMock;
  private final HttpClient httpClient = HttpClient.newHttpClient();

  private static ApiClient anApiClient(String token) {
    return TestUtils.anApiClient(token, ContextInitializer.SERVER_PORT);
  }

  @BeforeEach
  void setUp() throws Exception {
    TestUtils.setUpJwtService(jwtServiceMock);
    TestUtils.setUpAuthenticationManager(authenticationManagerMock);
    TestDataSqlLoader.executeAllSqlScripts(dataSource);
  }

  private String baseUri() {
    return "http://localhost:" + ContextInitializer.SERVER_PORT;
  }

  private HttpResponse<String> authenticatedPut(String token, String path) throws Exception {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(baseUri() + path))
            .header("Authorization", "Bearer " + token)
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.noBody())
            .build();
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  @Test
  void warehouse_worker_can_get_material_consumption_by_id() throws Exception {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(warehouseClient);

    MaterialConsumption actual =
        api.getMaterialConsumptionById(ADMIN_ID, COMPANY1_ID, MAT_CONSUMPTION1_ID);
    MaterialConsumption expected = materialConsumption1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_material_consumption_by_id() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(badClient);

    assertThrowsNotAuthorizedException(
        () -> api.getMaterialConsumptionById(ADMIN_ID, COMPANY1_ID, MAT_CONSUMPTION1_ID));
  }

  @Test
  void admin_can_get_all_material_consumptions() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(adminClient);

    List<MaterialConsumption> consumptions =
        api.getMaterialConsumptions(ADMIN_ID, COMPANY1_ID, 1, 100, null, null);

    assertEquals(2, consumptions.size());
    assertTrue(consumptions.stream().anyMatch(mc -> MAT_CONSUMPTION1_ID.equals(mc.getId())));
    assertTrue(consumptions.stream().anyMatch(mc -> MAT_CONSUMPTION2_ID.equals(mc.getId())));
  }

  @Test
  void admin_can_filter_material_consumptions_by_consumption_status() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(adminClient);

    List<MaterialConsumption> completed =
        api.getMaterialConsumptions(ADMIN_ID, COMPANY1_ID, 1, 100, "COMPLETED", null);

    assertEquals(2, completed.size());
    assertTrue(completed.stream().allMatch(mc -> "COMPLETED".equals(mc.getConsumptionStatus())));

    List<MaterialConsumption> inProgress =
        api.getMaterialConsumptions(ADMIN_ID, COMPANY1_ID, 1, 100, "IN_PROGRESS", null);

    assertTrue(inProgress.isEmpty());
  }

  @Test
  void admin_can_filter_material_consumptions_by_job_id() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(adminClient);

    List<MaterialConsumption> result =
        api.getMaterialConsumptions(ADMIN_ID, COMPANY1_ID, 1, 100, null, JOB1_ID);

    assertEquals(2, result.size());
    assertTrue(result.stream().allMatch(mc -> JOB1_ID.equals(mc.getJobId())));
  }

  @Test
  void employee_cannot_get_material_consumptions() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(employeeClient);

    assertThrowsForbiddenException(
        () -> api.getMaterialConsumptions(ADMIN_ID, COMPANY1_ID, 1, 100, null, null));
  }

  @Test
  @DirtiesContext
  void admin_can_update_material_consumption() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(adminClient);

    CrupdateMaterialConsumption toUpdate =
        materialConsumptionToCrupdateMaterialConsumption(materialConsumption1());
    toUpdate.setReason("Reason modifi\u00e9e");

    List<MaterialConsumption> updated =
        api.crupdateMaterialConsumptions(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));
    MaterialConsumption result = updated.get(0);

    assertEquals(1, updated.size());
    assertEquals(MAT_CONSUMPTION1_ID, result.getId());
    assertEquals("Reason modifi\u00e9e", result.getReason());
  }

  @Test
  @DirtiesContext
  void admin_can_create_material_consumption() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(adminClient);

    CrupdateMaterialConsumption creatable = someCreatableMaterialConsumption();

    List<MaterialConsumption> created =
        api.crupdateMaterialConsumptions(ADMIN_ID, COMPANY1_ID, List.of(creatable));

    assertEquals(1, created.size());
    MaterialConsumption result = created.get(0);
    assertEquals(creatable.getId(), result.getId());
    assertEquals("IN_PROGRESS", result.getConsumptionStatus());
  }

  @Test
  @DirtiesContext
  void admin_can_delete_material_consumption() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(adminClient);

    CrupdateMaterialConsumption toDelete = someCreatableMaterialConsumption();
    api.crupdateMaterialConsumptions(ADMIN_ID, COMPANY1_ID, List.of(toDelete));

    api.deleteMaterialConsumptionById(ADMIN_ID, COMPANY1_ID, toDelete.getId());

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"MaterialConsumption with id "
            + toDelete.getId()
            + " not found\"}",
        () -> api.getMaterialConsumptionById(ADMIN_ID, COMPANY1_ID, toDelete.getId()));
  }

  @Test
  void employee_cannot_create_material_consumption() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(employeeClient);

    assertThrowsForbiddenException(
        () ->
            api.crupdateMaterialConsumptions(
                ADMIN_ID, COMPANY1_ID, List.of(someCreatableMaterialConsumption())));
  }

  @Test
  @DirtiesContext
  void admin_can_complete_material_consumption() throws Exception {
    HttpResponse<String> response =
        authenticatedPut(
            ADMIN_TOKEN,
            "/companies/"
                + COMPANY1_ID
                + "/material_consumption/"
                + MAT_CONSUMPTION1_ID
                + "/complete");

    assertEquals(HttpStatus.OK.value(), response.statusCode());
  }

  @Test
  @DirtiesContext
  void admin_can_complete_in_progress_consumption() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(adminClient);

    CrupdateMaterialConsumption creatable = someCreatableMaterialConsumption();
    creatable.setConsumptionStatus("IN_PROGRESS");
    List<MaterialConsumption> created =
        api.crupdateMaterialConsumptions(ADMIN_ID, COMPANY1_ID, List.of(creatable));
    String newId = created.get(0).getId();

    HttpResponse<String> completeResponse =
        authenticatedPut(
            ADMIN_TOKEN,
            "/companies/" + COMPANY1_ID + "/material_consumption/" + newId + "/complete");

    assertEquals(HttpStatus.OK.value(), completeResponse.statusCode());
  }

  @Test
  @DirtiesContext
  void admin_can_return_materials_from_consumption() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(adminClient);

    CrupdateMaterialConsumption creatable = someCreatableMaterialConsumption();
    creatable.setConsumptionStatus("IN_PROGRESS");
    creatable.setQuantity(10);
    List<MaterialConsumption> created =
        api.crupdateMaterialConsumptions(ADMIN_ID, COMPANY1_ID, List.of(creatable));
    String newId = created.get(0).getId();

    authenticatedPut(
        ADMIN_TOKEN, "/companies/" + COMPANY1_ID + "/material_consumption/" + newId + "/complete");

    HttpResponse<String> returnResponse =
        authenticatedPut(
            ADMIN_TOKEN,
            "/companies/" + COMPANY1_ID + "/material_consumption/" + newId + "/return?quantity=3");

    assertEquals(HttpStatus.OK.value(), returnResponse.statusCode());
  }

  @Test
  void employee_cannot_complete_material_consumption() throws Exception {
    HttpResponse<String> response =
        authenticatedPut(
            EMPLOYEE_TOKEN,
            "/companies/"
                + COMPANY1_ID
                + "/material_consumption/"
                + MAT_CONSUMPTION1_ID
                + "/complete");

    assertEquals(HttpStatus.FORBIDDEN.value(), response.statusCode());
  }

  @Test
  void employee_cannot_return_materials() throws Exception {
    HttpResponse<String> response =
        authenticatedPut(
            EMPLOYEE_TOKEN,
            "/companies/"
                + COMPANY1_ID
                + "/material_consumption/"
                + MAT_CONSUMPTION1_ID
                + "/return?quantity=1");

    assertEquals(HttpStatus.FORBIDDEN.value(), response.statusCode());
  }

  @Test
  void admin_cannot_create_consumption_without_material() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(adminClient);

    CrupdateMaterialConsumption invalid = someCreatableMaterialConsumption();
    invalid.setMaterialId(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Material is mandatory\"}",
        () -> api.crupdateMaterialConsumptions(ADMIN_ID, COMPANY1_ID, List.of(invalid)));
  }

  @Test
  void admin_cannot_create_consumption_without_quantity() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    MaterialConsumptionApi api = new MaterialConsumptionApi(adminClient);

    CrupdateMaterialConsumption invalid = someCreatableMaterialConsumption();
    invalid.setQuantity(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Quantity is mandatory\"}",
        () -> api.crupdateMaterialConsumptions(ADMIN_ID, COMPANY1_ID, List.of(invalid)));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
