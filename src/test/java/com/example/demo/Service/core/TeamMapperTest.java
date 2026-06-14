package com.example.demo.Service.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.demo.client.model.CrupdateTeam;
import com.example.demo.client.model.User;
import com.example.demo.endpoint.rest.mapper.UserMapper;
import com.example.demo.endpoint.rest.mapper.core.TeamMapper;
import com.example.demo.service.JobService;
import com.example.demo.service.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamMapperTest {

  @Mock private UserService userService;
  @Mock private UserMapper userMapper;
  @Mock private JobService jobService;

  @InjectMocks private TeamMapper teamMapper;

  private com.example.demo.model.User domainLeader;
  private com.example.demo.model.User domainMember;
  private User restLeader;
  private User restMember;

  @BeforeEach
  void setUp() {
    domainLeader =
        com.example.demo.model.User.builder()
            .id("user-1")
            .firstName("John")
            .lastName("Doe")
            .email("john@example.com")
            .build();

    domainMember =
        com.example.demo.model.User.builder()
            .id("user-2")
            .firstName("Jane")
            .lastName("Smith")
            .email("jane@example.com")
            .build();

    restLeader = new User();
    restLeader.setId("user-1");
    restLeader.setFirstName("John");
    restLeader.setLastName("Doe");
    restLeader.setEmail("john@example.com");

    restMember = new User();
    restMember.setId("user-2");
    restMember.setFirstName("Jane");
    restMember.setLastName("Smith");
    restMember.setEmail("jane@example.com");
  }

  // ========== toDomain(CrupdateTeam) ==========

  private com.example.demo.model.Job domainJob;

  @BeforeEach
  void setUpJob() {
    domainJob = com.example.demo.model.Job.builder().id("job-1").build();
  }

  @Test
  void toDomain_ShouldMapAllFields() {
    CrupdateTeam rest =
        new CrupdateTeam()
            .id("team-1")
            .name("Alpha Team")
            .leaderId("user-1")
            .jobId("job-1")
            .memberIds(List.of("user-2"))
            .comment("Team comment");

    when(userService.getById("user-1")).thenReturn(domainLeader);
    when(userService.getById("user-2")).thenReturn(domainMember);
    when(jobService.findById("job-1")).thenReturn(Optional.of(domainJob));

    com.example.demo.model.core.Team result = teamMapper.toDomain(rest);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("team-1");
    assertThat(result.getName()).isEqualTo("Alpha Team");
    assertThat(result.getLeader()).isEqualTo(domainLeader);
    assertThat(result.getJob()).isEqualTo(domainJob);
    assertThat(result.getMembers()).hasSize(1);
    assertThat(result.getMembers().get(0)).isEqualTo(domainMember);
    assertThat(result.getComment()).isEqualTo("Team comment");
    verify(userService).getById("user-1");
    verify(userService).getById("user-2");
    verify(jobService).findById("job-1");
  }

  @Test
  void toDomain_ShouldHandleNullJobId() {
    CrupdateTeam rest =
        new CrupdateTeam()
            .id("team-1")
            .name("Alpha Team")
            .leaderId("user-1")
            .jobId(null)
            .memberIds(List.of("user-2"));

    when(userService.getById("user-1")).thenReturn(domainLeader);
    when(userService.getById("user-2")).thenReturn(domainMember);

    com.example.demo.model.core.Team result = teamMapper.toDomain(rest);

    assertThat(result).isNotNull();
    assertThat(result.getJob()).isNull();
  }

  @Test
  void toDomain_ShouldReturnNull_WhenInputIsNull() {
    com.example.demo.model.core.Team result = teamMapper.toDomain((CrupdateTeam) null);
    assertThat(result).isNull();
  }

  @Test
  void toDomain_ShouldHandleNullId() {
    CrupdateTeam rest = new CrupdateTeam().id(null).name("New Team").leaderId("user-1");

    when(userService.getById("user-1")).thenReturn(domainLeader);

    com.example.demo.model.core.Team result = teamMapper.toDomain(rest);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNull();
    assertThat(result.getName()).isEqualTo("New Team");
  }

  @Test
  void toDomain_ShouldHandleNullLeaderId() {
    CrupdateTeam rest = new CrupdateTeam().id("team-1").name("Alpha Team").leaderId(null);

    com.example.demo.model.core.Team result = teamMapper.toDomain(rest);

    assertThat(result).isNotNull();
    assertThat(result.getLeader()).isNull();
    verify(userService, never()).getById(anyString());
  }

  @Test
  void toDomain_ShouldHandleNullMemberIds() {
    CrupdateTeam rest =
        new CrupdateTeam().id("team-1").name("Alpha Team").leaderId("user-1").memberIds(null);

    when(userService.getById("user-1")).thenReturn(domainLeader);

    com.example.demo.model.core.Team result = teamMapper.toDomain(rest);

    assertThat(result).isNotNull();
    assertThat(result.getLeader()).isEqualTo(domainLeader);
    assertThat(result.getMembers()).isEmpty();
    verify(userService).getById("user-1");
  }

  @Test
  void toDomain_ShouldHandleEmptyMemberIds() {
    CrupdateTeam rest =
        new CrupdateTeam().id("team-1").name("Alpha Team").leaderId("user-1").memberIds(List.of());

    when(userService.getById("user-1")).thenReturn(domainLeader);

    com.example.demo.model.core.Team result = teamMapper.toDomain(rest);

    assertThat(result).isNotNull();
    assertThat(result.getMembers()).isEmpty();
  }

  @Test
  void toDomain_ShouldFilterNullMemberIds() {
    CrupdateTeam rest =
        new CrupdateTeam()
            .id("team-1")
            .name("Alpha Team")
            .leaderId("user-1")
            .memberIds(java.util.Arrays.asList("user-2", null));

    when(userService.getById("user-1")).thenReturn(domainLeader);
    when(userService.getById("user-2")).thenReturn(domainMember);

    com.example.demo.model.core.Team result = teamMapper.toDomain(rest);

    assertThat(result).isNotNull();
    assertThat(result.getMembers()).hasSize(1);
    assertThat(result.getMembers().get(0)).isEqualTo(domainMember);
  }

  @Test
  void toDomain_ShouldHandleNullName() {
    CrupdateTeam rest = new CrupdateTeam().id("team-1").name(null).leaderId("user-1");

    when(userService.getById("user-1")).thenReturn(domainLeader);

    com.example.demo.model.core.Team result = teamMapper.toDomain(rest);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isNull();
  }

  // ========== toDomain(List<CrupdateTeam>) ==========

  @Test
  void toDomainList_ShouldMapAll() {
    CrupdateTeam rest = new CrupdateTeam().id("team-1").name("Alpha Team").leaderId("user-1");

    when(userService.getById("user-1")).thenReturn(domainLeader);

    List<com.example.demo.model.core.Team> result = teamMapper.toDomain(List.of(rest));

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo("team-1");
  }

  @Test
  void toDomainList_ShouldReturnEmpty_WhenInputIsEmpty() {
    List<com.example.demo.model.core.Team> result = teamMapper.toDomain(List.of());
    assertThat(result).isEmpty();
  }

  @Test
  void toDomainList_ShouldMapEachElement() {
    when(userService.getById("user-1")).thenReturn(domainLeader);

    List<com.example.demo.model.core.Team> result =
        teamMapper.toDomain(List.of(new CrupdateTeam().id("team-1").name("A").leaderId("user-1")));

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo("team-1");
    verify(userService).getById("user-1");
  }

  // ========== toRestTeam ==========

  @Test
  void toRestTeam_ShouldMapAllFields() {
    com.example.demo.model.core.Team domain =
        com.example.demo.model.core.Team.builder()
            .id("team-1")
            .name("Alpha Team")
            .leader(domainLeader)
            .job(domainJob)
            .members(List.of(domainMember))
            .build();

    when(userMapper.toRestUser(domainLeader)).thenReturn(restLeader);
    when(userMapper.toRestUser(domainMember)).thenReturn(restMember);

    com.example.demo.client.model.Team result = teamMapper.toRestTeam(domain);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("team-1");
    assertThat(result.getName()).isEqualTo("Alpha Team");
    assertThat(result.getLeader()).isNotNull();
    assertThat(result.getLeader().getId()).isEqualTo("user-1");
    assertThat(result.getJobId()).isEqualTo("job-1");
    assertThat(result.getMembers()).hasSize(1);
    assertThat(result.getMembers().get(0).getId()).isEqualTo("user-2");
    verify(userMapper).toRestUser(domainLeader);
    verify(userMapper).toRestUser(domainMember);
  }

  @Test
  void toRestTeam_ShouldHandleNullJob() {
    com.example.demo.model.core.Team domain =
        com.example.demo.model.core.Team.builder()
            .id("team-1")
            .name("Alpha Team")
            .leader(domainLeader)
            .job(null)
            .build();

    when(userMapper.toRestUser(domainLeader)).thenReturn(restLeader);

    com.example.demo.client.model.Team result = teamMapper.toRestTeam(domain);

    assertThat(result).isNotNull();
    assertThat(result.getJobId()).isNull();
  }

  @Test
  void toRestTeam_ShouldReturnNull_WhenInputIsNull() {
    com.example.demo.client.model.Team result = teamMapper.toRestTeam(null);
    assertThat(result).isNull();
  }

  @Test
  void toRestTeam_ShouldHandleNullLeader() {
    com.example.demo.model.core.Team domain = new com.example.demo.model.core.Team();
    domain.setId("team-1");
    domain.setName("Alpha Team");
    domain.setMembers(List.of(domainMember));

    when(userMapper.toRestUser(domainMember)).thenReturn(restMember);

    com.example.demo.client.model.Team result = teamMapper.toRestTeam(domain);

    assertThat(result).isNotNull();
    assertThat(result.getLeader()).isNull();
    assertThat(result.getMembers()).hasSize(1);
    verify(userMapper, never()).toRestUser(null);
    verify(userMapper).toRestUser(domainMember);
  }

  @Test
  void toRestTeam_ShouldHandleEmptyMembers() {
    com.example.demo.model.core.Team domain =
        com.example.demo.model.core.Team.builder()
            .id("team-1")
            .name("Alpha Team")
            .leader(domainLeader)
            .members(List.of())
            .build();

    when(userMapper.toRestUser(domainLeader)).thenReturn(restLeader);

    com.example.demo.client.model.Team result = teamMapper.toRestTeam(domain);

    assertThat(result).isNotNull();
    assertThat(result.getLeader()).isNotNull();
    assertThat(result.getMembers()).isEmpty();
    verify(userMapper).toRestUser(domainLeader);
  }

  @Test
  void toRestTeam_ShouldHandleNullName() {
    com.example.demo.model.core.Team domain =
        com.example.demo.model.core.Team.builder()
            .id("team-1")
            .name(null)
            .leader(domainLeader)
            .build();

    when(userMapper.toRestUser(domainLeader)).thenReturn(restLeader);

    com.example.demo.client.model.Team result = teamMapper.toRestTeam(domain);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isNull();
  }

  @Test
  void toRestTeam_ShouldHandleNullId() {
    com.example.demo.model.core.Team domain =
        com.example.demo.model.core.Team.builder()
            .id(null)
            .name("No ID Team")
            .leader(domainLeader)
            .build();

    when(userMapper.toRestUser(domainLeader)).thenReturn(restLeader);

    com.example.demo.client.model.Team result = teamMapper.toRestTeam(domain);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNull();
  }

  // ========== toRestTeams ==========

  @Test
  void toRestTeams_ShouldMapList() {
    com.example.demo.model.core.Team domain =
        com.example.demo.model.core.Team.builder()
            .id("team-1")
            .name("Alpha Team")
            .leader(domainLeader)
            .build();

    when(userMapper.toRestUser(domainLeader)).thenReturn(restLeader);

    List<com.example.demo.client.model.Team> result = teamMapper.toRestTeams(List.of(domain));

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo("team-1");
  }

  @Test
  void toRestTeams_ShouldReturnEmpty_WhenInputIsEmpty() {
    List<com.example.demo.client.model.Team> result = teamMapper.toRestTeams(List.of());
    assertThat(result).isEmpty();
  }

  @Test
  void toRestTeams_ShouldHandleMultipleTeams() {
    com.example.demo.model.core.Team team1 =
        com.example.demo.model.core.Team.builder()
            .id("team-1")
            .name("Alpha")
            .leader(domainLeader)
            .build();
    com.example.demo.model.core.Team team2 =
        com.example.demo.model.core.Team.builder()
            .id("team-2")
            .name("Beta")
            .leader(domainLeader)
            .build();

    when(userMapper.toRestUser(domainLeader)).thenReturn(restLeader);

    List<com.example.demo.client.model.Team> result = teamMapper.toRestTeams(List.of(team1, team2));

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo("team-1");
    assertThat(result.get(1).getId()).isEqualTo("team-2");
    verify(userMapper, times(2)).toRestUser(domainLeader);
  }
}
