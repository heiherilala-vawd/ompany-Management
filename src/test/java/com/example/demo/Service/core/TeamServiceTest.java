package com.example.demo.Service.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.core.Team;
import com.example.demo.repository.core.TeamRepository;
import com.example.demo.service.core.TeamService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

  @Mock private TeamRepository teamRepository;

  @InjectMocks private TeamService teamService;

  private Team team;
  private String teamId;
  private User leader;

  @BeforeEach
  void setUp() {
    teamId = "team-123";
    leader = User.builder().id("user-1").email("leader@example.com").build();

    team = Team.builder().id(teamId).name("Alpha Team").leader(leader).build();
  }

  @Test
  void findById_ShouldReturnTeam_WhenExists() {
    when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

    Optional<Team> result = teamService.findById(teamId);

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(teamId);
    assertThat(result.get().getName()).isEqualTo("Alpha Team");
    verify(teamRepository).findById(teamId);
  }

  @Test
  void findById_ShouldReturnEmpty_WhenNotExists() {
    when(teamRepository.findById("unknown")).thenReturn(Optional.empty());

    Optional<Team> result = teamService.findById("unknown");

    assertThat(result).isEmpty();
    verify(teamRepository).findById("unknown");
  }

  @Test
  void findById_ShouldReturnEmpty_WhenIdIsNull() {
    when(teamRepository.findById(null)).thenReturn(Optional.empty());

    Optional<Team> result = teamService.findById(null);

    assertThat(result).isEmpty();
    verify(teamRepository).findById(null);
  }

  @Test
  void findAll_ShouldReturnPagedTeams() {
    PageFromOne page = new PageFromOne("1");
    BoundedPageSize pageSize = new BoundedPageSize("10");
    Page<Team> expectedPage = new PageImpl<>(List.of(team));

    when(teamRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

    Page<Team> result = teamService.findAll(page, pageSize);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getId()).isEqualTo(teamId);
    verify(teamRepository).findAll(any(Pageable.class));
  }

  @Test
  void findAll_ShouldReturnEmptyPage_WhenNoTeams() {
    PageFromOne page = new PageFromOne("1");
    BoundedPageSize pageSize = new BoundedPageSize("10");
    Page<Team> expectedPage = Page.empty();

    when(teamRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

    Page<Team> result = teamService.findAll(page, pageSize);

    assertThat(result.getContent()).isEmpty();
    verify(teamRepository).findAll(any(Pageable.class));
  }

  @Test
  void findAll_ShouldUseDefaults_WhenNullParams() {
    Page<Team> expectedPage = new PageImpl<>(List.of(team));

    when(teamRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

    Page<Team> result = teamService.findAll(null, null);

    assertThat(result.getContent()).hasSize(1);
    verify(teamRepository).findAll(any(Pageable.class));
  }

  @Test
  void createOrUpdateAll_ShouldSaveAllTeams() {
    List<Team> teams = List.of(team);

    when(teamRepository.saveAll(anyList())).thenReturn(teams);

    List<Team> result = teamService.createOrUpdateAll(teams);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(teamId);
    verify(teamRepository).saveAll(teams);
  }

  @Test
  void createOrUpdateAll_ShouldHandleMultipleTeams() {
    Team team2 = Team.builder().id("team-456").name("Beta Team").leader(leader).build();
    List<Team> teams = List.of(team, team2);

    when(teamRepository.saveAll(anyList())).thenReturn(teams);

    List<Team> result = teamService.createOrUpdateAll(teams);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo("team-123");
    assertThat(result.get(1).getId()).isEqualTo("team-456");
    verify(teamRepository).saveAll(teams);
  }

  @Test
  void createOrUpdateAll_ShouldReturnEmptyList_WhenInputIsEmpty() {
    List<Team> result = teamService.createOrUpdateAll(List.of());

    assertThat(result).isEmpty();
    verify(teamRepository).saveAll(List.of());
  }

  @Test
  void createOrUpdateAll_ShouldHandleTeamWithNullId() {
    Team newTeam = Team.builder().id(null).name("New Team").leader(leader).build();
    List<Team> teams = List.of(newTeam);

    when(teamRepository.saveAll(anyList())).thenReturn(List.of(newTeam));

    List<Team> result = teamService.createOrUpdateAll(teams);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isNull();
    assertThat(result.get(0).getName()).isEqualTo("New Team");
    verify(teamRepository).saveAll(teams);
  }

  @Test
  void deleteById_ShouldDelete_WhenExists() {
    doNothing().when(teamRepository).deleteById(teamId);

    teamService.deleteById(teamId);

    verify(teamRepository).deleteById(teamId);
  }

  @Test
  void deleteById_ShouldHandleNullId() {
    doNothing().when(teamRepository).deleteById(null);

    teamService.deleteById(null);

    verify(teamRepository).deleteById(null);
  }
}
