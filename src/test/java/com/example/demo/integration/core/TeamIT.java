package com.example.demo.integration.core;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.TeamApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateTeam;
import com.example.demo.client.model.Team;
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
@ContextConfiguration(initializers = TeamIT.ContextInitializer.class)
class TeamIT {
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
  void admin_can_get_team_by_id() throws Exception {
    TeamApi api = new TeamApi(anApiClient(ADMIN_TOKEN));

    Team actual = api.getTeamById(ADMIN_ID, COMPANY1_ID, TEAM1_ID);
    Team expected = team1();
    expected.setCreatedAt(actual.getCreatedAt());
    expected.setUpdatedAt(actual.getUpdatedAt());
    expected.setCreatedBy(actual.getCreatedBy());
    expected.setUpdatedBy(actual.getUpdatedBy());
    expected.setComment(actual.getComment());

    assertEquals(expected, actual);
  }

  @Test
  void user_with_bad_token_cannot_get_team_by_id() {
    TeamApi api = new TeamApi(anApiClient(BAD_TOKEN));

    assertThrowsNotAuthorizedException(() -> api.getTeamById(ADMIN_ID, COMPANY1_ID, TEAM1_ID));
  }

  @Test
  void administration_can_get_all_teams() throws Exception {
    TeamApi api = new TeamApi(anApiClient(ADMINISTRATION_TOKEN));

    List<Team> teams = api.getTeams(ADMIN_ID, COMPANY1_ID, 1, 100);

    assertEquals(2, teams.size());
    assertTrue(teams.stream().anyMatch(t -> TEAM1_ID.equals(t.getId())));
    assertTrue(teams.stream().anyMatch(t -> TEAM2_ID.equals(t.getId())));
  }

  @Test
  void employee_cannot_get_all_teams() {
    TeamApi api = new TeamApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(() -> api.getTeams(EMPLOYEE_ID, COMPANY1_ID, 1, 100));
  }

  @Test
  @DirtiesContext
  void admin_can_update_team() throws Exception {
    TeamApi api = new TeamApi(anApiClient(ADMIN_TOKEN));

    CrupdateTeam toUpdate = teamToCrupdateTeam(team1());
    toUpdate.setName("Équipe chantier A mis à jour");

    List<Team> updated = api.crupdateTeams(ADMIN_ID, COMPANY1_ID, List.of(toUpdate));

    assertEquals(1, updated.size());
    assertEquals(TEAM1_ID, updated.get(0).getId());
    assertEquals("Équipe chantier A mis à jour", updated.get(0).getName());
  }

  @Test
  void employee_cannot_create_teams() {
    TeamApi api = new TeamApi(anApiClient(EMPLOYEE_TOKEN));

    assertThrowsForbiddenException(
        () -> api.crupdateTeams(EMPLOYEE_ID, COMPANY1_ID, List.of(someCreatableTeam())));
  }

  @Test
  void administration_cannot_delete_team() {
    TeamApi api = new TeamApi(anApiClient(ADMINISTRATION_TOKEN));

    assertThrowsForbiddenException(() -> api.deleteTeamById(ADMIN_ID, COMPANY1_ID, TEAM1_ID));
  }

  @Test
  @DirtiesContext
  void admin_can_delete_team() throws Exception {
    TeamApi api = new TeamApi(anApiClient(ADMIN_TOKEN));

    api.deleteTeamById(ADMIN_ID, COMPANY1_ID, TEAM1_ID);

    List<Team> teams = api.getTeams(ADMIN_ID, COMPANY1_ID, 1, 100);
    assertEquals(1, teams.size());
    assertEquals(TEAM2_ID, teams.get(0).getId());
  }

  @Test
  void admin_gets_not_found_when_team_does_not_exist() {
    TeamApi api = new TeamApi(anApiClient(ADMIN_TOKEN));

    assertThrowsApiException(
        "{\"type\":\"404 NOT_FOUND\",\"message\":\"Team with id nonexistent_team not found\"}",
        () -> api.getTeamById(ADMIN_ID, COMPANY1_ID, "nonexistent_team"));
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
