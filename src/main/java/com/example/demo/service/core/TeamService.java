package com.example.demo.service.core;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.core.Team;
import com.example.demo.repository.core.TeamRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  public Page<Team> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return teamRepository.findAll(pageable);
  }

  @Transactional
  public List<Team> createOrUpdateAll(List<Team> teams) {
    return teamRepository.saveAll(teams);
  }

  @Transactional
  public void deleteById(String id) {
    teamRepository.deleteById(id);
  }
}
