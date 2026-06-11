package com.example.demo.integration.money;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.OrganizationApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateOrganization;
import com.example.demo.client.model.Organization;
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
@ContextConfiguration(initializers = OrganizationIT.ContextInitializer.class)
class OrganizationIT {
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

  private static Organization expectedOrganization1() {
    Organization org = organization1();
    org.setCreatedAt(null);
    org.setUpdatedAt(null);
    org.setCreatedBy(null);
    org.setUpdatedBy(null);
    org.setComment(null);
    return org;
  }

  @Test
  void admin_can_get_organization_by_id() throws Exception {
    OrganizationApi api = new OrganizationApi(anApiClient(ADMIN_TOKEN));
    Organization actual = api.getOrganizationById(ADMIN_ID, COMPANY1_ID, ORGANIZATION1_ID);
    actual.setCreatedAt(null);
    actual.setUpdatedAt(null);
    actual.setCreatedBy(null);
    actual.setUpdatedBy(null);
    assertEquals(expectedOrganization1(), actual);
  }

  @Test
  void user_with_bad_token_cannot_get_organization() {
    OrganizationApi api = new OrganizationApi(anApiClient(BAD_TOKEN));
    assertThrowsNotAuthorizedException(
        () -> api.getOrganizationById(ADMIN_ID, COMPANY1_ID, ORGANIZATION1_ID));
  }

  @Test
  void admin_can_get_all_organizations() throws Exception {
    OrganizationApi api = new OrganizationApi(anApiClient(ADMIN_TOKEN));
    List<Organization> organizations = api.getOrganizations(ADMIN_ID, COMPANY1_ID);
    assertEquals(2, organizations.size());
    assertTrue(organizations.stream().anyMatch(o -> ORGANIZATION1_ID.equals(o.getId())));
    assertTrue(organizations.stream().anyMatch(o -> ORGANIZATION2_ID.equals(o.getId())));
  }

  @Test
  void employee_cannot_get_organizations() {
    OrganizationApi api = new OrganizationApi(anApiClient(EMPLOYEE_TOKEN));
    assertThrowsForbiddenException(() -> api.getOrganizations(EMPLOYEE_ID, COMPANY1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_create_organization() throws Exception {
    OrganizationApi api = new OrganizationApi(anApiClient(ADMIN_TOKEN));
    CrupdateOrganization toCreate = someCreatableOrganization();
    List<Organization> created =
        api.crupdateOrganizations(ADMIN_ID, COMPANY1_ID, List.of(toCreate));
    assertEquals(1, created.size());
    assertEquals(toCreate.getName(), created.get(0).getName());
  }

  @Test
  @DirtiesContext
  void admin_can_update_organization() throws Exception {
    OrganizationApi api = new OrganizationApi(anApiClient(ADMIN_TOKEN));
    CrupdateOrganization toUpdate = organizationToCrupdateOrganization(organization1());
    toUpdate.setName("BNI Madagascar Modifié");
    List<Organization> updated =
        api.crupdateOrganizations(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));
    assertEquals(1, updated.size());
    assertEquals(ORGANIZATION1_ID, updated.get(0).getId());
    assertEquals("BNI Madagascar Modifié", updated.get(0).getName());
  }

  @Test
  void employee_cannot_create_organization() {
    OrganizationApi api = new OrganizationApi(anApiClient(EMPLOYEE_TOKEN));
    assertThrowsForbiddenException(
        () ->
            api.crupdateOrganizations(
                EMPLOYEE_ID, COMPANY1_ID, List.of(someCreatableOrganization())));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_organization() throws Exception {
    OrganizationApi api = new OrganizationApi(anApiClient(ADMIN_TOKEN));
    CrupdateOrganization toDelete = someCreatableOrganization();
    api.crupdateOrganizations(ADMIN_ID, COMPANY1_ID, List.of(toDelete));
    api.deleteOrganizationById(ADMIN_ID, COMPANY1_ID, toDelete.getId());
    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"Organization with id "
            + toDelete.getId()
            + " not found\"}",
        () -> api.getOrganizationById(ADMIN_ID, COMPANY1_ID, toDelete.getId()));
  }

  @Test
  void administration_cannot_delete_organization() {
    OrganizationApi api = new OrganizationApi(anApiClient(ADMINISTRATION_TOKEN));
    assertThrowsForbiddenException(
        () -> api.deleteOrganizationById(ADMIN_ID, COMPANY1_ID, ORGANIZATION1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
