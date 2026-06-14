package com.example.demo.service.core;

import static com.example.demo.repository.specification.SpecificationUtils.equal;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.Job;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.User;
import com.example.demo.model.core.Team;
import com.example.demo.model.exception.ForbiddenException;
import com.example.demo.repository.core.TeamRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

  private final TeamRepository teamRepository;
  private final ModificationUtils modificationUtils;

  public Optional<Team> findById(String id) {
    return teamRepository.findById(id);
  }

  public Page<Team> findAll(PageFromOne page, BoundedPageSize pageSize, String jobId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    Specification<Team> spec = Specification.where((root, query, cb) -> cb.conjunction());
    if (jobId != null) {
      spec = spec.and(equal(jobId, "job", "id"));
    }
    return teamRepository.findAll(spec, pageable);
  }

  @Transactional
  public List<Team> createOrUpdateAll(List<Team> teams) {
    User currentUser = modificationUtils.takePrimaryUser();
    List<Team> processedTeams = new ArrayList<>();

    for (Team team : teams) {
      Team existingTeam =
          team.getId() == null ? null : teamRepository.findById(team.getId()).orElse(null);

      if (currentUser.getRole() == User.Role.WAREHOUSE_WORKER) {
        Job existingJob = existingTeam != null ? existingTeam.getJob() : null;

        if (existingJob != null) {
          boolean isAssignedToExisting =
              existingJob.getResponsibleUsers().stream()
                  .anyMatch(u -> u.getId().equals(currentUser.getId()));
          if (!isAssignedToExisting) {
            throw new ForbiddenException(
                "Warehouse worker is not assigned to the job of this team");
          }
        }

        Job newJob = team.getJob();
        if (newJob != null) {
          boolean isAssignedToNew =
              newJob.getResponsibleUsers().stream()
                  .anyMatch(u -> u.getId().equals(currentUser.getId()));
          if (!isAssignedToNew) {
            throw new ForbiddenException("Warehouse worker is not assigned to the specified job");
          }
        }
      }

      modificationUtils.createOrUpdateModel(team, existingTeam, team.getId(), currentUser);
      processedTeams.add(team);
    }

    return teamRepository.saveAll(processedTeams);
  }

  @Transactional
  public void deleteById(String id) {
    teamRepository.deleteById(id);
  }
}
