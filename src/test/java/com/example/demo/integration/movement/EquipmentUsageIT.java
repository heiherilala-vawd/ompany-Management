package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.EquipmentUsageApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateEquipmentUsage;
import com.example.demo.client.model.EquipmentUsage;
import com.example.demo.client.model.UsageStatus;
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
@ContextConfiguration(initializers = EquipmentUsageIT.ContextInitializer.class)
class EquipmentUsageIT {
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
  void warehouse_cannot_get_other_equipment_usage() {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(warehouseClient);

    assertThrowsApiException(
        "{\"type\":\"403 FORBIDDEN\",\"message\":\"Equipment usage not associated with the user\"}",
        () -> api.getEquipmentUsageById(WAREHOUSE_ID, COMPANY1_ID, EQUIP_USAGE1_ID));
  }

  @Test
  void user_with_bad_token_cannot_get_equipment_usage_by_id() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(badClient);

    assertThrowsNotAuthorizedException(
        () -> api.getEquipmentUsageById(ADMIN_ID, COMPANY1_ID, EQUIP_USAGE1_ID));
  }

  @Test
  void admin_can_get_all_equipment_usages() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(adminClient);

    List<EquipmentUsage> usages = api.getEquipmentUsages(ADMIN_ID, COMPANY1_ID, 1, 100, null);

    assertEquals(2, usages.size());
    assertTrue(usages.stream().anyMatch(eu -> EQUIP_USAGE1_ID.equals(eu.getId())));
    assertTrue(usages.stream().anyMatch(eu -> EQUIP_USAGE2_ID.equals(eu.getId())));
  }

  @Test
  void admin_can_filter_equipment_usages_by_job_id() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(adminClient);

    List<EquipmentUsage> result = api.getEquipmentUsages(ADMIN_ID, COMPANY1_ID, 1, 100, JOB1_ID);

    assertEquals(2, result.size());
    assertTrue(result.stream().allMatch(eu -> JOB1_ID.equals(eu.getJobId())));
  }

  @Test
  void employee_can_list_own_equipment_usages() throws Exception {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(employeeClient);

    List<EquipmentUsage> usages =
        api.getEquipmentUsages(EMPLOYEE_ID, COMPANY1_ID, 1, 100, null);

    assertEquals(0, usages.size());
  }

  @Test
  void employee_cannot_get_other_equipment_usage() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(employeeClient);

    assertThrowsApiException(
        "{\"type\":\"403 FORBIDDEN\",\"message\":\"Equipment usage not associated with the user\"}",
        () -> api.getEquipmentUsageById(EMPLOYEE_ID, COMPANY1_ID, EQUIP_USAGE1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_update_equipment_usage() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(adminClient);

    CrupdateEquipmentUsage toUpdate = equipmentUsageToCrupdateEquipmentUsage(equipmentUsage1());
    toUpdate.setComment("Mis \u00e0 jour");

    List<EquipmentUsage> updated =
        api.crupdateEquipmentUsages(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));
    EquipmentUsage result = updated.get(0);

    assertEquals(1, updated.size());
    assertEquals(EQUIP_USAGE1_ID, result.getId());
    assertEquals("Mis \u00e0 jour", result.getComment());
  }

  @Test
  @DirtiesContext
  void admin_can_create_equipment_usage() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(adminClient);

    CrupdateEquipmentUsage creatable = someCreatableEquipmentUsage();

    List<EquipmentUsage> created =
        api.crupdateEquipmentUsages(ADMIN_ID, COMPANY1_ID, List.of(creatable));

    assertEquals(1, created.size());
    EquipmentUsage result = created.get(0);
    assertEquals(creatable.getId(), result.getId());
    assertEquals(UsageStatus.IN_USE, result.getUsageStatus());
  }

  @Test
  @DirtiesContext
  void admin_can_delete_equipment_usage() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(adminClient);

    CrupdateEquipmentUsage toDelete = someCreatableEquipmentUsage();
    api.crupdateEquipmentUsages(ADMIN_ID, COMPANY1_ID, List.of(toDelete));

    api.deleteEquipmentUsageById(ADMIN_ID, COMPANY1_ID, toDelete.getId());

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"EquipmentUsage with id "
            + toDelete.getId()
            + " not found\"}",
        () -> api.getEquipmentUsageById(ADMIN_ID, COMPANY1_ID, toDelete.getId()));
  }

  @Test
  void employee_cannot_create_equipment_usage() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(employeeClient);

    assertThrowsForbiddenException(
        () ->
            api.crupdateEquipmentUsages(
                ADMIN_ID, COMPANY1_ID, List.of(someCreatableEquipmentUsage())));
  }

  @Test
  @DirtiesContext
  void admin_can_return_equipment_with_returned_status() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(adminClient);

    CrupdateEquipmentUsage creatable = someCreatableEquipmentUsage();
    creatable.setUsageStatus("IN_USE");
    List<EquipmentUsage> created =
        api.crupdateEquipmentUsages(ADMIN_ID, COMPANY1_ID, List.of(creatable));
    String newId = created.get(0).getId();

    HttpResponse<String> response =
        authenticatedPut(
            ADMIN_TOKEN,
            "/users/"
                + ADMIN_ID
                + "/companies/"
                + COMPANY1_ID
                + "/equipment_usages/"
                + newId
                + "/return?status=RETURNED");

    assertEquals(HttpStatus.OK.value(), response.statusCode());
  }

  @Test
  @DirtiesContext
  void admin_can_return_equipment_with_lost_status() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(adminClient);

    CrupdateEquipmentUsage creatable = someCreatableEquipmentUsage();
    creatable.setUsageStatus("IN_USE");
    List<EquipmentUsage> created =
        api.crupdateEquipmentUsages(ADMIN_ID, COMPANY1_ID, List.of(creatable));
    String newId = created.get(0).getId();

    HttpResponse<String> response =
        authenticatedPut(
            ADMIN_TOKEN,
            "/users/"
                + ADMIN_ID
                + "/companies/"
                + COMPANY1_ID
                + "/equipment_usages/"
                + newId
                + "/return?status=LOST");

    assertEquals(HttpStatus.OK.value(), response.statusCode());
  }

  @Test
  @DirtiesContext
  void admin_can_return_equipment_with_broken_status() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(adminClient);

    CrupdateEquipmentUsage creatable = someCreatableEquipmentUsage();
    creatable.setUsageStatus("IN_USE");
    List<EquipmentUsage> created =
        api.crupdateEquipmentUsages(ADMIN_ID, COMPANY1_ID, List.of(creatable));
    String newId = created.get(0).getId();

    HttpResponse<String> response =
        authenticatedPut(
            ADMIN_TOKEN,
            "/users/"
                + ADMIN_ID
                + "/companies/"
                + COMPANY1_ID
                + "/equipment_usages/"
                + newId
                + "/return?status=BROKEN");

    assertEquals(HttpStatus.OK.value(), response.statusCode());
  }

  @Test
  void employee_cannot_return_equipment() throws Exception {
    HttpResponse<String> response =
        authenticatedPut(
            EMPLOYEE_TOKEN,
            "/users/"
                + ADMIN_ID
                + "/companies/"
                + COMPANY1_ID
                + "/equipment_usages/"
                + EQUIP_USAGE1_ID
                + "/return?status=RETURNED");

    assertEquals(HttpStatus.FORBIDDEN.value(), response.statusCode());
  }

  @Test
  void admin_cannot_create_usage_without_equipment() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    EquipmentUsageApi api = new EquipmentUsageApi(adminClient);

    CrupdateEquipmentUsage invalid = someCreatableEquipmentUsage();
    invalid.setEquipmentId(null);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Equipment is mandatory\"}",
        () -> api.crupdateEquipmentUsages(ADMIN_ID, COMPANY1_ID, List.of(invalid)));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
