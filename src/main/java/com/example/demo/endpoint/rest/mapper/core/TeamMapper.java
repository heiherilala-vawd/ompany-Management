package com.example.demo.endpoint.rest.mapper.core;

import com.example.demo.client.model.CrupdateTeam;
import com.example.demo.client.model.Team;
import com.example.demo.endpoint.rest.mapper.UserMapper;
import com.example.demo.service.JobService;
import com.example.demo.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TeamMapper {

  private final UserService userService;
  private final UserMapper userMapper;
  private final JobService jobService;

  public com.example.demo.model.core.Team toDomain(CrupdateTeam restTeam) {
    if (restTeam == null) return null;

    var builder =
        com.example.demo.model.core.Team.builder()
            .id(restTeam.getId())
            .name(restTeam.getName())
            .comment(restTeam.getComment());

    if (restTeam.getLeaderId() != null) {
      builder.leader(userService.getById(restTeam.getLeaderId()));
    }

    if (restTeam.getJobId() != null) {
      builder.job(jobService.findById(restTeam.getJobId()).orElse(null));
    }

    if (restTeam.getMemberIds() != null) {
      builder.members(
          restTeam.getMemberIds().stream()
              .filter(id -> id != null)
              .map(userService::getById)
              .collect(Collectors.toList()));
    }

    return builder.build();
  }

  public List<com.example.demo.model.core.Team> toDomain(List<CrupdateTeam> restTeams) {
    return restTeams.stream().map(this::toDomain).collect(Collectors.toList());
  }

  public Team toRestTeam(com.example.demo.model.core.Team domainTeam) {
    if (domainTeam == null) return null;

    Team restTeam = new Team();
    restTeam.setId(domainTeam.getId());
    restTeam.setName(domainTeam.getName());
    if (domainTeam.getLeader() != null) {
      restTeam.setLeader(userMapper.toRestUser(domainTeam.getLeader()));
    }
    if (domainTeam.getJob() != null) {
      restTeam.setJobId(domainTeam.getJob().getId());
    }

    if (domainTeam.getMembers() != null) {
      restTeam.setMembers(
          domainTeam.getMembers().stream()
              .map(userMapper::toRestUser)
              .collect(Collectors.toList()));
    }
    return restTeam;
  }

  public List<Team> toRestTeams(List<com.example.demo.model.core.Team> domainTeams) {
    return domainTeams.stream().map(this::toRestTeam).collect(Collectors.toList());
  }
}
