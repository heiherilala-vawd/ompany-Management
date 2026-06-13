package com.example.demo.integration;

import static com.example.demo.integration.conf.TestHistoryFixtures.*;
import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.HistoryApi;
import com.example.demo.client.api.UsersApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateUser;
import com.example.demo.client.model.EntityType;
import com.example.demo.client.model.History;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestDataSqlLoader;
import com.example.demo.integration.conf.TestUtils;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = HistoryIT.ContextInitializer.class)
class HistoryIT {
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
  void admin_can_get_all_histories() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    HistoryApi api = new HistoryApi(adminClient);
    UsersApi usersApi = new UsersApi(adminClient);

    List<History> histories = api.getHistories(1, 100, null, null, null, null, null);

    assertEquals(5, histories.size());
    CrupdateUser newUser1 = userToCrupdateUser(admin1());
    String newLastName = "new last name";
    newUser1.setLastName(newLastName);

    usersApi.crupdateUsers(COMPANY1_ID, List.of(newUser1));
    List<History> histories2 = api.getHistories(1, 100, null, null, null, null, null);
    assertEquals(6, histories2.size());
  }

  @Test
  void admin_can_paginate_histories_with_page_size() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    HistoryApi api = new HistoryApi(adminClient);

    List<History> histories = api.getHistories(1, 2, null, null, null, null, null);

    assertEquals(2, histories.size());
  }

  @Test
  void admin_can_get_second_page_of_histories() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    HistoryApi api = new HistoryApi(adminClient);

    List<History> histories = api.getHistories(2, 2, null, null, null, null, null);

    assertEquals(2, histories.size());
  }

  @Test
  void administration_can_get_all_histories() throws Exception {
    ApiClient administrationClient = anApiClient(ADMINISTRATION_TOKEN);
    HistoryApi api = new HistoryApi(administrationClient);

    List<History> histories = api.getHistories(1, 100, null, null, null, null, null);

    assertEquals(5, histories.size());
  }

  @Test
  void employee_cannot_get_histories() {
    ApiClient employeeClient = anApiClient(EMPLOYEE_TOKEN);
    HistoryApi api = new HistoryApi(employeeClient);

    assertThrowsForbiddenException(() -> api.getHistories(1, 100, null, null, null, null, null));
  }

  @Test
  void warehouse_cannot_get_histories() {
    ApiClient warehouseClient = anApiClient(WAREHOUSE_TOKEN);
    HistoryApi api = new HistoryApi(warehouseClient);

    assertThrowsForbiddenException(() -> api.getHistories(1, 100, null, null, null, null, null));
  }

  @Test
  void user_with_bad_token_cannot_get_histories() {
    ApiClient badClient = anApiClient(BAD_TOKEN);
    HistoryApi api = new HistoryApi(badClient);

    assertThrowsNotAuthorizedException(
        () -> api.getHistories(1, 100, null, null, null, null, null));
  }

  @Test
  void admin_can_filter_histories_by_user_id() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    HistoryApi api = new HistoryApi(adminClient);

    List<History> histories = api.getHistories(1, 100, ADMIN_ID, null, null, null, null);

    assertEquals(3, histories.size());
    assertTrue(histories.stream().allMatch(h -> ADMIN_ID.equals(h.getUserId())));
  }

  @Test
  void admin_can_filter_histories_by_entity_type() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    HistoryApi api = new HistoryApi(adminClient);

    List<History> histories = api.getHistories(1, 100, null, EntityType.COMPANY, null, null, null);

    assertEquals(2, histories.size());
    assertTrue(histories.stream().allMatch(h -> EntityType.COMPANY.equals(h.getEntityType())));
  }

  @Test
  void admin_can_filter_histories_by_entity_id() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    HistoryApi api = new HistoryApi(adminClient);

    List<History> histories = api.getHistories(1, 100, null, null, COMPANY1_ID, null, null);

    assertEquals(2, histories.size());
    assertTrue(histories.stream().allMatch(h -> COMPANY1_ID.equals(h.getEntityId())));
  }

  @Test
  void admin_can_filter_histories_by_date_range() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    HistoryApi api = new HistoryApi(adminClient);

    OffsetDateTime dateFrom = OffsetDateTime.of(2024, 2, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    OffsetDateTime dateTo = OffsetDateTime.of(2024, 2, 28, 23, 59, 59, 0, ZoneOffset.UTC);

    List<History> histories =
        api.getHistories(1, 100, null, null, null, dateFrom.toInstant(), dateTo.toInstant());

    assertEquals(3, histories.size());
  }

  @Test
  void admin_gets_empty_history_list_when_filters_match_nothing() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    HistoryApi api = new HistoryApi(adminClient);

    List<History> histories =
        api.getHistories(1, 100, null, EntityType.BANK_FEE, COMPANY1_ID, null, null);

    assertTrue(histories.isEmpty());
  }

  @Test
  void admin_can_combine_multiple_filters() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    HistoryApi api = new HistoryApi(adminClient);

    List<History> histories =
        api.getHistories(1, 100, ADMIN_ID, EntityType.COMPANY, COMPANY1_ID, null, null);

    assertEquals(2, histories.size());
    assertTrue(
        histories.stream()
            .allMatch(
                h ->
                    ADMIN_ID.equals(h.getUserId())
                        && EntityType.COMPANY.equals(h.getEntityType())
                        && COMPANY1_ID.equals(h.getEntityId())));
  }

  @Test
  void admin_can_get_history_with_correct_data() throws Exception {
    ApiClient adminClient = anApiClient(ADMIN_TOKEN);
    HistoryApi api = new HistoryApi(adminClient);

    List<History> histories = api.getHistories(1, 100, null, EntityType.JOB, JOB1_ID, null, null);

    assertEquals(1, histories.size());
    History actual = histories.get(0);
    History expected = history3();

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getPreviousValue(), actual.getPreviousValue());
    assertEquals(expected.getNewValue(), actual.getNewValue());
    assertEquals(expected.getUserId(), actual.getUserId());
    assertEquals(expected.getEntityType(), actual.getEntityType());
    assertEquals(expected.getEntityId(), actual.getEntityId());
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
