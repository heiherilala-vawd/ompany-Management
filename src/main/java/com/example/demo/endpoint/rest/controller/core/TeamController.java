package com.example.demo.endpoint.rest.controller.core;

import com.example.demo.client.model.CrupdateTeam;
import com.example.demo.client.model.Team;
import com.example.demo.endpoint.rest.mapper.core.TeamMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.core.TeamService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TeamController {

  private final TeamService teamService;
  private final TeamMapper teamMapper;

  @GetMapping("/companies/{comp_id}/teams/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public Team getTeamById(@PathVariable String comp_id, @PathVariable String id) {
    return teamMapper.toRestTeam(
        teamService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Team with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/teams")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Team> getTeams(
      @PathVariable String comp_id,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    return teamMapper.toRestTeams(teamService.findAll(page, pageSize).getContent());
  }

  @PutMapping("/companies/{comp_id}/teams")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Team> crupdateTeams(
      @PathVariable String comp_id, @RequestBody List<CrupdateTeam> toWrite) {
    List<com.example.demo.model.core.Team> saved =
        teamService.createOrUpdateAll(teamMapper.toDomain(toWrite));
    return teamMapper.toRestTeams(saved);
  }

  @DeleteMapping("/companies/{comp_id}/teams/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteTeamById(@PathVariable String comp_id, @PathVariable String id) {
    teamService.deleteById(id);
  }
}
