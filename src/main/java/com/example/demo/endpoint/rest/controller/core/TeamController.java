package com.example.demo.endpoint.rest.controller.core;

import com.example.demo.client.model.CrupdateTeam;
import com.example.demo.client.model.Team;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.core.TeamMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.core.TeamService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TeamController {

  private final TeamService teamService;
  private final TeamMapper teamMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/teams/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public Team getTeamById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return teamMapper.toRestTeam(
        teamService
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Team with id " + id + " not found")));
  }

  @GetMapping("/users/{userId}/companies/{companyId}/teams")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public PaginatedResponse getTeams(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize,
      @RequestParam(name = "job_id", required = false) String jobId) {
    var result = teamService.findAll(page, pageSize, jobId);
    return new PaginatedResponse(
        teamMapper.toRestTeams(result.getContent()), (int) result.getTotalElements());
  }

  @PutMapping("/users/{userId}/companies/{companyId}/teams")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION', 'WAREHOUSE_WORKER')")
  public List<Team> crupdateTeams(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateTeam> toWrite) {
    List<com.example.demo.model.core.Team> saved =
        teamService.createOrUpdateAll(teamMapper.toDomain(toWrite));
    return teamMapper.toRestTeams(saved);
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/teams/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteTeamById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    teamService.deleteById(id);
  }
}
