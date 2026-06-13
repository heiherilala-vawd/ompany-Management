package com.example.demo.integration;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.JobApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateJob;
import com.example.demo.client.model.Job;
import com.example.demo.client.model.JobStatus;
import com.example.demo.client.model.User;
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
@ContextConfiguration(initializers = JobIT.ContextInitializer.class)
class JobIT {
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
  void employee_can_get_job_by_id() throws Exception {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    JobApi api = new JobApi(employeeClient);

    Job actual = api.getJobById(EMPLOYEE_ID, COMPANY1_ID, JOB1_ID);
    Job expected = job1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_job_by_id() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    JobApi api = new JobApi(badClient);

    assertThrowsNotAuthorizedException(() -> api.getJobById(ADMIN_ID, COMPANY1_ID, JOB1_ID));
  }

  @Test
  void admin_can_get_all_jobs() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi api = new JobApi(adminClient);

    List<Job> jobs = api.getJobs(ADMIN_ID, COMPANY1_ID, 1, 100, null, null);

    assertEquals(1, jobs.size());
    assertEquals(JOB1_ID, jobs.get(0).getId());
  }

  @Test
  void employee_cannot_get_all_jobs() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    JobApi api = new JobApi(employeeClient);

    assertThrowsForbiddenException(() -> api.getJobs(EMPLOYEE_ID, COMPANY1_ID, 1, 100, null, null));
  }

  @Test
  void warehouse_can_filter_jobs_by_status() throws Exception {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    JobApi api = new JobApi(warehouseClient);

    List<Job> jobs = api.getJobs(WAREHOUSE_ID, COMPANY1_ID, 1, 100, JobStatus.IN_PROGRESS, null);

    assertEquals(1, jobs.size());
    assertEquals(JOB1_ID, jobs.get(0).getId());
  }

  @Test
  void administration_can_filter_jobs_by_company_id() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    JobApi api = new JobApi(administrationClient);

    List<Job> jobs = api.getJobs(ADMIN_ID, COMPANY2_ID, 1, 100, null, null);

    assertEquals(1, jobs.size());
    assertEquals(JOB2_ID, jobs.get(0).getId());
  }

  @Test
  void administration_can_filter_jobs_by_description() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    JobApi api = new JobApi(administrationClient);

    List<Job> jobs = api.getJobs(ADMIN_ID, COMPANY1_ID, 1, 100, null, "bâtiment A");

    assertEquals(1, jobs.size());
    assertEquals(JOB1_ID, jobs.get(0).getId());
  }

  @Test
  @DirtiesContext
  void administration_can_update_jobs() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    JobApi api = new JobApi(administrationClient);

    CrupdateJob jobToUpdate = jobToCrupdateJob(job1());
    jobToUpdate.setDescription("Construction du batiment A mise a jour");

    List<Job> updatedJobs = api.crupdateJobs(ADMIN_ID, COMPANY1_ID, List.of(jobToUpdate));
    Job updatedJob = updatedJobs.get(0);

    assertEquals(1, updatedJobs.size());
    assertEquals(JOB1_ID, updatedJob.getId());
    assertEquals("Construction du batiment A mise a jour", updatedJob.getDescription());
    assertEquals(job1().getStatus(), updatedJob.getStatus());
  }

  @Test
  void warehouse_cannot_create_jobs() {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    JobApi api = new JobApi(warehouseClient);

    assertThrowsForbiddenException(
        () -> api.crupdateJobs(WAREHOUSE_ID, COMPANY1_ID, List.of(someCreatableJob())));
  }

  @Test
  void administration_cannot_delete_job() {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    JobApi api = new JobApi(administrationClient);

    assertThrowsForbiddenException(() -> api.deleteJobById(ADMIN_ID, COMPANY1_ID, JOB1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_create_job_without_company_in_body() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi api = new JobApi(adminClient);

    CrupdateJob job = someCreatableJob();
    job.setCompanyId(null);

    List<Job> created = api.crupdateJobs(ADMIN_ID, COMPANY1_ID, List.of(job));

    assertEquals(1, created.size());
    assertEquals(job.getId(), created.get(0).getId());
  }

  @Test
  @DirtiesContext
  void admin_can_delete_job() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi api = new JobApi(adminClient);

    CrupdateJob toCreate = someCreatableJob();
    String newJobId = toCreate.getId();
    api.crupdateJobs(ADMIN_ID, COMPANY1_ID, List.of(toCreate));

    api.deleteJobById(ADMIN_ID, COMPANY1_ID, newJobId);

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"Job with id " + newJobId + " not found\"}",
        () -> api.getJobById(ADMIN_ID, COMPANY1_ID, newJobId));
  }

  @Test
  @DirtiesContext
  void administration_can_assign_user_to_job() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi api = new JobApi(adminClient);

    api.assignUserToJob(USER1_ID, COMPANY1_ID, JOB1_ID);

    List<User> users = api.getJobResponsibleUsers(ADMIN_ID, COMPANY1_ID, JOB1_ID);
    assertTrue(users.stream().anyMatch(u -> USER1_ID.equals(u.getId())));
  }

  @Test
  void administration_can_get_job_responsible_users() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi api = new JobApi(adminClient);

    List<User> users = api.getJobResponsibleUsers(ADMIN_ID, COMPANY1_ID, JOB1_ID);
    assertNotNull(users);
  }

  @Test
  @DirtiesContext
  void administration_can_unassign_user_from_job() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi api = new JobApi(adminClient);

    api.assignUserToJob(USER1_ID, COMPANY1_ID, JOB1_ID);
    List<User> usersAfterAssign = api.getJobResponsibleUsers(ADMIN_ID, COMPANY1_ID, JOB1_ID);
    assertTrue(usersAfterAssign.stream().anyMatch(u -> USER1_ID.equals(u.getId())));

    api.unassignUserFromJob(USER1_ID, COMPANY1_ID, JOB1_ID);
    List<User> usersAfterUnassign = api.getJobResponsibleUsers(ADMIN_ID, COMPANY1_ID, JOB1_ID);
    assertTrue(usersAfterUnassign.stream().noneMatch(u -> USER1_ID.equals(u.getId())));
  }

  @Test
  void admin_cannot_create_job_with_end_before_start() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi api = new JobApi(adminClient);

    CrupdateJob invalidJob = someCreatableJob();
    invalidJob.setStartDate(java.time.LocalDate.now());
    invalidJob.setEndDate(java.time.LocalDate.now().minusDays(1));

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Job end date cannot be before start date\"}",
        () -> api.crupdateJobs(ADMIN_ID, COMPANY1_ID, List.of(invalidJob)));
  }

  @Test
  void getJobs_ShouldThrowBadRequest_WhenUserIdIsBlank() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi api = new JobApi(adminClient);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Path variable 'userId' must not be empty\"}",
        () -> api.getJobs(" ", COMPANY1_ID, 1, 100, null, null));
  }

  @Test
  void getJobs_ShouldThrowBadRequest_WhenCompanyIdIsBlank() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi api = new JobApi(adminClient);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Path variable 'companyId' must not be empty\"}",
        () -> api.getJobs(ADMIN_ID, " ", 1, 100, null, null));
  }

  @Test
  void getJobById_ShouldThrowBadRequest_WhenUserIdIsBlank() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi api = new JobApi(adminClient);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Path variable 'userId' must not be empty\"}",
        () -> api.getJobById(" ", COMPANY1_ID, JOB1_ID));
  }

  @Test
  void assignUserToJob_ShouldThrowBadRequest_WhenUserIdIsBlank() {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi api = new JobApi(adminClient);

    assertThrowsApiException(
        "{\"type\":\"400 BAD_REQUEST\",\"message\":\"Path variable 'userId' must not be empty\"}",
        () -> api.assignUserToJob(" ", COMPANY1_ID, JOB1_ID));
  }

  @Test
  @DirtiesContext
  void warehouse_can_assign_user_to_job_when_assigned() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi adminApi = new JobApi(adminClient);

    adminApi.assignUserToJob(WAREHOUSE_ID, COMPANY1_ID, JOB1_ID);

    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    JobApi warehouseApi = new JobApi(warehouseClient);

    warehouseApi.assignUserToJob(USER1_ID, COMPANY1_ID, JOB1_ID);

    List<User> users = adminApi.getJobResponsibleUsers(ADMIN_ID, COMPANY1_ID, JOB1_ID);
    assertTrue(users.stream().anyMatch(u -> USER1_ID.equals(u.getId())));
    assertTrue(users.stream().anyMatch(u -> WAREHOUSE_ID.equals(u.getId())));
  }

  @Test
  void warehouse_cannot_assign_user_to_job_when_not_assigned() {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    JobApi api = new JobApi(warehouseClient);

    assertThrowsApiException(
        "{\"type\":\"403 FORBIDDEN\",\"message\":\"Warehouse worker is not assigned to this job\"}",
        () -> api.assignUserToJob(USER1_ID, COMPANY1_ID, JOB1_ID));
  }

  @Test
  @DirtiesContext
  void warehouse_can_get_job_responsible_users_when_assigned() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi adminApi = new JobApi(adminClient);

    adminApi.assignUserToJob(WAREHOUSE_ID, COMPANY1_ID, JOB1_ID);

    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    JobApi warehouseApi = new JobApi(warehouseClient);

    List<User> users = warehouseApi.getJobResponsibleUsers(WAREHOUSE_ID, COMPANY1_ID, JOB1_ID);
    assertTrue(users.stream().anyMatch(u -> WAREHOUSE_ID.equals(u.getId())));
  }

  @Test
  void warehouse_cannot_get_job_responsible_users_when_not_assigned() {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    JobApi api = new JobApi(warehouseClient);

    assertThrowsApiException(
        "{\"type\":\"403 FORBIDDEN\",\"message\":\"Warehouse worker is not assigned to this job\"}",
        () -> api.getJobResponsibleUsers(WAREHOUSE_ID, COMPANY1_ID, JOB1_ID));
  }

  @Test
  @DirtiesContext
  void warehouse_can_unassign_user_from_job_when_assigned() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi adminApi = new JobApi(adminClient);

    adminApi.assignUserToJob(WAREHOUSE_ID, COMPANY1_ID, JOB1_ID);
    adminApi.assignUserToJob(USER1_ID, COMPANY1_ID, JOB1_ID);

    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    JobApi warehouseApi = new JobApi(warehouseClient);

    warehouseApi.unassignUserFromJob(USER1_ID, COMPANY1_ID, JOB1_ID);

    List<User> users = adminApi.getJobResponsibleUsers(ADMIN_ID, COMPANY1_ID, JOB1_ID);
    assertTrue(users.stream().anyMatch(u -> WAREHOUSE_ID.equals(u.getId())));
    assertTrue(users.stream().noneMatch(u -> USER1_ID.equals(u.getId())));
  }

  @Test
  void warehouse_cannot_unassign_user_from_job_when_not_assigned() {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    JobApi api = new JobApi(warehouseClient);

    assertThrowsApiException(
        "{\"type\":\"403 FORBIDDEN\",\"message\":\"Warehouse worker is not assigned to this job\"}",
        () -> api.unassignUserFromJob(USER1_ID, COMPANY1_ID, JOB1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
