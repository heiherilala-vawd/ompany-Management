package com.example.demo.integration;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.JobApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateJob;
import com.example.demo.client.model.Job;
import com.example.demo.client.model.JobStatus;
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

    Job actual = api.getJobById(COMPANY1_ID, JOB1_ID);
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

    assertThrowsNotAuthorizedException(() -> api.getJobById(COMPANY1_ID, JOB1_ID));
  }

  @Test
  void admin_can_get_all_jobs() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    JobApi api = new JobApi(adminClient);

    List<Job> jobs = api.getJobs(COMPANY1_ID, 1, 100, null, null, null);

    assertEquals(2, jobs.size());
    assertTrue(jobs.stream().anyMatch(job -> JOB1_ID.equals(job.getId())));
    assertTrue(jobs.stream().anyMatch(job -> JOB2_ID.equals(job.getId())));
  }

  @Test
  void employee_cannot_get_all_jobs() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    JobApi api = new JobApi(employeeClient);

    assertThrowsForbiddenException(() -> api.getJobs(COMPANY1_ID, 1, 100, null, null, null));
  }

  @Test
  void warehouse_can_filter_jobs_by_status() throws Exception {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    JobApi api = new JobApi(warehouseClient);

    List<Job> jobs = api.getJobs(COMPANY1_ID, 1, 100, JobStatus.IN_PROGRESS, null, null);

    assertEquals(1, jobs.size());
    assertEquals(JOB1_ID, jobs.get(0).getId());
  }

  @Test
  void administration_can_filter_jobs_by_company_id() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    JobApi api = new JobApi(administrationClient);

    List<Job> jobs = api.getJobs(COMPANY1_ID, 1, 100, null, COMPANY2_ID, null);

    assertEquals(1, jobs.size());
    assertEquals(JOB2_ID, jobs.get(0).getId());
  }

  @Test
  void administration_can_filter_jobs_by_description() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    JobApi api = new JobApi(administrationClient);

    List<Job> jobs = api.getJobs(COMPANY1_ID, 1, 100, null, null, "bâtiment A");

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

    List<Job> updatedJobs = api.crupdateJobs(COMPANY1_ID, List.of(jobToUpdate));
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
        () -> api.crupdateJobs(COMPANY1_ID, List.of(someCreatableJob())));
  }

  @Test
  void administration_cannot_delete_job() {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    JobApi api = new JobApi(administrationClient);

    assertThrowsForbiddenException(() -> api.deleteJobById(COMPANY1_ID, JOB1_ID));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
